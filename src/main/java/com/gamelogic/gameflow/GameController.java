package com.gamelogic.gameflow;

import com.gamelogic.commands.Keybindings;
import com.gamelogic.core.Controller;
import com.gamelogic.core.ScriptController;
import com.gamelogic.combat.CombatSystem;
import com.gamelogic.commands.CommandProcessor;
import com.gamelogic.core.TileKeyRegistry;
import com.gamelogic.map.*;
import com.gamelogic.map.mapLogic.MapController;
import com.gamelogic.inventory.InventoryManager;
import com.gamelogic.map.mapLogic.MapType;
import com.gamelogic.messaging.Messenger;
import com.gamelogic.playerlogic.PlayerController;
import com.gamelogic.villages.House;

import java.util.Map;
import java.util.Objects;

public class GameController implements IUpdateMinimap, IUpdateGame {

    private final PlayerController playerController;
    private final Controller controller;
    private final MapController MainMapController;
    private MapController currentMapController;
    private final InventoryManager inventoryManager;
    private final CombatSystem combatSystem;
    private final UIMapController uiMapController;
    private final CommandProcessor commandProcessor;
    private final ScriptController scriptController;
    private final EnvironmentChecker environmentChecker;
    private final Map<String, TileKey> tileKeyMap;

    Coordinates mainMapLocation;
    House house;

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
    public GameController(Controller controller, Keybindings keybindings) {
        tileKeyMap = TileKeyRegistry.getTileKeyList();
        scriptController = new ScriptController();
        this.controller = controller;
        TileKeyRegistry.initialize("/key.json");
        uiMapController = new UIMapController();
        this.MainMapController = new MapController("/levelData.json", MapType.OVERWORLD);
        uiMapController.processCharacters("/characters.json");
        Coordinates startingCords = MainMapController.generateValidStartPosition();
        this.playerController = new PlayerController(startingCords, MainMapController.getCoordinates(), this.controller, MainMapController);
        this.combatSystem = new CombatSystem(playerController);
        inventoryManager = new InventoryManager(playerController, controller);
        commandProcessor = new CommandProcessor(controller, playerController, this, this, combatSystem, inventoryManager,
                MainMapController, MainMapController, MainMapController, MainMapController, keybindings);
        int startingVisibility = tileKeyMap.get(MainMapController.getMapValue(playerController.getMapCoordinates())).visibility();
        uiMapController.setVisibility(startingVisibility);
        if (startingVisibility != 2) {
            controller.UIUpdate("Player: The air is thick here", 0);
        }
        currentMapController = MainMapController;
        environmentChecker = new EnvironmentChecker(this.controller,playerController,currentMapController);
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
        Coordinates startingCords = MainMapController.generateValidStartPosition();
        playerController.resetPlayer(startingCords);
        MainMapController.setLevel(0);
        MainMapController.resetMap();
        currentMapController = MainMapController;
        intro();
        int startingVisibility = tileKeyMap.get(currentMapController.getMapValue(playerController.getMapCoordinates())).visibility();
        uiMapController.setVisibility(startingVisibility);
        uiMapController.setDirection("down");
        controller.UIUpdate(playerController.getWeapon().name() + ": " + playerController.getWeapon().damage(), 5);
        if(playerController.isGameOver()){playerController.toggleGameOver();}
        if (startingVisibility != 2) {
            controller.UIUpdate("Player: The air is thick here", 0);
        }
    }
    public void intro() {
        controller.UIUpdate("""
                Upon awaking on this strange island
                you hear a strange voice calling from the caves below.
                After hearing the voice you feel a chill go down you back,
                something isn't right!
                """, 0);
        controller.clearInput();
        controller.scroll();
        renderMinimap();
        updateGameInfo();
    }
    public void resetGame() {
        newGame();
        controller.UIUpdate("Health: " + playerController.getHealth(), 3);
        String cordOrigins = "[" + (-currentMapController.getCoordinates().x() / 2) + (-currentMapController.getCoordinates().y() / 2) + "]";
        controller.UIUpdate(cordOrigins, 2);
        inHouse = false;
        house = null;
    }

    //IUpdateMinimap
    @Override
    public void renderMinimap() {
        if(inHouse){
            switchMap(currentMapController.getHouse(currentMapController.getHouseNumber(playerController.getMapCoordinates(), currentVillage), currentVillage));
            inHouse = false;
        }
        if(currentMapController instanceof House){ //<- I plan to have a third map type: dungeon, but haven't got to it yet.
            if(new Coordinates(playerController.getMapCoordinates().y(), playerController.getMapCoordinates().x()).equals(((House) currentMapController).getExitCoordinates())){
                returnToMainMap();
                inHouse = false;
            }
        }
        uiMapController.minimap(controller, currentMapController, playerController);
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

    private void switchMap(MapController mapController) {
        currentMapController = mapController;
        commandProcessor.changeMapState(currentMapController);
        mainMapLocation = playerController.getMapCoordinates();
        environmentChecker.changeMap(mapController);
        if(currentMapController instanceof House) {
            playerController.setCoordinates(((House) currentMapController).getExitCoordinates().y(), ((House) currentMapController).getExitCoordinates().x()-1);
            playerController.setMaxCoordinates(((House) currentMapController).getMaxCoordinates());
        }
    }
    private void returnToMainMap(){
        currentMapController = MainMapController;
        commandProcessor.changeMapState(currentMapController);
        playerController.setMaxCoordinates(currentMapController.getCoordinates());
        playerController.setCoordinates(mainMapLocation.x(),  mainMapLocation.y()+1);
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
            controller.UIUpdate(messenger.getMessage(), 0);
            moves = 0;
        }
    }

    //progression
    public void levelProgression(int level) {
        if (level > deepestLevel) {
            String sound = currentMapController.getSound();
            String voice = currentMapController.getVoice();
            controller.UIUpdate("You gain confidence delving deeper, and can take more hits!", 0);
            playerController.increaseMaxHealth(25 * level);
            playerController.increaseLevel();
            deepestLevel = level;
            String script = scriptController.script(level);
            if(script != null) {
                controller.UIUpdate(script, 0);
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
        controller.GameOver(true);
    }


    @Override
    public void updateGameInfo() {
        if(playerController.isGameOver()){return;}
        controller.UIUpdate("(" + playerController.getDisplayCoordinates().x() + "," + playerController.getDisplayCoordinates().y() + ")", 2);

        TileKey tile = tileKeyMap.get(currentMapController.getMapValue(playerController.getMapCoordinates()));
        if(Objects.equals(tile.name(), "goal")) {
            Victory();
        }
        double effect =  tileHealthData(playerController.getMapCoordinates());
        environmentChecker.checkTile(combatSystem, commandProcessor, effect);
        inventoryManager.updateInventoryDisplay();
        if(currentMapController == MainMapController) {
            levelProgression(MainMapController.getLevel());
            if(currentMapController.getLevel() == 0 && currentMapController == MainMapController) {
                Messenger messenger = MainMapController.checkForVillages(playerController.getMapCoordinates());
                String village = messenger.getMessage();

                if (village != null) {
                    controller.UIUpdate(village, 0);
                    currentVillage = messenger.getPayloadString();
                }
            }
        }
    }
}

