package com.gameLogic;

import com.gameLogic.MapLogic.MapController;
import com.gameLogic.PlayerLogic.PlayerController;

import java.util.Map;
import java.util.Objects;


public class CommandProcessor{

    private enum CommandState {
        NONE,  // 0 - Not in a command
        TAKE,  // 2 - Taking item
        HEAL,  // 3 - Using health item
        ATTACK, // 4 - Attacking
        HATTACK, //5 - Using health item during fight
        PAUSE //6 Pause clearing of input for take

    }
    private enum Movement {
        LEFT, RIGHT, UP, DOWN;
        public static Movement getmovement(String string) {
            try {
                return Movement.valueOf(string.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }
    private CommandState commandState = CommandState.NONE;
    Map<String, Runnable> charCommands = Map.of(
            "C", this::healing,
            "V", this::attack,
            "B", this::grabItem,
            "X", () -> traverseLevels(1),
            "Z", () -> traverseLevels(-1)
    );
    private boolean escape = false;
    IGuiEventListener controller;
    IGuiCommandGetter commandGetter;
    MapController mapController;
    PlayerController playerController;
    IUpdateMinimap updateMinimap;
    IUpdateGame updateGame;
    CombatSystem combatSystem;
    InventoryManager inventoryManager;


    public CommandProcessor(Controller controller, MapController mapController, PlayerController playerController
            , IUpdateMinimap updateMinimap, IUpdateGame updateGame, CombatSystem combatSystem, InventoryManager inventoryManager) {
        this.controller = controller;
        this.mapController = mapController;
        this.playerController = playerController;
        this.updateMinimap = updateMinimap;
        this.combatSystem = combatSystem;
        this.inventoryManager = inventoryManager;
        this.updateGame = updateGame;
        this.commandGetter = controller;
    }

    public void handleKeyInput(String keyPressed) {
        Movement move = Movement.getmovement(keyPressed);
        if (commandState != CommandState.NONE && !keyPressed.equals("ENTER")) {
            return;
        }
        if (combatSystem.isMonsterOnTile() && !Objects.equals(keyPressed, "V") && !Objects.equals(keyPressed, "C")) {
            if (move != null) {
                if (!attemptEscape()) return;
            }
        }
        if(move != null) {
            moveOnLevel(move);
            updateGame.updateGameInfo();
            return;
        }
        handleAction(keyPressed);
    }

    private void handleAction(String keyPressed){
        Runnable runnable = charCommands.get(keyPressed);
        if (runnable != null) {
            runnable.run();
        } else if (Objects.equals(keyPressed, "ENTER")) {
            handleTextCommand(commandGetter.getCommand());
        }
        controller.clearInput();
        updateGame.updateGameInfo();
    }

    private void grabItem() {
        commandState = CommandState.TAKE;
        controller.clearInput();
        if (!mapController.getItems(playerController.getCoords())) {
            controller.UIUpdate("No items on tile", 0);
            commandState = CommandState.NONE;
            return;
        }
        controller.UIUpdate("Which item?", 0);
    }

    private void attack() {
        if (mapController.getMonsters(playerController.getCoords()) == null) {
            controller.UIUpdate("No monster on tile", 0);
            commandState = CommandState.NONE;
            controller.clearInput();
            return;
        }
        controller.UIUpdate("Which monster?", 0);
        commandState = CommandState.ATTACK;
    }

    private void healing() {
        controller.UIUpdate("Which healing item?", 0);
        if (commandState == CommandState.ATTACK) {
            commandState = CommandState.HATTACK;
        } else {
            commandState = CommandState.HEAL;
        }
    }

    private boolean attemptEscape() {
        int number = (int) (Math.floor(Math.random() * 10));
        if (number < 3) {
            controller.UIUpdate("Escape success", 0);
            escape = true;
            commandState = CommandState.NONE;
        } else {
            controller.UIUpdate("Failed Escape!", 0);
            combatSystem.monstersAttack(mapController.getMonstersAttack(playerController.getCoords()));
            return false;
        }
        return true;
    }

    private void handleTextCommand(String command) {
        if (commandState == CommandState.NONE) {
            handleInventoryCommands(command);
        } else {
            executePendingAction(command);
            if (commandState == CommandState.HATTACK) {
                commandState = CommandState.ATTACK;
            } else {
                if (commandState == CommandState.TAKE) {
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
                Messenger messenger = mapController.grabItem(playerController.getCoords(), command);
                switch (messenger.getItemType()) {
                    case 0:
                        controller.UIUpdate("Grabbed weapon: " + messenger.getWeapon().name(), 0);
                        controller.UIUpdate(messenger, 5);
                        playerController.equipWeapon(messenger.getWeapon());
                        break;
                    case 1:
                        controller.UIUpdate("Grabbed armor: " + messenger.getArmor().name(), 0);
                        controller.UIUpdate(messenger, 4);
                        playerController.equipArmor(messenger.getArmor());
                        break;
                    case 2:
                        controller.UIUpdate("Grabbed healing item: " + messenger.getHealingItem().getName(), 0);
                        playerController.addToInventory(messenger);
                        break;
                    default:
                        throw new IllegalArgumentException("Unexpected value: " + messenger.getItemType());
                }
                commandState = CommandState.NONE;
            }
            case HEAL -> inventoryManager.useHealthItem(command);
            case ATTACK -> {
                String message = combatSystem.attack(mapController.attackMonsters(command,
                        playerController.getAttack(), playerController.getCoords())).getMessage();
                if (message != null) {
                    controller.UIUpdate(message, 0);
                }
                combatSystem.monstersAttack(mapController.getMonstersAttack(playerController.getCoords()));
            }
            case PAUSE -> handleInventoryCommands(command);
            default -> controller.UIUpdate("Unexpected command state", 0);
        }
    }
    private void handleInventoryCommands(String command) {
        if (command.equalsIgnoreCase("take")) {
            if (!mapController.getItems(playerController.getCoords())) {
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
    private void moveOnLevel(Movement movement) {
        int deltaX;
        int deltaY;
        int[] playerCoords = playerController.getCoords();
        switch (movement) {
            case LEFT -> {
                deltaX = -1;
                deltaY = 0;
            }
            case RIGHT -> {
                deltaX = 1;
                deltaY = 0;
            }
            case UP -> {
                deltaX = 0;
                deltaY = -1;
            }
            case DOWN -> {
                deltaX = 0;
                deltaY = 1;
            }
            default -> { return;}
        }
        updateMinimap.setVisibility(playerController.movement(deltaX, deltaY,
                mapController.getMapValue(playerCoords[0], playerCoords[1]),
                mapController.getMapValue(playerCoords[0] + deltaX, playerCoords[1] + deltaY)));
        updateMinimap.setDirection(deltaX, deltaY);
        updateMinimap.renderMinimap();
    }
    public void traverseLevels(int dir) {
        if (mapController.getMovement(mapController.getMapValue(playerController.getCoords()[0],
                playerController.getCoords()[1]), 1)) {
            if (dir < 0 && mapController.isCave(mapController.getMapValue(playerController.getCoords()[0],
                    playerController.getCoords()[1]))) {
                mapController.change_level(dir);
                updateMinimap.renderMinimap();
            } else if (dir > 0 && mapController.isLadder(mapController.getMapValue(playerController.getCoords()[0],
                    playerController.getCoords()[1]))) {
                mapController.change_level(dir);
                updateMinimap.renderMinimap();
            } else {
                controller.UIUpdate("Can only go up on a ladder or down on a cave", 0);
            }
        } else {
            controller.UIUpdate("Can only change level on a ladder or a cave", 0);
        }
    }
    public boolean escaped() {
        return escape;
    }
    public void toggleEscape() {
        escape = !escape;
    }
}

