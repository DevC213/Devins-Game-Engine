package com.adventure_logic;

import com.Movement.MovementController;
import com.adventure_logic.MapLogic.MapController;
import com.adventure_logic.PlayerLogic.PlayerController;

import java.util.List;
import java.util.Objects;
import java.util.Vector;

public class GameController {

    private final PlayerController playerController;
    private final Controller controller;
    private final MapController mapController;
    private final InventoryManager inventoryManager;
    private final CombatSystem combatSystem;
    private final MovementController movementController;
    private final UIMapController uiMapController;
    private boolean escape = false;
    private final Vector<String> directions= new Vector<>(List.of("LEFT", "RIGHT", "UP", "DOWN"));
    private int moves = 0;

    private enum CommandState {
        NONE,  // 0 - Not in a command
        DROP,  // 1 - Dropping item
        TAKE,  // 2 - Taking item
        HEAL,  // 3 - Using health item
        ATTACK, // 4 - Attacking
        HATTACK, //5 - Using health item during fight
    }
    private CommandState commandState = CommandState.NONE;

    GameController(Controller controller) {
        this.controller = controller;
        this.mapController = new MapController("/MapData/Maps/Map1.txt", this.controller);
        this.playerController = new PlayerController(0, 0,
                mapController.getCords()[1], mapController.getCords()[0], this.controller, mapController);
        this.combatSystem = new CombatSystem(mapController, playerController, this.controller);
        this.inventoryManager = new InventoryManager(playerController, this.controller);
        this.movementController = new MovementController();
        uiMapController = new UIMapController();
    }
    public void newGame() {
        playerController.resetPlayer();
        mapController.setLevel(0);
        mapController.resetMap();
        updateGameInfo();
        uiMapController.setVisibility(2);
    }
    public void updateGameInfo() {
        controller.UIUpdate(java.util.Arrays.toString(playerController.getRCords()), 2);
        checkForItems();
        checkForMonsters();
        inventoryManager.updateInventoryDisplay();
    }

    //Command processing
    public void handleKeyInput(String keyPressed) {
        if (!commandState.equals(CommandState.NONE) && !keyPressed.equals("ENTER")) {
            return;
        }
        if (combatSystem.isMonsterOnTile() && !Objects.equals(keyPressed, "V") && !Objects.equals(keyPressed, "C")) {
            if(directions.contains(keyPressed) ) {
                int number = (int) (Math.floor(Math.random() * 10));
                System.out.println(number);
                if(number < 3){
                    controller.UIUpdate("Escape success",0);
                    escape = true;
                    commandState = CommandState.NONE;
                } else {
                    controller.UIUpdate("Failed Escape!", 0);
                    combatSystem.monsterAttack();
                    return;
                }
            }
        }
        switch (keyPressed) {
            case "LEFT" -> movementController.direction("west");
            case "RIGHT" -> movementController.direction("east");
            case "DOWN" -> movementController.direction("south");
            case "UP" -> movementController.direction("north");
            case "Z" -> traverseLevels(-1);
            case "X" -> traverseLevels(1);
            case "C" -> {
                controller.UIUpdate("Which healing item?", 0);
                if (commandState == CommandState.ATTACK) {
                    commandState = CommandState.HATTACK;
                } else {
                    commandState = CommandState.HEAL;
                }
            }
            case "V" -> {
                if(mapController.getMonsters(playerController.getCords()) == null){
                    controller.UIUpdate("No monster on tile", 0);
                    commandState = CommandState.NONE;
                    controller.clearInput();
                    return;
                }
                controller.UIUpdate("Which monster?", 0);
                commandState = CommandState.ATTACK;
                controller.setFocus();

            }
            default -> {if (Objects.equals(keyPressed, "ENTER")) {
                handleTextCommand(controller.getCommand());}}
        }
        controller.clearInput();
        updateGameInfo();
    }
    private void handleTextCommand(String command) {
        if(commandState.equals(CommandState.NONE)) {
            handleInventoryCommands(command);
        } else {
            executePendingAction(command);
            if (commandState.equals(CommandState.HATTACK)) {
                commandState = CommandState.ATTACK;
            } else {
                commandState = CommandState.NONE;
            }
        }
        controller.clearInput();
    }
    private void executePendingAction(String command) {
        switch (commandState) {
            case DROP -> {
                if(!playerController.contains(command) || Objects.equals(command, "")){
                    controller.UIUpdate("No item dropped", 0);
                    commandState = CommandState.NONE;
                    return;
                }
                inventoryManager.dropItem(command);
                mapController.addItem(playerController.getCords(), command);
            }
            case TAKE -> inventoryManager.takeItem(mapController.grabItem(playerController.getCords(), command));
            case HEAL -> inventoryManager.useHealthItem(command);
            case ATTACK -> combatSystem.attack(command);
            default -> controller.UIUpdate("Unexpected command state", 0);
        }
    }
    private void handleInventoryCommands(String command) {
        switch (command.toLowerCase()) {
            case "take" -> {
                if(mapController.getItems(playerController.getCords()) == null){
                    controller.UIUpdate("No items on tile", 0);
                    commandState = CommandState.NONE;
                    return;
                }
                controller.UIUpdate("Which item?",0);
                commandState = CommandState.TAKE;
            }
            case "drop" -> {
                if(playerController.InventoryCommands(new String[]{} ,3) == null){
                    controller.UIUpdate("No items in inventory", 0);
                    commandState = CommandState.NONE;
                    return;
                }
                controller.UIUpdate("Which item?",0);
                commandState = CommandState.DROP;
            }
            default -> controller.UIUpdate("Invalid command", 0);
        }
    }

    //Command Processing for movement
    public void moveOnLevel(int movement, char dir){
        switch (dir) {
            case 'r':
                uiMapController.setVisibility(playerController.movement(movement,1));
                break;
            case 'c':
                uiMapController.setVisibility(playerController.movement(movement,2));
                break;
            default:
                break;
        }
        moves++;
        renderMinimap();
        spawnMonster();
    }
    public void traverseLevels(int dir){
        if(mapController.getMovement(mapController.getMapValue(playerController.getCords()[0],
                playerController.getCords()[1]),1)) {
            if(dir < 0 && mapController.isCave(mapController.getMapValue(playerController.getCords()[0],
                    playerController.getCords()[1]))){
                mapController.change_level(dir);
                renderMinimap();
            }
            else if(dir > 0 && mapController.isLadder(mapController.getMapValue(playerController.getCords()[0],
                    playerController.getCords()[1]))){
                mapController.change_level(dir);
                renderMinimap();
            }
            else{
                controller.UIUpdate("Can only go up on a ladder or down on a cave",0);
            }
        } else{
            controller.UIUpdate("Can only change level on a ladder or a cave",0);
        }
    }

    //Mini-Map Control
    public void renderMinimap(){
        uiMapController.minimap(controller, mapController, playerController);
    }

    //checking map
    private void checkForItems() {
        if (mapController.getItems(playerController.getCords()) != null) {
            controller.UIUpdate("Items at location: " +
                    mapController.getItems(playerController.getCords()), 0);
        }
    }
    private void checkForMonsters() {
        if (mapController.getMonsters(playerController.getCords()) != null) {
            controller.UIUpdate("Monsters at location: " +
                    mapController.getMonsters(playerController.getCords()), 0);
            if (!combatSystem.isMonsterOnTile()) {
                combatSystem.toggleMonster();
            }
        } else if (escape){
            escape = false;
            combatSystem.toggleMonster();
        }else if(combatSystem.isMonsterOnTile()) {
            controller.UIUpdate("Monsters Killed", 0);
            combatSystem.toggleMonster();
        }
    }

    //facade & communication functions for Adventure class
    public int[] getCords(){return mapController.getCords();}
    public double getHealth(){return playerController.getHealth();}

    private void spawnMonster() {
        int MOVES_BEFORE_SPAWN = 10;
        int random = (int) Math.floor(Math.random() * 20);

        if (random > 15 & moves >= MOVES_BEFORE_SPAWN) {
            mapController.spawnMonster(playerController.getCords());
            controller.UIUpdate("Monster Spawned", 0);
            moves = 0;
        }
    }
}

