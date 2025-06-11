package com.gameLogic;

import com.gameLogic.MapLogic.MapController;
import com.gameLogic.PlayerLogic.PlayerController;

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
        scriptController = new ScriptController();
        this.controller = controller;
        this.mapController = new MapController("/MapData/mapDataLocations.txt", this.controller);
        int[] startingCords = mapController.generateValidStartPosition();
        this.playerController = new PlayerController(startingCords[0], startingCords[1],
                mapController.getCords()[1], mapController.getCords()[0], this.controller, mapController);
        this.combatSystem = new CombatSystem(playerController);
        inventoryManager = new InventoryManager(playerController, controller);
        uiMapController = new UIMapController();
        commandProcessor = new CommandProcessor(controller, mapController,
                playerController, this, this, combatSystem, inventoryManager);

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
        int[] startingCords = mapController.generateValidStartPosition();
        playerController.resetPlayer(startingCords);
        mapController.setLevel(0);
        mapController.resetMap();
        intro();
        uiMapController.setVisibility(2);
        uiMapController.setDirection(0);
        controller.UIUpdate(playerController.getWeapon().name() + ": " + playerController.getWeapon().damage(), 5);
        if(playerController.isGameOver()){playerController.toggleGameOver();}
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
    public void setDirection(int deltaX, int deltaY) {
        if(deltaY > 0){
            uiMapController.setDirection(0);
        }else if(deltaY < 0){
            uiMapController.setDirection(3);
        } else if(deltaX > 0){
            uiMapController.setDirection(2);
        } else{
            uiMapController.setDirection(1);
        }
    }
    @Override
    public void updateGameInfo() {
        if(playerController.isGameOver()){return;}
        controller.UIUpdate(java.util.Arrays.toString(playerController.getRCords()), 2);
        checkForItems();
        checkForMonsters();
        String image = mapController.getMapValue(playerController.getCoords()[0], playerController.getCoords()[1]);
        if(Objects.equals(image, "GOAL")) {
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
            if (tileStatus != TileStatus.DAMAGING) {
                controller.UIUpdate("Player: It hurts walking here.", 0);
                tileStatus = TileStatus.DAMAGING;
            }
        } else if (effect < 0) {
            if (tileStatus != TileStatus.HEALING) {
                controller.UIUpdate("Player: Its is soothing to my feet walking here.", 0);
                tileStatus = TileStatus.HEALING;
            }
        } else {
            tileStatus = TileStatus.NEUTRAL;
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
            playerController.increaseLevel();
            deepestLevel = level;
            String script = scriptController.script(level);
            if(script != null) {
                controller.UIUpdate(script, 0);
            }
        }
    }
    private void Victory(){
        playerController.toggleGameOver();
        controller.GameOver(true);
    }
}

