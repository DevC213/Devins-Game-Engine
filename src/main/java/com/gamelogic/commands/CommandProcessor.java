package com.gamelogic.commands;

import com.gamelogic.core.MainGameController;
import com.gamelogic.combat.CombatSystem;
import com.gamelogic.combat.IMonsters;
import com.gamelogic.core.TileKeyRegistry;
import com.gamelogic.map.*;
import com.gamelogic.map.mapLogic.IDoesDamage;
import com.gamelogic.inventory.IAccessItems;
import com.gamelogic.inventory.InventoryManager;
import com.gamelogic.messaging.Messenger;
import com.gamelogic.playerlogic.PlayerController;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;


public class CommandProcessor {


    private String HEALING;
    private String ATTACKING;
    private String GRAB_ITEM;
    private String ENTER_AREA;
    private CommandState commandState = CommandState.NONE;
    Map<String, Runnable> charCommands;
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

    public CommandProcessor(MainGameController mainGameController, PlayerController playerController
            , IUpdateMinimap updateMinimap, IUpdateGame updateGame, CombatSystem combatSystem, InventoryManager inventoryManager,
                            IMonsters monsters, IAccessItems accessItems, IDoesDamage doesDamage, IMapState mapState, @NotNull Keybindings keybindings) {
        this.controller = mainGameController;
        this.playerController = playerController;
        this.updateMinimap = updateMinimap;
        this.combatSystem = combatSystem;
        this.inventoryManager = inventoryManager;
        this.updateGame = updateGame;
        this.commandGetter = mainGameController;
        this.mapState = mapState;
        this.monsters = monsters;
        this.doesDamage = doesDamage;
        this.accessItems = accessItems;
        HEALING = keybindings.heal().toUpperCase();
        ATTACKING = keybindings.attack().toUpperCase();
        GRAB_ITEM = keybindings.grabItem().toUpperCase();
        ENTER_AREA = keybindings.enterArea().toUpperCase();
        charCommands = Map.of(
                HEALING, this::healing,
                ATTACKING, this::attack,
                GRAB_ITEM, this::grabItem,
                ENTER_AREA, this::enterArea,
                "ENTER", () -> handleTextCommand(commandGetter.getCommand())
        );
        tileKeyMap = TileKeyRegistry.getTileKeyList();
    }
    public void changeMapState(IMapState mapState){
        this.mapState = mapState;
    }
    public void handleKeyInput(@NotNull String keyPressed) {
        Movement move = Movement.getMovement(keyPressed.toUpperCase());
        if (commandState != CommandState.NONE && !keyPressed.equals("ENTER")) {
            return;
        }
        if (combatSystem.isMonsterOnTile() && !Objects.equals(keyPressed, ATTACKING) && !Objects.equals(keyPressed, HEALING)) {
            if (move != Movement.DEFAULT) {
                if (!attemptEscape()) return;
            }
        }
        if (move != Movement.DEFAULT) {
            moveOnLevel(move);
            updateGame.updateGameInfo();
            return;
        }
        handleAction(keyPressed);
        controller.clearInput();
    }

    private void handleAction(@NotNull String keyPressed) {
        Runnable runnable = charCommands.get(keyPressed.toUpperCase());
        if (runnable != null) {
            runnable.run();
        }
        updateGame.updateGameInfo();
    }

    private void grabItem() {
        commandState = CommandState.TAKE;
        controller.clearInput();
        if (!accessItems.areItemsOnTile(playerController.getMapCoordinates())) {
            controller.UIUpdate("No items on tile", 0);
        } else{
            String item = accessItems.getItemName(playerController.getMapCoordinates());
            executePendingAction(item);
        }
        commandState = CommandState.NONE;
    }

    private void attack() {

        List<String> monstersOnTile = monsters.getMonsterNames(playerController.getMapCoordinates());
        if (monstersOnTile.isEmpty()) {
            controller.UIUpdate("No monster on tile", 0);
            commandState = CommandState.NONE;
            return;
        } else if (monstersOnTile.size() == 1) {
            executePendingAction(monstersOnTile.getFirst());
            commandState = CommandState.NONE;
            return;
        }
        controller.UIUpdate("AOE or Single", 0);
        commandState = CommandState.ATTACK_CHOICE;
        controller.commandFocus();
    }

    private void healing() {
        controller.UIUpdate("Which healing item?", 0);
        controller.commandFocus();
        if (commandState == CommandState.ATTACK) {
            commandState = CommandState.HEAL_IN_COMBAT;
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
        if (commandState == CommandState.HEAL_IN_COMBAT) {
            commandState = CommandState.ATTACK;
        } else {
            if (commandState == CommandState.TAKE || commandState == CommandState.ATTACK || commandState == CommandState.ATTACK_CHOICE) {
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
            case ATTACK_CHOICE -> attackChoice(command);
            default -> controller.UIUpdate("Unexpected command state", 0);
        }
    }

    private void attackChoice(String command) {
        String choice = command.toLowerCase();
        switch (choice) {
            case("aoe") -> {
                List<Messenger> messengers = doesDamage.attackAllMonsters(playerController.getAttack(), playerController.getMapCoordinates());
                for(Messenger messenger : messengers) {
                    String message = combatSystem.attack(messenger).getMessage();
                    processAttacks(message);
                }
                attack();
            }case("single") -> {
                controller.UIUpdate("Which Monster?", 0);
                commandState = CommandState.ATTACK;
                controller.commandFocus();
            } default ->{
                controller.UIUpdate("Invalid choice", 0);
                controller.UIUpdate("Defaulting to Single. Which Monster?",0);
                commandState = CommandState.ATTACK;
                controller.commandFocus();
            }
        }
    }
    private void attackMonster(String command) {
        String message = combatSystem.attack(doesDamage.attackMonsters(command,
                playerController.getAttack(), playerController.getMapCoordinates())).getMessage();
        processAttacks(message);
        attack();
    }
    private void processAttacks(String message){
        if (message != null) {
            playerController.monsterKilled();
            controller.UIUpdate(message, 0);
        }
        combatSystem.monstersAttack(doesDamage.getMonstersAttack(playerController.getMapCoordinates()));
        controller.textAreaFocus();
    }
    private void takeItem(String command) {
        Messenger messenger = accessItems.grabItem(playerController.getMapCoordinates(), command);
        switch (messenger.getItemType()) {
            case -1:
                commandState = CommandState.NONE;
                controller.textAreaFocus();
                return;
            case 0:
                if (messenger.getWeapon().damage() < playerController.getAttack()) {
                    controller.UIUpdate("Current weapon is better.", 0);
                    return;
                }
                controller.UIUpdate("Grabbed weapon: " + messenger.getWeapon().name(), 0);
                controller.UIUpdate(messenger, 5);
                playerController.equipWeapon(messenger.getWeapon());
                break;
            case 1:
                if(messenger.getArmor().defence() < playerController.getDefence()){
                    controller.UIUpdate("Current armor is better.", 0);
                    return;
                }
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
    private void moveOnLevel(@NotNull Movement movement) {
        int deltaX = movement.dx;
        int deltaY = movement.dy;
        Coordinates playerCoords = playerController.getMapCoordinates();
        updateMinimap.setVisibility(playerController.movement(new Coordinates(deltaX, deltaY),
                mapState.getMapValue(playerCoords),
                mapState.getMapValue(new Coordinates(playerCoords.x() + deltaX, playerCoords.y() + deltaY))));
        updateMinimap.setDirection(deltaX, deltaY);
        updateMinimap.renderMinimap();
    }
    public void enterArea() {
        TileKey tile = tileKeyMap.get(mapState.getMapValue(playerController.getMapCoordinates()));
        if(tile == null) {
            controller.UIUpdate("Invalid tile found.", 0);
            return;
        }
        int levelDelta = tile.levelDelta();
        String mapTile = tile.name();
        if (mapTile.equals("house")) {
            updateMinimap.toggleHouse();
            updateMinimap.renderMinimap();
        }else if (levelDelta != 0) {
            mapState.changeLevel(levelDelta);
            updateMinimap.renderMinimap();
        } else {
            controller.UIUpdate("Can only change level on a ladder, cave, or stairs", 0);
        }
    }
    public boolean escaped() {
        return escape;
    }
    public void toggleEscape() {
        escape = !escape;
    }
    public void updateKeyBindings(@NotNull Keybindings keybindings) {
        HEALING = keybindings.heal();
        ENTER_AREA = keybindings.enterArea();
        ATTACKING = keybindings.attack();
        GRAB_ITEM = keybindings.grabItem();
    }

    public void clearCommandState() {
        commandState = CommandState.NONE;
    }
}
