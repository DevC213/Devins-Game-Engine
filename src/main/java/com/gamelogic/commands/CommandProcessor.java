package com.gamelogic.commands;

import com.gamelogic.combat.CombatSystem;
import com.gamelogic.gameflow.ClassController;
import com.gamelogic.map.IMonsters;
import com.gamelogic.core.TileKeyRegistry;
import com.gamelogic.map.*;
import com.gamelogic.map.mapLogic.IDoesDamage;
import com.gamelogic.inventory.IAccessItems;
import com.gamelogic.map.mapLogic.MapController;
import com.gamelogic.messaging.Messenger;
import com.gamelogic.playerlogic.PlayerController;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class CommandProcessor {


    Map<String, Runnable> charCommands;
    private boolean escapeThisTurn = false;


    //MainGameController
    IGuiEventListener controller;

    //Game Controller
    IUpdateMinimap updateMinimap;
    IUpdateGame updateGame;
    CombatSystem combatSystem;
    PlayerController playerController;

    //mapController
    IMapState mapState;
    IMonsters monsters;
    IDoesDamage doesDamage;
    IAccessItems accessItems;

    Map<String, TileKey> tileKeyMap;

    public CommandProcessor(@NotNull Keybindings keybindings, ClassController classController) {
        this.controller = ClassController.mainGameController;
        this.playerController = classController.playerController;

        this.updateGame = classController.gameController;
        this.updateMinimap = classController.gameController;

        this.combatSystem = classController.combatSystem;

        this.mapState = classController.currentMapController;
        this.monsters = classController.currentMapController;
        this.doesDamage = classController.currentMapController;
        this.accessItems = classController.currentMapController;

        charCommands = Map.of(
                keybindings.grabItem().toUpperCase(), this::grabItem,
                keybindings.enterArea().toUpperCase(), this::enterArea
        );
        tileKeyMap = TileKeyRegistry.getTileKeyList();
    }
    public void changeMapState(MapController mapController) {
        this.mapState = mapController;
        this.monsters = mapController;
        this.doesDamage = mapController;
        this.accessItems = mapController;
    }
    public void handleKeyInput(@NotNull String keyPressed) {
        Movement move = Movement.getMovement(keyPressed.toUpperCase());

        if (monsters.isMonsterOnTile(playerController.getMapCoordinates())) {
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
    }

    private void handleAction(@NotNull String keyPressed) {
        Runnable runnable = charCommands.get(keyPressed.toUpperCase());
        if (runnable != null) {
            runnable.run();
        }
        updateGame.updateGameInfo();
    }
    private void grabItem() {
        if (!accessItems.areItemsOnTile(playerController.getMapCoordinates())) {
            controller.UIUpdate("No items on tile", 0);
        } else{
            String item = accessItems.getItemName(playerController.getMapCoordinates());
            takeItem(item);
        }
    }
    private boolean attemptEscape() {
        int number = (int) (Math.floor(Math.random() * 10));
        if (number < 3) {
            controller.UIUpdate("Escape success", 0);
            escapeThisTurn = true;
        } else {
            controller.UIUpdate("Failed Escape!", 0);
            combatSystem.monstersAttack(doesDamage.getMonstersAttack(playerController.getMapCoordinates()));
            return false;
        }
        return true;
    }

    public void attackMonster(String command) {
        String message = combatSystem.attack(doesDamage.attackMonsters(command,
                playerController.getAttack(), playerController.getMapCoordinates())).getMessage();
        processAttacks(message);
        playerController.levelUp();
    }
    public void AOEAttack(){
        List<Messenger> messengers = doesDamage.attackAllMonsters(playerController.getAttack(), playerController.getMapCoordinates());
        for(Messenger messenger : messengers) {
            String message = combatSystem.attack(messenger).getMessage();
            processAttacks(message);
        }
        playerController.levelUp();
    }
    public void monstersTurn(){
        combatSystem.monstersAttack(doesDamage.getMonstersAttack(playerController.getMapCoordinates()));
    }
    private void processAttacks(String message){
        if (message != null) {
            playerController.monsterKilled();
            controller.UIUpdate(message, 0);
        }
    }
    private void takeItem(String command) {
        Messenger messenger = accessItems.grabItem(playerController.getMapCoordinates(), command);
        switch (messenger.getItemType()) {
            case NONE:
                return;
            case WEAPON:
                if (messenger.getWeapon().damage() < playerController.getAttack()) {
                    controller.UIUpdate("Current weapon is better.", 0);
                    return;
                }
                controller.UIUpdate("Grabbed weapon: " + messenger.getWeapon().name(), 0);
                controller.UIUpdate(messenger.getWeapon().name() + ": " + messenger.getWeapon().damage(), 5);
                playerController.equipWeapon(messenger.getWeapon());
                break;
            case ARMOR:
                if(messenger.getArmor().defence() < playerController.getDefence()){
                    controller.UIUpdate("Current armor is better.", 0);
                    return;
                }
                controller.UIUpdate("Grabbed armor: " + messenger.getArmor().name(), 0);
                controller.UIUpdate(messenger.getArmor().name() + ": " + messenger.getArmor().defence(), 4);
                playerController.equipArmor(messenger.getArmor());
                break;
            case HEALING:
                controller.UIUpdate("Grabbed healing item: " + messenger.getHealingItem().getName(), 0);
                playerController.addToInventory(messenger);
                break;
                //Expansion options for items that are not a weapon, armor, healing. Quest, Key or other items.
            default:
                throw new IllegalArgumentException("Unexpected value: " + messenger.getItemType());
        }
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
        return escapeThisTurn;
    }
    public void toggleEscape() {
        escapeThisTurn = !escapeThisTurn;
    }
}
