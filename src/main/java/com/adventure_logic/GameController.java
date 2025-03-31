package com.adventure_logic;

import com.Movement.Movement_Controller;
import com.adventure_logic.MapLogic.MapController;
import com.adventure_logic.PlayerLogic.PlayerController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class GameController {

    private final PlayerController playerController;
    private final Controller controller;
    private final MapController mapController;
    private final InventoryManager inventoryManager;
    private final CombatSystem combatSystem;
    private final Movement_Controller movementController;
    private final int[] SQUARE_CHANGE = {-2, -1, 0, 1, 2};
    private int visibility = 2;

    private enum CommandState {
        NONE,  // 0 - Not in a command
        DROP,  // 1 - Dropping item
        TAKE,  // 2 - Taking item
        HEAL,  // 3 - Using health item
        ATTACK // 4 - Attacking
    }
    private CommandState commandState = CommandState.NONE;

    GameController(Controller controller) {
        this.controller = controller;
        this.mapController = new MapController("MapData/Maps/Map1.txt", this.controller);
        this.playerController = new PlayerController(0, 0,
                mapController.getCords()[1], mapController.getCords()[0], controller, mapController);
        this.combatSystem = new CombatSystem(mapController, playerController, controller);
        this.inventoryManager = new InventoryManager(playerController, controller);
        this.movementController = new Movement_Controller();
    }
    public void updateGameInfo() {
        controller.UIUpdate(java.util.Arrays.toString(playerController.getRCords()), 2);
        checkForItems();
        checkForMonsters();
        inventoryManager.updateInventoryDisplay();
    }

    //Command processioning
    public void handleCommands(String keyPressed) {
        if (!commandState.equals(CommandState.NONE) && !keyPressed.equals("ENTER")) {
            return;
        }
        if (combatSystem.isMonsterOnTile() && commandState != CommandState.ATTACK) {
            if(!Objects.equals(keyPressed, "V")) {
                controller.UIUpdate("Can't leave tile, until monster is killed!", 0);
                return;
            }
        }
        switch (keyPressed) {
            case "LEFT" -> movementController.direction("west");
            case "RIGHT" -> movementController.direction("east");
            case "DOWN" -> movementController.direction("south");
            case "UP" -> movementController.direction("north");
            case "Z" -> move(-1);
            case "X" -> move(1);
            case "C" -> {
                controller.UIUpdate("Which healing item?", 0);
                commandState = CommandState.HEAL;
            }
            case "V" -> {
                if(mapController.getMonsters(playerController.getCords()) == null){
                    controller.UIUpdate("No monster on tile", 0);
                    commandState = CommandState.NONE;
                    return;
                }
                controller.UIUpdate("Which monster?", 0);
                commandState = CommandState.ATTACK;
            }
            default -> {if (Objects.equals(keyPressed, "ENTER")) {processCommand(controller.getCommand());}}
        }

        updateGameInfo();
    }
    private void processCommand(String command) {
        if(commandState.equals(CommandState.NONE)) {
            handleGameCommand(command);
        } else {
            executeCommand(command);
            commandState = CommandState.NONE;
        }
        controller.clearInput();
    }
    private void executeCommand(String command) {
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
    private void handleGameCommand(String command) {
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

    //Movement
    public void move(int movement, char dir){
        switch (dir) {
            case 'r':
                playerController.movement(movement,1);
                break;
            case 'c':
                playerController.movement(movement,2);
                break;
            default:
                break;
        }
        minimap();
    }
    public void move(int dir){
        if(mapController.getMovementOrDamage(mapController.getMapValue(playerController.getCords()[0],
                playerController.getCords()[1]),1)) {
            if(dir < 0 && mapController.isCave(mapController.getMapValue(playerController.getCords()[0],
                    playerController.getCords()[1]))){
                mapController.change_level(dir);
                minimap();
            }
            else if(dir > 0 && mapController.isLadder(mapController.getMapValue(playerController.getCords()[0],
                    playerController.getCords()[1]))){
                mapController.change_level(dir);
                minimap();
            }
            else{
                controller.UIUpdate("Can only go up on a ladder or down on a cave",0);
            }
        } else{
            controller.UIUpdate("Can only change level on a ladder or a cave",0);
        }
        controller.clearInput();
    }

    //Mini-Map Control
    public void minimap() {
        String player;
        try {
            createBlend(playerController);
            player = "2";
        } catch (Exception e){
            player = "1";
        }
        for (int j = 0; j < SQUARE_CHANGE.length; j++) {
            for (int k = 0; k < SQUARE_CHANGE.length; k++) {
                if(Math.abs(SQUARE_CHANGE[j]) > visibility || Math.abs(SQUARE_CHANGE[k]) > visibility ){
                    controller.modifyImage(k,j,mapController.getImage("?"));
                } else if (SQUARE_CHANGE[j] == 0 && SQUARE_CHANGE[k] == 0) {
                    controller.modifyImage(k, j, mapController.getImage(player));
                } else {
                    controller.modifyImage(k, j, mapController.getImage(mapController.getMapValue(playerController.getCords()[0]
                            + SQUARE_CHANGE[j],playerController.getCords()[1] + SQUARE_CHANGE[k])));
                }
            }
        }
    }
    private void createBlend(final PlayerController plays) throws IOException {
        BufferedImage player;
        BufferedImage tile;
        BufferedImage blend;
        int imageWidth;
        int imageHeight;
        Graphics merger;

        String valAtPlayer = mapController.getMapValue(plays.getCords()[0], plays.getCords()[1]);
        player = ImageIO.read(new File(mapController.getImage("1")));
        tile = ImageIO.read(new File(mapController.getImage(valAtPlayer)));
        imageWidth = Math.max(player.getWidth(),tile.getWidth());
        imageHeight = Math.max(player.getHeight(), tile.getHeight());
        blend = new BufferedImage(imageWidth,imageHeight,BufferedImage.TYPE_INT_ARGB);

        merger = blend.getGraphics();
        merger.drawImage(tile,0,0,null);
        merger.drawImage(player,0,0,null);
        merger.dispose();

        ImageIO.write(blend,"PNG", new File("MapPics", "PlayerBlend"));
    }
    public void setVisibility(int visibility) {
        this.visibility = visibility;
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
        } else if (combatSystem.isMonsterOnTile()) {
            controller.UIUpdate("Monster Killed", 0);
            combatSystem.toggleMonster();
        }
    }
    public void newGame() {
        playerController.resetPlayer();
        mapController.setLevel(0);
        mapController.resetMap();
        updateGameInfo();
    }

    //facade & communication functions
    public int[] getCords(){return mapController.getCords();}
    public double getHealth(){return playerController.getHealth();}


    /*
        Un-used memthods, that may come in handy.

        public void resumeGame() {
        if (playerController.getHealth() < 1) {
            System.out.println(playerController.getHealing_items());
            if (playerController.getHealing_items() == null) {
                controller.GameOver();
            } else {
                playerController.EmergencyUse();
            }
        }
    }

     */

}

