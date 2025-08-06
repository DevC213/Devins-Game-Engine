package com.gamelogic.gameflow;

import com.gamelogic.commands.Keybindings;
import com.gamelogic.map.*;
import com.gamelogic.map.mapLogic.MapController;
import com.gamelogic.messaging.Messenger;
import com.gamelogic.villages.House;
import com.recoveryitems.RecoveryItem;
import com.savesystem.PlayerState;
import javafx.collections.ObservableList;

import java.util.Objects;

public class GameController implements IUpdateMinimap, IUpdateGame {


    private MapController currentMapController;
    private final GameProgressController gameProgressController;
    private final MapSwitchingController  mapSwitchingController;
    private final GameProgression gameProgression;
    private final ClassController classController;
    Coordinates mainMapLocation;
    private int moves = 0;
    private int deepestLevel = 0;
    private String currentVillage = "";
    private boolean inHouse = false;
    //Constructor
    public GameController(Keybindings keybindings) {
        classController = new ClassController(keybindings,this);
        currentMapController = classController.currentMapController;
        gameProgressController = new GameProgressController(this.classController);
        mapSwitchingController = new MapSwitchingController(this.classController);
        gameProgression = new GameProgression(this.classController);
    }
    public void respawn() {
        gameProgressController.respawn();
        updateGameInfo();
        renderMinimap();
    }

    public int getID() {
        return currentMapController.getID();
    }

    public int getLevel() {
        return currentMapController.getLevel();
    }

    public void loadMapFromID(int currentMapID) {
        switchMap(currentMapID);
        Coordinates coordinates = mapSwitchingController.loadMapFromID(currentMapID, currentMapController);
        if(coordinates != null) {
            mainMapLocation = coordinates;
        }
        updateGameInfo();
        renderMinimap();
    }
    public void setLevel(int level) {
        currentMapController.setLevel(level);
    }

    public void setDeepestLevel(int deepestLevel) {
        this.deepestLevel = deepestLevel;
        gameProgression.setDeepestLevel(deepestLevel);
    }

    public double tileHealthData(Coordinates location) {
        return classController.tileKeyMap.get(currentMapController.getMapValue(location)).healthDelta();
    }
    public void handleInput(String keyPressed) {
        if(classController.playerController.isGameOver()){return;}
        classController.commandProcessor.handleKeyInput(keyPressed);
        Movement move = Movement.getmovement(keyPressed);
        if (!move.equals(Movement.DEFAULT)) {
            moves++;
            spawnMonster();
        }
    }
    public void attackMonster(String monster){
        classController.commandProcessor.attackMonster(monster);
    }
    public void monsterAttack(){
        classController.commandProcessor.monstersTurn();
    }
    //dialog
    public void intro() {
        gameProgressController.intro();
        renderMinimap();
        updateGameInfo();
    }
    public void resetGame() {
        gameProgressController.resetGame(currentMapController);
        inHouse = false;
    }

    //IUpdateMinimap
    @Override
    public void renderMinimap() {

        enterHouse();
        leaveHouse();
        classController.uiMapController.minimap(ClassController.mainGameController, currentMapController, classController.playerController);
    }

    private void leaveHouse() {
        if(currentMapController instanceof IExitCoordinates exitCoordinates){
            Coordinates exit = exitCoordinates.getExitCoordinates();
            Coordinates player = classController.playerController.getMapCoordinates();
            if(player.equals(exit)){
                currentMapController = mapSwitchingController.returnToMainMap(mainMapLocation);
            }
        }
    }

    private void enterHouse() {
        if(inHouse){
            mainMapLocation = classController.playerController.getMapCoordinates();
            House house = currentMapController.getHouse(mainMapLocation, currentVillage);
            currentMapController = mapSwitchingController.switchMap(house.getID());
            inHouse = false;
        }
    }

    @Override
    public void toggleHouse() {
        inHouse = !inHouse;
    }
    @Override
    public void setVisibility(int visibility) {
        classController.uiMapController.setVisibility(visibility);
    }
    @Override
    public void setDirection(int deltaX, int deltaY) {
        if(deltaY > 0){
            classController.uiMapController.setDirection("down");
        }else if(deltaY < 0){
            classController. uiMapController.setDirection("up");
        } else if(deltaX > 0){
            classController.uiMapController.setDirection("right");
        } else{
            classController.uiMapController.setDirection("left");
        }
    }

    public void switchMap(int ID) {
        currentMapController = mapSwitchingController.switchMap(ID);
    }
    public void setHealth() {
        classController.playerController.setHealth(classController.uiMapController.getPlayerHealth());
    }
    public void setCharacter(String character) {
        classController.uiMapController.setCharacterID(character);
    }
    private void spawnMonster() {
        Messenger messenger = currentMapController.spawnMonsters(classController.playerController.getMapCoordinates(), moves);
        if (messenger != null && messenger.getMessage() != null) {
            ClassController.mainGameController.UIUpdate(messenger.getMessage(), 0);
            moves = 0;
        }
    }

    //progression
    private void Victory(){
        classController.playerController.toggleGameOver();
        ClassController.mainGameController.GameOver(true);
    }
    @Override
    public void updateGameInfo() {
        if(classController.playerController.isGameOver()){return;}
        ClassController.mainGameController.UIUpdate("(" + classController.playerController.getDisplayCoordinates().x() + "," + classController.playerController.getDisplayCoordinates().y() + ")", 2);
        checkEnvironment();
        checkProgression();
    }

    private void checkProgression() {
        int level = currentMapController.getLevel();
        if(currentMapController.progressesGame()){
            gameProgression.levelProgression(level, currentMapController);
            if(level > deepestLevel){
                deepestLevel = level;
            }
        }
        String village = gameProgression.checkProgression(currentMapController);
        if(village != null) {
            currentVillage = village;
            classController.environmentChecker.checkNPC(currentVillage, classController.playerController.getMapCoordinates());
        }
        if(!currentMapController.inVillage()){
            currentVillage = "";
        }
        if(!currentVillage.isEmpty()){
            classController.environmentChecker.checkNPC(currentVillage, classController.playerController.getMapCoordinates());
        }
    }

    private void checkEnvironment() {
        TileKey tile = classController.tileKeyMap.get(currentMapController.getMapValue(classController.playerController.getMapCoordinates()));
        if(Objects.equals(tile.name(), "goal")) {
            Victory();
        }
        double effect =  tileHealthData(classController.playerController.getMapCoordinates());
        classController.environmentChecker.checkTile(classController.combatSystem, classController.commandProcessor, effect);

    }

    public int getDeepestLevel() {
        return deepestLevel;
    }
    public PlayerState getPlayerState(){
        return classController.playerController.createPlayerState();
    }
    public void loadData(PlayerState playerState) {
        classController.playerController.loadFromPlayerState(playerState);
        updateGameInfo();
        renderMinimap();
    }

    public void useRecoveryItem(RecoveryItem item) {
        classController.playerController.useHealing(item.getName());
    }
    public ObservableList<RecoveryItem> getRecoveryItems() {
        return classController.playerController.getRecoveryItems();
    }
    public void AOE() {
        classController.commandProcessor.AOEAttack();
    }
}

