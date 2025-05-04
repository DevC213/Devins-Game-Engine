package com.gameLogic;

import com.gameLogic.MapLogic.MapController;
import com.gameLogic.PlayerLogic.PlayerController;

import java.util.List;
import java.util.Objects;
import java.util.Vector;

public class GameController implements IUpdateMinimap, IUpdateGame {

    private PlayerController playerController;
    private Controller controller;
    private MapController mapController;
    private InventoryManager inventoryManager;
    private CombatSystem combatSystem;
    private UIMapController uiMapController;
    private CommandProcessor commandProcessor;
    private ScriptController scriptController;

    private final Vector<String> directions = new Vector<>(List.of("LEFT", "RIGHT", "UP", "DOWN"));
    private int moves = 0;
    private int deepestLevel = 0;
    private boolean healingTile = false;
    private boolean damagingTile = false;;

    public GameController() {
    }

    public void setController(Controller controller) {
        scriptController = new ScriptController();
        this.controller = controller;
        this.mapController = new MapController("/MapData/mapDataLocations.txt", this.controller);
        this.playerController = new PlayerController(0, 0,
                mapController.getCords()[1], mapController.getCords()[0], this.controller, mapController);
        this.combatSystem = new CombatSystem(playerController);
        inventoryManager = new InventoryManager(playerController, controller);
        uiMapController = new UIMapController();
        commandProcessor = new CommandProcessor(controller, mapController,
                playerController, this, this, combatSystem, inventoryManager);

    }//Command processing
    public void handleInput(String keyPressed) {
        if(playerController.isGameOver()){return;}
        commandProcessor.handleKeyInput(keyPressed);
        if (directions.contains(keyPressed)) {
            moves++;
            spawnMonster();
            checkForMonsters();
        }
    }


    public void newGame() {
        playerController.resetPlayer();
        mapController.setLevel(0);
        mapController.resetMap();
        updateGameInfo();
        uiMapController.setVisibility(2);
        controller.UIUpdate(playerController.getWeapon().name() + ": " + playerController.getWeapon().damage(), 5);
        if(playerController.isGameOver()){playerController.toggleGameOver();}
    }
    public void intro() {
        controller.UIUpdate("""
                Upon landing on this strange island, you hear a strange voice calling from the caves below.
                After hearing the voice you feel a chill go down you back, something isn't right!.
                """, 0);
        renderMinimap();
        controller.UIUpdate("""                                                        
                Enter Take or Drop to pickup or drop item.
                Then press enter. Use z to enter cave, and x
                to climb ladder, an v to attack.
                
                Use arrow keys for movement, Inventory is on
                Side of map.
                """, 0);
        controller.clearInput();
    }
    public void launchGame(Controller controller) {
        controller.UIUpdate("Welcome to Devin's adventure"
                + "Game press 'start game' to begin:\n", 0);
    }
    public void resetGame() {
        newGame();
        controller.UIUpdate("Health: " + playerController.getHealth(), 3);
        String cordOrigins = "[" + (-mapController.getCords()[1] / 2) + (-mapController.getCords()[0] / 2) + "]";
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
    public void updateGameInfo() {
        if(playerController.isGameOver()){return;}
        controller.UIUpdate(java.util.Arrays.toString(playerController.getRCords()), 2);
        checkForItems();
        checkForMonsters();
        String image = mapController.getMapValue(playerController.getCoords()[0], playerController.getCoords()[1]);
        if(Objects.equals(image, "*")) {
            Victory();
        }
        double effect = tileEffect(playerController.getCoords());
        checkTileEffect(effect);
        inventoryManager.updateInventoryDisplay();
        healthIncrease(mapController.getLevel());

    }

    //checking map
    private void checkTileEffect(double effect) {
        if (effect > 0) {
            if (!damagingTile) {
                controller.UIUpdate("Player: It hurts walking here.", 0);
                damagingTile = true;
                healingTile = false;
            }
        } else if (effect < 0) {
            if (!healingTile) {
                controller.UIUpdate("Player: Its is soothing to my feet walking here.", 0);
                healingTile = true;
                damagingTile = false;
            }
        } else {
            healingTile = false;
            damagingTile = false;
        }
        if (effect != 0) {
            playerController.damage(effect);
        }
    }
    private void checkForItems() {
        String string = mapController.itemList(playerController.getCoords()).toString();
        if (!string.isEmpty()) {
            controller.UIUpdate("Items at location: \n" + string, 0);
        }
    }
    private void checkForMonsters() {
        boolean monsterFound = mapController.isMonsterOnTile(playerController.getCoords());
        if (monsterFound) {
            controller.UIUpdate("Monsters at location: " +
                    mapController.getMonsters(playerController.getCoords()), 0);
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
        Messenger messenger = mapController.spawnMonsters(playerController.getCoords(), moves);
        if (messenger != null && messenger.getMessage() != null) {
            controller.UIUpdate(messenger.getMessage(), 0);
            moves = 0;
        }
    }

    public double tileEffect(int[] location) {
        return mapController.effect(mapController.getMapValue(location[0], location[1]));
    }
    public void healthIncrease(int level) {
        if (level > deepestLevel) {
            controller.UIUpdate("You gain confidence delving deeper, and can take more hits!", 0);
            playerController.increaseMaxHealth(25 * level);
            deepestLevel = level;
            String script = scriptController.script(level);
            if(script != null) {
                controller.UIUpdate(scriptController.script(level), 0);
            }
        }
    }
    private void Victory(){
        playerController.toggleGameOver();
        controller.GameOver(true);
    }
}

