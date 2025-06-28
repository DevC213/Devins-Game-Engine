package com.gamelogic.gameflow;

import com.gamelogic.commands.Keybindings;
import com.gamelogic.core.*;
import com.gamelogic.combat.CombatSystem;
import com.gamelogic.commands.CommandProcessor;
import com.gamelogic.map.*;
import com.gamelogic.map.mapLogic.MapController;
import com.gamelogic.inventory.InventoryManager;
import com.gamelogic.map.mapLogic.MapType;
import com.gamelogic.messaging.Messenger;
import com.gamelogic.playerlogic.PlayerController;
import com.gamelogic.villages.House;
import com.savesystem.PlayerState;

import java.util.Map;
import java.util.Objects;

public class GameController implements IUpdateMinimap, IUpdateGame {

    private final PlayerController playerController;
    private final MainGameController mainGameController;
    private MapController currentMapController;
    private final InventoryManager inventoryManager;
    private final CombatSystem combatSystem;
    private final UIMapController uiMapController;
    private final CommandProcessor commandProcessor;
    private final ScriptController scriptController;
    private final EnvironmentChecker environmentChecker;
    private final Map<String, TileKey> tileKeyMap;
    private Difficulty difficulty;

    Coordinates mainMapLocation;
    int mainMapCurrentLevel = 0;
    House house;

    public void setDifficulty(String difficulty) {
        switch(difficulty.toLowerCase()) {
            case "normal" -> this.difficulty = Difficulty.NORMAL;
            case "daredevil"  -> this.difficulty = Difficulty.HARDCORE;
        }
    }

    public void respawn() {
        currentMapController = MapRegistry.getMapController(0);
        currentMapController.setLevel(0);
        Coordinates coordinates = currentMapController.generateValidStartPosition();
        playerController.respawn(coordinates);
        mainGameController.UIUpdate("You passed out for a time, and found yourself if a different location.\n" +
                "You were robbed 50!",0);
        mainGameController.UIUpdate("Money: " + playerController.getGold(),6);
        mainGameController.UIUpdate("Health: " + playerController.getHealth(),3);
        updateGameInfo();
        renderMinimap();
    }

    public int getID() {
        return currentMapController.getID();
    }

    public int getLevel() {
        return currentMapController.getLevel();
    }

    public void setID(int currentMapID) {
        switchMap(currentMapID);

        if(currentMapController instanceof IExitCoordinates exitCoordinates && currentMapController.getLevel() == 0) {
            Coordinates mainMap = exitCoordinates.getMapCoordinates();
            mainMapLocation = new Coordinates(mainMap.y(), mainMap.x());
            playerController.setMaxCoordinates(currentMapController.getCoordinates());
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

    enum Movement{LEFT, RIGHT, UP, DOWN;
        public static Movement getmovement(String string){
            try {
                return Movement.valueOf(string.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    private int moves = 0;
    private int deepestLevel = 0;
    private String currentVillage;
    private boolean inHouse = false;

    //Constructor
    public GameController(MainGameController mainGameController, Keybindings keybindings) {
        tileKeyMap = TileKeyRegistry.getTileKeyList();
        scriptController = new ScriptController();
        this.mainGameController = mainGameController;
        TileKeyRegistry.initialize("/key.json");
        uiMapController = new UIMapController();
        this.currentMapController = new MapController("/levelData.json", MapType.OVERWORLD,0);
        MapRegistry.addMap(currentMapController,0);
        uiMapController.processCharacters("/characters.json");
        Coordinates startingCords = currentMapController.generateValidStartPosition();
        this.playerController = new PlayerController(startingCords, currentMapController.getCoordinates(), this.mainGameController, difficulty);
        this.combatSystem = new CombatSystem(playerController);
        inventoryManager = new InventoryManager(playerController, mainGameController);
        commandProcessor = new CommandProcessor(mainGameController, playerController, this, this, combatSystem, inventoryManager,
                currentMapController, currentMapController, currentMapController, currentMapController, keybindings);
        int startingVisibility = tileKeyMap.get(currentMapController.getMapValue(playerController.getMapCoordinates())).visibility();
        uiMapController.setVisibility(startingVisibility);
        if (startingVisibility != 2) {
            mainGameController.UIUpdate("Player: The air is thick here", 0);
        }
        environmentChecker = new EnvironmentChecker(this.mainGameController,playerController,currentMapController);
    }
    public double tileHealthData(Coordinates location) {
        return TileKeyRegistry.getTileKey(currentMapController.getMapValue(location)).healthDelta();
    }
    public void handleInput(String keyPressed) {
        if(playerController.isGameOver()){return;}
        commandProcessor.handleKeyInput(keyPressed);
        Movement move = Movement.getmovement(keyPressed);
        if (move != null) {
            moves++;
            spawnMonster();
        }
    }

    //dialog
    public void newGame() {
        currentMapController = MapRegistry.getMapController(0);
        Coordinates startingCords = currentMapController.generateValidStartPosition();
        playerController.resetPlayer(startingCords);
        currentMapController.setLevel(0);
        currentMapController.resetMap();
        intro();
        int startingVisibility = tileKeyMap.get(currentMapController.getMapValue(playerController.getMapCoordinates())).visibility();
        uiMapController.setVisibility(startingVisibility);
        uiMapController.setDirection("down");
        mainGameController.UIUpdate(playerController.getWeapon().name() + ": " + playerController.getWeapon().damage(), 5);
        if(playerController.isGameOver()){playerController.toggleGameOver();}
        if (startingVisibility != 2) {
            mainGameController.UIUpdate("Player: The air is thick here", 0);
        }
    }
    public void intro() {
        mainGameController.UIUpdate("""
                Upon awaking on this strange island
                you hear a strange voice calling from the caves below.
                After hearing the voice you feel a chill go down you back,
                something isn't right!
                """, 0);
        mainGameController.clearInput();
        mainGameController.scroll();
        renderMinimap();
        updateGameInfo();
    }
    public void resetGame() {
        newGame();
        mainGameController.UIUpdate("Health: " + playerController.getHealth(), 3);
        String cordOrigins = "[" + (-currentMapController.getCoordinates().x() / 2) + (-currentMapController.getCoordinates().y() / 2) + "]";
        mainGameController.UIUpdate(cordOrigins, 2);
        inHouse = false;
        house = null;
    }

    //IUpdateMinimap
    @Override
    public void renderMinimap() {
        if(inHouse){
            mainMapLocation = playerController.getMapCoordinates();
            switchMap(currentMapController.getHouse(currentMapController.getHouseNumber(playerController.getMapCoordinates(), currentVillage), currentVillage).getID());
            inHouse = false;
        }
        if(currentMapController instanceof IExitCoordinates exitCoordinates){
            Coordinates exit = exitCoordinates.getExitCoordinates();
            if(new Coordinates(playerController.getMapCoordinates().y(), playerController.getMapCoordinates().x()).equals(exit)){
                returnToMainMap();
            }
        }
        uiMapController.minimap(mainGameController, currentMapController, playerController);
    }
    @Override
    public void toggleHouse() {
        inHouse = !inHouse;
    }
    @Override
    public void setVisibility(int visibility) {
        uiMapController.setVisibility(visibility);
    }
    @Override
    public void setDirection(int deltaX, int deltaY) {
        if(deltaY > 0){
            uiMapController.setDirection("down");
        }else if(deltaY < 0){
            uiMapController.setDirection("up");
        } else if(deltaX > 0){
            uiMapController.setDirection("right");
        } else{
            uiMapController.setDirection("left");
        }
    }

    private void switchMap(int ID) {
        currentMapController = MapRegistry.getMapController(ID);
        commandProcessor.changeMapState(currentMapController);
        environmentChecker.changeMap(currentMapController);
        if(currentMapController instanceof IExitCoordinates exitCoordinates) {
            Coordinates exit = exitCoordinates.getExitCoordinates();
            playerController.setCoordinates(exit.y(), exit.x()-1);
            playerController.setMaxCoordinates(currentMapController.getCoordinates());
        }
    }
    private void returnToMainMap(){
        currentMapController = MapRegistry.getMapController(0);
        commandProcessor.changeMapState(currentMapController);
        playerController.setMaxCoordinates(currentMapController.getCoordinates());
        playerController.setCoordinates(mainMapLocation.x(),  mainMapLocation.y()+1);
        environmentChecker.changeMap(currentMapController);
    }
    public void setHealth() {
        playerController.setHealth(uiMapController.getPlayerHealth());
    }
    public void setCharacter(String character) {
        uiMapController.setCharacterID(character);
    }
    private void spawnMonster() {
        Messenger messenger = currentMapController.spawnMonsters(playerController.getMapCoordinates(), moves);
        if (messenger != null && messenger.getMessage() != null) {
            mainGameController.UIUpdate(messenger.getMessage(), 0);
            moves = 0;
        }
    }

    //progression
    public void levelProgression(int level) {
        if (level > deepestLevel) {
            String sound = currentMapController.getSound();
            String voice = currentMapController.getVoice();
            mainGameController.UIUpdate("You gain confidence delving deeper, and can take more hits!", 0);
            playerController.increaseMaxHealth(25 * level);
            playerController.increaseLevel();
            deepestLevel = level;
            String script = scriptController.script(level);
            if(script != null) {
                mainGameController.UIUpdate(script, 0);
            } else {
                if(sound != null) {
                    scriptController.playSound(sound);
                }
                if(voice != null) {
                    scriptController.playSound(voice);
                }
            }
        }
    }
    private void Victory(){
        playerController.toggleGameOver();
        mainGameController.GameOver(true);
    }

    @Override
    public void updateGameInfo() {
        if(playerController.isGameOver()){return;}
        mainGameController.UIUpdate("(" + playerController.getDisplayCoordinates().x() + "," + playerController.getDisplayCoordinates().y() + ")", 2);
        TileKey tile = tileKeyMap.get(currentMapController.getMapValue(playerController.getMapCoordinates()));
        if(Objects.equals(tile.name(), "goal")) {
            Victory();
        }
        double effect =  tileHealthData(playerController.getMapCoordinates());
        environmentChecker.checkTile(combatSystem, commandProcessor, effect);
        inventoryManager.updateInventoryDisplay();
        if(currentMapController.progressesGame()){
            levelProgression(currentMapController.getLevel());
        }
        if(currentMapController.getLevel() == 0) {
            Messenger messenger = currentMapController.checkForVillages(playerController.getMapCoordinates());
            if (messenger != null) {
                String village = messenger.getMessage();
                if (village != null) {
                    mainGameController.UIUpdate(village, 0);
                    currentVillage = messenger.getPayloadString();
                }
            }
        }
    }
    public int getDeepestLevel() {
        return deepestLevel;
    }
    public PlayerState getPlayerState(){
        return playerController.createPlayerState();
    }
    public void loadData(PlayerState playerState) {
        playerController.loadFromPlayerState(playerState);
        updateGameInfo();
        renderMinimap();
    }
}

