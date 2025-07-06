package com.gamelogic.gameflow;

import com.gamelogic.commands.Keybindings;
import com.gamelogic.core.*;
import com.gamelogic.map.*;
import com.gamelogic.map.mapLogic.MapController;
import com.gamelogic.messaging.Messenger;
import com.gamelogic.villages.House;
import com.savesystem.PlayerState;

import java.util.Objects;

public class GameController implements IUpdateMinimap, IUpdateGame {


    private MapController currentMapController;
    private final GameProgressController gameProgressController;
    private final MapSwitchingController  mapSwitchingController;
    private final GameProgression gameProgression;
    private Difficulty difficulty;
    private final ClassController classController;
    Coordinates mainMapLocation;
    private int moves = 0;
    private int deepestLevel = 0;
    private String currentVillage;
    private boolean inHouse = false;
    //Constructor
    public GameController(MainGameController mainGameController, Keybindings keybindings) {
        classController = new ClassController(mainGameController,keybindings,this, difficulty);
        currentMapController = classController.currentMapController;
        gameProgressController = new GameProgressController(this.classController);
        mapSwitchingController = new MapSwitchingController(this.classController);
        gameProgression = new GameProgression(this.classController);
    }
    public void setDifficulty(String difficulty) {
        switch(difficulty.toLowerCase()) {
            case "normal" -> this.difficulty = Difficulty.NORMAL;
            case "daredevil"  -> this.difficulty = Difficulty.HARDCORE;
        }
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
    }

    public double tileHealthData(Coordinates location) {
        return classController.getTileKey(currentMapController.getMapValue(location)).healthDelta();
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
        classController.uiMapController.minimap(classController.mainGameController, currentMapController, classController.playerController);
    }

    private void leaveHouse() {
        if(currentMapController instanceof IExitCoordinates exitCoordinates){
            Coordinates exit = exitCoordinates.getExitCoordinates();
            Coordinates flippedPlayer = new Coordinates(classController.playerController.getMapCoordinates().y(), classController.playerController.getMapCoordinates().x());
            if(flippedPlayer.equals(exit)){
                currentMapController = mapSwitchingController.returnToMainMap(mainMapLocation);
            }
        }
    }

    private void enterHouse() {
        if(inHouse){
            mainMapLocation = classController.playerController.getMapCoordinates();
            House house = currentMapController.getHouse(currentMapController.getHouseNumber(classController.playerController.getMapCoordinates(), currentVillage), currentVillage);
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
            classController.mainGameController.UIUpdate(messenger.getMessage(), 0);
            moves = 0;
        }
    }

    //progression
    private void Victory(){
        classController.playerController.toggleGameOver();
        classController.mainGameController.GameOver(true);
    }
    @Override
    public void updateGameInfo() {
        if(classController.playerController.isGameOver()){return;}
        classController.mainGameController.UIUpdate("(" + classController.playerController.getDisplayCoordinates().x() + "," + classController.playerController.getDisplayCoordinates().y() + ")", 2);
        classController.inventoryManager.updateInventoryDisplay();
        checkEnvironment();
        checkProgression();
    }

    private void checkProgression() {
        if(currentMapController.progressesGame()){
            gameProgression.levelProgression(currentMapController.getLevel(), deepestLevel, currentMapController);
        }
        String village = gameProgression.checkProgression(currentMapController);
        if(village != null){
            currentVillage = village;
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
}

