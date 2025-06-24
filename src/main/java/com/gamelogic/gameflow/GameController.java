package com.gamelogic.gameflow;

import com.gamelogic.core.Controller;
import com.gamelogic.core.ScriptController;
import com.gamelogic.combat.CombatSystem;
import com.gamelogic.commands.CommandProcessor;
import com.gamelogic.core.TileKeyRegistry;
import com.gamelogic.map.*;
import com.gamelogic.map.mapLogic.MapController;
import com.gamelogic.inventory.InventoryManager;
import com.gamelogic.messaging.Messenger;
import com.gamelogic.playerlogic.PlayerController;

import java.util.Map;
import java.util.Objects;

public class GameController implements IUpdateMinimap, IUpdateGame {

    private PlayerController playerController;
    private Controller controller;
    private MapController mapController;
    private InventoryManager inventoryManager;
    private CombatSystem combatSystem;
    private UIMapController uiMapController;
    private CommandProcessor commandProcessor;
    private ScriptController scriptController;
    private Map<String, TileKey> tileKeyMap;
    private String currentVillage;
    private boolean inHouseOrDungeon = false;

    public void setHealth() {
        playerController.setHealth(uiMapController.getPlayerHealth());
    }

    enum TileStatus {NEUTRAL, HEALING, DAMAGING}
    private TileStatus tileStatus = TileStatus.NEUTRAL;
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

    public GameController() {
    }
    public GameController(Controller controller) {
        tileKeyMap = TileKeyRegistry.getTileKeyList();
        scriptController = new ScriptController();
        this.controller = controller;
        TileKeyRegistry.initialize("/key.json");
        uiMapController = new UIMapController();
        this.mapController = new MapController("/levelData.json", this.controller);
        uiMapController.processCharacters("/characters.json");
        Coordinates startingCords = mapController.generateValidStartPosition();
        this.playerController = new PlayerController(startingCords,mapController.getCoordinates(), this.controller, mapController);
        this.combatSystem = new CombatSystem(playerController);
        inventoryManager = new InventoryManager(playerController, controller);
        commandProcessor = new CommandProcessor(controller, playerController, this, this, combatSystem, inventoryManager,
                mapController,mapController,mapController,mapController);
        int startingVisibility = tileKeyMap.get(mapController.getMapValue(playerController.getMapCoordinates())).visibility();
        uiMapController.setVisibility(startingVisibility);
        if (startingVisibility != 2) {
            controller.UIUpdate("Player: The air is thick here", 0);
        }
    }
    public void setCharacter(String character) {
        uiMapController.setCharacterID(character);
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
    public void newGame() {
        Coordinates startingCords = mapController.generateValidStartPosition();
        playerController.resetPlayer(startingCords);
        mapController.setLevel(0);
        mapController.resetMap();
        intro();
        int startingVisibility = tileKeyMap.get(mapController.getMapValue(playerController.getMapCoordinates())).visibility();
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
        renderMinimap();
        controller.clearInput();
        updateGameInfo();
        controller.scroll();
    }
    public void resetGame() {
        newGame();
        controller.UIUpdate("Health: " + playerController.getHealth(), 3);
        String cordOrigins = "[" + (-mapController.getCoordinates().x() / 2) + (-mapController.getCoordinates().y() / 2) + "]";
        controller.UIUpdate(cordOrigins, 2);
    }

    //Mini-Map Control
    @Override
    public void renderMinimap() {
        uiMapController.minimap(controller, mapController, playerController);
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
    @Override
    public void updateGameInfo() {
        if(playerController.isGameOver()){return;}
        controller.UIUpdate("(" + playerController.getDisplayCoordinates().x() + "," + playerController.getDisplayCoordinates().y() + ")", 2);
        checkForItems();
        checkForMonsters();
        String image = mapController.getMapValue(playerController.getMapCoordinates());
        if(Objects.equals(image, "GOAL")) {
            Victory();
        }
        double effect = tileHealthData(playerController.getMapCoordinates());
        checkTileEffect(effect);
        inventoryManager.updateInventoryDisplay();
        healthIncrease(mapController.getLevel());
        String village = mapController.checkForVillages(playerController.getMapCoordinates()).getMessage();
        String villageName = mapController.checkForVillages(playerController.getMapCoordinates()).getMessage();
        if(village != null) {
            controller.UIUpdate(village, 0);
            currentVillage = villageName;
        }
    }

    //checking map
    private void checkTileEffect(double effect) {
        if (effect < 0) {
            if (tileStatus != TileStatus.DAMAGING) {
                controller.UIUpdate("Player: It hurts walking here.", 0);
                tileStatus = TileStatus.DAMAGING;
            }
        } else if (effect > 0) {
            if (tileStatus != TileStatus.HEALING) {
                controller.UIUpdate("Player: Its is soothing to my feet walking here.", 0);
                tileStatus = TileStatus.HEALING;
            }
        } else {
            tileStatus = TileStatus.NEUTRAL;
        }
        if (effect != 0) {
            playerController.changeHealth(effect);
        }
    }
    private void checkForItems() {
        String string = mapController.itemList(playerController.getMapCoordinates()).toString();
        if (!string.isEmpty()) {
            controller.UIUpdate("Items at location: \n" + string, 0);
        }
    }
    private void checkForMonsters() {
        boolean monsterFound = mapController.isMonsterOnTile(playerController.getMapCoordinates());
        if (monsterFound) {
            controller.UIUpdate("Monsters at location: " +
                    mapController.getMonsters(playerController.getMapCoordinates()), 0);
            if (!combatSystem.isMonsterOnTile()) {
                combatSystem.toggleMonster();
            }
        } else {
            if (commandProcessor.escaped()) {
                commandProcessor.toggleEscape();
                combatSystem.toggleMonster();
            } else if (combatSystem.isMonsterOnTile()) {
                controller.UIUpdate("Monsters Killed", 0);
                combatSystem.toggleMonster();
            }
        }
    }
    private void spawnMonster() {
        Messenger messenger = mapController.spawnMonsters(playerController.getMapCoordinates(), moves);
        if (messenger != null && messenger.getMessage() != null) {
            controller.UIUpdate(messenger.getMessage(), 0);
            moves = 0;
        }
    }
    public double tileHealthData(Coordinates location) {
        return TileKeyRegistry.getTileKey(mapController.getMapValue(location)).healthDelta();
    }
    public void healthIncrease(int level) {
        if (level > deepestLevel) {
            String sound = mapController.getSound();
            String voice = mapController.getVoice();
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
}

