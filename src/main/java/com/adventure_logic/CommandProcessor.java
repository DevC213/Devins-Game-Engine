package com.adventure_logic;

import com.adventure_logic.MapLogic.MapController;
import com.adventure_logic.PlayerLogic.PlayerController;

import java.util.List;
import java.util.Objects;
import java.util.Vector;

public class CommandProcessor {

    private enum CommandState {
        NONE,  // 0 - Not in a command
        TAKE,  // 2 - Taking item
        HEAL,  // 3 - Using health item
        ATTACK, // 4 - Attacking
        HATTACK, //5 - Using health item during fight
        PAUSE //6 Pause clearing of input for take/drop

    }
    private CommandState commandState = CommandState.NONE;
    private final Vector<String> directions= new Vector<>(List.of("LEFT", "RIGHT", "UP", "DOWN"));
    private boolean escape = false;
    Controller controller;
    MapController mapController;
    PlayerController playerController;
    IUpdateMinimap updateMinimap;
    IUpdateGame updateGame;
    CombatSystem combatSystem;
    InventoryManager inventoryManager;


    public CommandProcessor(Controller controller, MapController mapController, PlayerController playerController
            , IUpdateMinimap updateMinimap,IUpdateGame updateGame, CombatSystem combatSystem, InventoryManager inventoryManager) {
        this.controller = controller;
        this.mapController = mapController;
        this.playerController = playerController;
        this.updateMinimap = updateMinimap;
        this.combatSystem = combatSystem;
        this.inventoryManager = inventoryManager;
        this.updateGame = updateGame;
    }
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
                    combatSystem.monstersAttack(mapController.getMonstersAttack(playerController.getCords()));
                    return;
                }
            }
        }
        switch (keyPressed) {
            case "LEFT" -> {
                updateMinimap.setVisibility(moveOnLevel(-1, 'c'));
                updateMinimap.renderMinimap();
            }
            case "RIGHT" -> {
                updateMinimap.setVisibility(moveOnLevel(1, 'c'));
                updateMinimap.renderMinimap();
            }
            case "DOWN" -> {
                updateMinimap.setVisibility(moveOnLevel(1, 'r'));
                updateMinimap.renderMinimap();
            }
            case "UP" -> {
                updateMinimap.setVisibility(moveOnLevel(-1, 'r'));
                updateMinimap.renderMinimap();
            }
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
            case "B" -> {
                commandState = CommandState.PAUSE;
                controller.clearInput();
                controller.UIUpdate("Enter take.", 0);
            }
            default -> {if (Objects.equals(keyPressed, "ENTER")) {
                handleTextCommand(controller.getCommand());}}
        }
        controller.clearInput();
        updateGame.updateGameInfo();
    }
    private void handleTextCommand(String command) {

        if(commandState.equals(CommandState.NONE)) {
            handleInventoryCommands(command);
        } else {
            executePendingAction(command);
            if (commandState.equals(CommandState.HATTACK)) {
                commandState = CommandState.ATTACK;
            } else {
                if(commandState.equals(CommandState.TAKE)) {
                    return;
                }
                commandState = CommandState.NONE;
            }
        }
        controller.clearInput();
    }
    private void executePendingAction(String command) {
        switch (commandState) {
            case TAKE -> {
                Messenger messenger = mapController.grabItem(playerController.getCords(), command);
                switch (messenger.getItemType()){
                    case 0:
                        controller.UIUpdate("Grabbed weapon: " + messenger.getWeapon().name(),0);
                        controller.UIUpdate(messenger,5);
                        playerController.equipWeapon(messenger.getWeapon());
                        break;
                    case 1:
                        controller.UIUpdate("Grabbed armor: " + messenger.getArmor().name(),0);
                        controller.UIUpdate(messenger,4);
                        playerController.equipArmor(messenger.getArmor());
                        break;
                    case 2:
                        controller.UIUpdate("Grabbed healing item: " + messenger.getHealingItem().getName(),0);
                        playerController.addToInventory(messenger);
                        break;
                    default: throw new IllegalArgumentException("Unexpected value: " + messenger.getItemType());
                }
                commandState = CommandState.NONE;
            }
            case HEAL -> inventoryManager.useHealthItem(command);
            case ATTACK -> {
                String message = combatSystem.attack(mapController.attackMonsters(command,
                        playerController.getAttack(), playerController.getCords())).getMessage();
                if(message != null) {
                    controller.UIUpdate(message, 0);
                }
                combatSystem.monstersAttack(mapController.getMonstersAttack(playerController.getCords()));
            }
            case PAUSE -> handleInventoryCommands(command);
            default -> controller.UIUpdate("Unexpected command state", 0);
        }
    }
    private void handleInventoryCommands(String command) {
        if (command.equalsIgnoreCase("take")) {
            if (!mapController.getItems(playerController.getCords())) {
                controller.UIUpdate("No items on tile", 0);
                commandState = CommandState.NONE;
                return;
            }
            controller.UIUpdate("Which item?", 0);
            commandState = CommandState.TAKE;
        } else {
            controller.UIUpdate("Invalid command", 0);
        }
    }

    //Command Processing for movement
    public int moveOnLevel(int movement, char dir){
        int[] playerCords = playerController.getCords();
        return switch (dir) {
            case 'r' -> playerController.movement(movement, 1,
                    mapController.getMapValue(playerCords[0], playerCords[1]), mapController.getMapValue(playerCords[0], playerCords[1] + movement));
            case 'c' -> playerController.movement(movement, 2,
                    mapController.getMapValue(playerCords[0], playerCords[1]), mapController.getMapValue(playerCords[0] + movement, playerCords[1]));
            default -> 2;
        };
    }
    public void traverseLevels(int dir){
        if(mapController.getMovement(mapController.getMapValue(playerController.getCords()[0],
                playerController.getCords()[1]),1)) {
            if(dir < 0 && mapController.isCave(mapController.getMapValue(playerController.getCords()[0],
                    playerController.getCords()[1]))){
                mapController.change_level(dir);
                updateMinimap.renderMinimap();
            }
            else if(dir > 0 && mapController.isLadder(mapController.getMapValue(playerController.getCords()[0],
                    playerController.getCords()[1]))){
                mapController.change_level(dir);
                updateMinimap.renderMinimap();
            }
            else{
                controller.UIUpdate("Can only go up on a ladder or down on a cave",0);
            }
        } else{
            controller.UIUpdate("Can only change level on a ladder or a cave",0);
        }
    }
    public boolean escaped(){
        return escape;
    }
    public void toggleEscape(){
        escape = !escape;
    }
}
