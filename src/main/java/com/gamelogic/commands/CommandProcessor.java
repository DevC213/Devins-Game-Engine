package com.gamelogic.commands;

import com.gamelogic.core.Controller;
import com.gamelogic.combat.CombatSystem;
import com.gamelogic.combat.IMonsters;
import com.gamelogic.core.TileKeyRegistry;
import com.gamelogic.map.*;
import com.gamelogic.map.mapLogic.ICanCross;
import com.gamelogic.map.mapLogic.IDoesDamage;
import com.gamelogic.inventory.IAccessItems;
import com.gamelogic.inventory.InventoryManager;
import com.gamelogic.map.mapLogic.MapController;
import com.gamelogic.messaging.Messenger;
import com.gamelogic.playerlogic.PlayerController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class CommandProcessor{

    private enum CommandState {
        NONE,  // 0 - Not in a command
        TAKE,  // 2 - Taking item
        HEAL,  // 3 - Using health item
        ATTACK, // 4 - Attacking
        HATTACK, //5 - Using health item during fight
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
            "X", () -> traverseLevels(-1),
            "Z", () -> traverseLevels(1)
    );
    private boolean escape = false;
    IGuiEventListener controller;
    IGuiCommandGetter commandGetter;
    PlayerController playerController;
    IUpdateMinimap updateMinimap;
    IUpdateGame updateGame;
    CombatSystem combatSystem;
    InventoryManager inventoryManager;
    IMapState mapState;
    IMonsters monsters;
    IDoesDamage doesDamage;
    IAccessItems accessItems;
    Map<String, TileKey> tileKeyMap;

    public CommandProcessor(Controller controller, PlayerController playerController
            , IUpdateMinimap updateMinimap, IUpdateGame updateGame, CombatSystem combatSystem, InventoryManager inventoryManager,
                            IMonsters  monsters, IAccessItems accessItems, IDoesDamage doesDamage, IMapState mapState) {
        this.controller = controller;
        this.playerController = playerController;
        this.updateMinimap = updateMinimap;
        this.combatSystem = combatSystem;
        this.inventoryManager = inventoryManager;
        this.updateGame = updateGame;
        this.commandGetter = controller;
        this.mapState = mapState;
        this.monsters = monsters;
        this.doesDamage = doesDamage;
        this.accessItems = accessItems;
        tileKeyMap = TileKeyRegistry.getTileKeyList();
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
        controller.clearInput();
    }

    private void handleAction(String keyPressed){
        Runnable runnable = charCommands.get(keyPressed);
        if (runnable != null) {
            runnable.run();
        } else if (Objects.equals(keyPressed, "ENTER")) {
            handleTextCommand(commandGetter.getCommand());
        }
        updateGame.updateGameInfo();
    }

    private void grabItem() {
        commandState = CommandState.TAKE;
        controller.clearInput();
        if (!accessItems.itemsOnTile(playerController.getMapCoordinates())) {
            controller.UIUpdate("No items on tile", 0);
            commandState = CommandState.NONE;
            return;
        }
        controller.UIUpdate("Which item?", 0);
        controller.commandFocus();
    }

    private void attack() {
        if (monsters.getMonsters(playerController.getMapCoordinates()) == null) {
            controller.UIUpdate("No monster on tile", 0);
            commandState = CommandState.NONE;
            return;
        }
        controller.UIUpdate("Which monster?", 0);
        commandState = CommandState.ATTACK;
        controller.commandFocus();
    }

    private void healing() {
        controller.UIUpdate("Which healing item?", 0);
        controller.commandFocus();
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
            combatSystem.monstersAttack(doesDamage.getMonstersAttack(playerController.getMapCoordinates()));
            return false;
        }
        return true;
    }
    private void handleTextCommand(String command) {
            executePendingAction(command);
            if (commandState == CommandState.HATTACK) {
                commandState = CommandState.ATTACK;
            } else {
                if (commandState == CommandState.TAKE) {
                    return;
                }
                commandState = CommandState.NONE;
            }
        controller.clearInput();
    }
    private void executePendingAction(String command) {
        switch (commandState) {
            case TAKE -> takeItem(command);
            case HEAL -> inventoryManager.useHealthItem(command);
            case ATTACK -> attackMonster(command);
            default -> controller.UIUpdate("Unexpected command state", 0);
        }
    }
    private void attackMonster(String command) {
        String message = combatSystem.attack(doesDamage.attackMonsters(command,
                playerController.getAttack(), playerController.getMapCoordinates())).getMessage();
        if (message != null) {
            playerController.monsterKilled();
            controller.UIUpdate(message, 0);
        }
        combatSystem.monstersAttack(doesDamage.getMonstersAttack(playerController.getMapCoordinates()));
        controller.textAreaFocus();
    }
    private void takeItem(String command) {
        Messenger messenger = accessItems.grabItem(playerController.getMapCoordinates(), command);
        if(messenger == null) {
            commandState = CommandState.NONE;
            controller.textAreaFocus();
            return;
        }
        switch (messenger.getItemType()) {
            case 0:
                if(messenger.getWeapon().damage() < playerController.getAttack()){
                    controller.UIUpdate("Current weapon is better.",0);
                    return;
                }
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
        controller.textAreaFocus();
        commandState = CommandState.NONE;
    }

    //Command Processing for movement
    private void moveOnLevel(Movement movement) {
        int deltaX;
        int deltaY;
        Coordinates playerCoords = playerController.getMapCoordinates();
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
        updateMinimap.setVisibility(playerController.movement(new Coordinates(deltaX, deltaY),
                mapState.getMapValue(playerCoords),
                mapState.getMapValue(new Coordinates(playerCoords.x() + deltaX, playerCoords.y() + deltaY))));
        updateMinimap.setDirection(deltaX, deltaY);
        updateMinimap.renderMinimap();
    }
    public void traverseLevels(int dir) {
        TileKey tile = tileKeyMap.get(mapState.getMapValue(playerController.getMapCoordinates()));
        if (tile.walkable()) {
            if (dir > 0 && Objects.equals(tile.name(), "cave")) {
                mapState.changeLevel(dir);
                updateMinimap.renderMinimap();
            } else if (dir < 0 && tile.name().equals("ladder")) {
                mapState.changeLevel(dir);
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

