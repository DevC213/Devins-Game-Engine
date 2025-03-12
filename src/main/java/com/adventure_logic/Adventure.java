package com.adventure_logic;

import com.Commands.Command_Controller;
import com.Movement.Movement_Controller;
import com.adventure_logic.MapLogic.MapController;
import com.adventure_logic.PlayerLogic.PlayerController;

import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

/* Todo:
        High priority:
            Full working attack w/ finding weapons
            Full working defence w/ finding armor
       Medium priority:
            More monsters across levels
            Lower levels
            Win condition
            Create bigger maps
            clean up last classes
        Low Priority:
            Lantern + changing visibility
            Cleanup warnings,
            Cleanup GUI
*/

public class Adventure {


    /**
     * values for command location.
     * 0 - > Continue on,
     * 1 -> Pickup
     * 2 -> Drop Item
     * 3 -> Use Health Item
     * 4 -> attack
     * "1" & "2" are used internally
     */
    /*
        Adventure should start and restart the game
     */
    private static Adventure adventure = getAdventure();
    private Command_Controller command_controller;
    public PlayerController playerController;
    public MapController gameMapController;
    public Controller controller;
    public GuiEventListener guiEventListener;

    private final Movement_Controller mover = new Movement_Controller();
    private final int CONTINUE_GAME = 0;
    public  int commandLocation = CONTINUE_GAME;
    private String command = "";
    private  boolean monsterOnTile = false;
    private final int[] COLUMN_CHANGE = {-2, -1, 0, 1, 2};
    private final int[] ROW_CHANGE = {-2, -1, 0, 1, 2};
    private boolean commandControllerSet = false;
    private int visibility = 2;


    //Constructor/initialization methods
    private Adventure() {
    }
    public static synchronized Adventure getAdventure(){
        if (adventure == null){
            adventure = new Adventure();
        }
        return adventure;
    }
    public void setController(final Controller control) {
        this.controller = control;
        gameMapController = new MapController("MapData/Maps/Map1.txt");
        playerController = new PlayerController(0, 0,
                gameMapController.getCords()[1], gameMapController.getCords()[0], controller);
        controller.UIUpdate(Arrays.toString(playerController.getCords()),2);
    }
    public void toggleMonster(){
        monsterOnTile = !monsterOnTile;
    }
    public boolean getMonster(){
        return monsterOnTile;
    }

    //Game progression controls
    public void intro() {
         controller.UIUpdate("""
                 You find yourself lost on island, explore around to find
                 what this Island has to offer but you know you need to hurry
                 you hear noises coming from the caves that you can see.
                 """,0);
        minimap();
        controller.UIUpdate("""                                                        
                                Enter Take or Drop to pickup or drop item.
                                Then press enter. Use z to enter cave, and x
                                to climb ladder.
                         
                                Use arrow keys for movement, Inventory is on
                                Side of map.
                                """,0);
        controller.clearInput();
        updateInventory();
    }
    public void launchGame() {
        controller = new Controller();
        guiEventListener = controller;
        guiEventListener.UIUpdate("Welcome to Devin's Text adventure"
                + "Game press 'start game' to begin:\n",0);
    }
    public void resumeGame() {
        if(playerController.getHealth() < 1){
            System.out.println(playerController.getHealing_items());
            if (playerController.getHealing_items()== null) {
                controller.GameOver();
            } else {
                playerController.EmergencyUse();
            }
        } else {
            command = controller.getCommand();
            if (!commandControllerSet) {
                command_controller = new Command_Controller(adventure);
                commandControllerSet = true;
            }
            commandLocation = command_controller.Command(command);
            if (commandLocation == 0) {
                writeCommands();
            }
            updateInventory();
        }
    }
    public void resetGame(){
        playerController.resetPlayer();
        updateHealth(playerController.getHealth());
        updateInventory();
        String cordOrigins = "[" + (-getMapSize()[1] / 2) + (-getMapSize()[0]/2) + "]";
        controller.UIUpdate(cordOrigins,2);
        gameMapController.setLevel(0);
        gameMapController.resetMap();
    }


    //input processing
    public void control(String keyPressed) {
        final int attacking = 4;
        final ArrayList<String> directions = new ArrayList<>() {
            {
                add("LEFT");
                add("UP");
                add("DOWN");
                add("RIGHT");
            }
        };
        if (commandLocation == attacking && !Objects.equals(keyPressed, "ENTER")) {
            return;
        }
        if (directions.contains(keyPressed)) {
            movement(keyPressed);
        }
        switch (keyPressed.toLowerCase(Locale.ROOT)) {
            case "z" -> mapChange(-1);
            case "x" -> mapChange(1);
            case "c" -> useHealthPot();
            case "v" -> attack();
            default -> {if (Objects.equals(keyPressed, "ENTER")) {enterProcessor();}}
        }
    }
    public void enterProcessor(){
        switch (commandLocation) {
            case 0 -> {
                resumeGame();
                clear();
            }
            case 1 -> {
                dropItem();
                clear();
            }
            case 2 -> {
                takeItem();
                clear();
            }
            case 3 -> usingHealing();
            case 4 -> attacking();
        }

    }


    //movement methods & minimap update
    public void movement(final String direction) {

        if(monsterOnTile){
            controller.UIUpdate("Can't leave tile, until monster is killed!",0);
            return;
        }
        switch (direction) {
            case "LEFT" -> mover.direction("west");
            case "RIGHT" -> mover.direction("east");
            case "DOWN" -> mover.direction("south");
            case "UP" -> mover.direction("north");
            default -> {/**/}
        }
       minimapItems();
    }
    public boolean getCanCross(final int[] cords){
        return gameMapController.getMovementOrDamage(gameMapController.getMapValue(cords[0],cords[1]),2);
    }
    public boolean getDoesDamage(final int[] cords){
        return gameMapController.getMovementOrDamage(gameMapController.getMapValue(cords[0],cords[1]),0);
    }
    public int tileDamage(final int[] cords){
        return gameMapController.Damage(gameMapController.getMapValue(cords[0],cords[1]));
    }
    public void movePlayer(int movement, char dir){
        if(dir == 'c'){
            playerController.movement(movement,2);
        } else if(dir == 'r'){
            playerController.movement(movement,1);
        }
    }


    //Inventory Controls
    public void dropItem() {
        command = controller.getCommand();
        if(playerController.InventoryCommands(null, 3).isEmpty()){
            controller.UIUpdate("Your inventory is empty",0);
        } else if (playerController.InventoryCommands(null,3).contains(command)) {
            controller.UIUpdate("Dropping Item...",0);
            gameMapController.addItem(playerController.getCords(), command);
            playerController.InventoryCommands(new String[]{command}, 4);
        } else {
            controller.UIUpdate("Nothing dropped",0);
        }
        commandLocation = CONTINUE_GAME;
        updateInventory();
        minimapItems();
    }
    public void dropItem(String item){
        controller.UIUpdate("Dropping: " + item,0);
        gameMapController.addItem(playerController.getCords(), item);
        playerController.InventoryCommands(item.split(""), 4);
    }
    public void takeItem() {
        String item = gameMapController.grabItem(playerController
                .getCords(), controller.getCommand());
        if(item == null){
            controller.UIUpdate("Items left",0);
        }else if (item.split(",").length > 1) {
            playerController.InventoryCommands(item.split(","),1);
            controller.UIUpdate("Items Added",0);
        } else if(item.split(",").length == 1) {
            playerController.InventoryCommands(new String[]{item}, 1);
            controller.UIUpdate("Item Added",0);
        }else{
            controller.UIUpdate("Items left",0);
        }
        commandLocation = 0;
        updateInventory();
        minimapItems();
    }


    //Map Controls
    public void mapChange(int dir){
        if(gameMapController.getMovementOrDamage(gameMapController.getMapValue(playerController.getCords()[0],
                playerController.getCords()[1]),1)) {
             if(dir < 0 && gameMapController.isCave(gameMapController.getMapValue(playerController.getCords()[0],
                     playerController.getCords()[1]))){
                 gameMapController.change_level(dir);
                 minimap();
            }
            else if(dir > 0 && gameMapController.isLadder(gameMapController.getMapValue(playerController.getCords()[0],
                    playerController.getCords()[1]))){
                gameMapController.change_level(dir);
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
    public void changeVisibility(int val){
        visibility = val;
    }
    public void minimap() {
        String player;
        try {
            createBlend(playerController);
            player = "2";
        } catch (Exception e){
            player = "1";
        }
        for (int j = 0; j < COLUMN_CHANGE.length; j++) {
            for (int k = 0; k < ROW_CHANGE.length; k++) {
                if(Math.abs(COLUMN_CHANGE[j]) > visibility || Math.abs(ROW_CHANGE[k]) > visibility ){
                    controller.modifyImage(k,j,gameMapController.getImage("?"));
                } else if (COLUMN_CHANGE[j] == 0 && ROW_CHANGE[k] == 0) {
                    controller.modifyImage(k, j, gameMapController.getImage(player));
                } else {
                    controller.modifyImage(k, j, gameMapController.getImage(gameMapController.getMapValue(playerController.getCords()[0]
                            + COLUMN_CHANGE[j],playerController.getCords()[1] + ROW_CHANGE[k])));
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

        String valAtPlayer = gameMapController.getMapValue(plays.getCords()[0], plays.getCords()[1]);
        player = ImageIO.read(new File(gameMapController.getImage("1")));
        tile = ImageIO.read(new File(gameMapController.getImage(valAtPlayer)));
        imageWidth = Math.max(player.getWidth(),tile.getWidth());
        imageHeight = Math.max(player.getHeight(), tile.getHeight());
        blend = new BufferedImage(imageWidth,imageHeight,BufferedImage.TYPE_INT_ARGB);

        merger = blend.getGraphics();
        merger.drawImage(tile,0,0,null);
        merger.drawImage(player,0,0,null);
        merger.dispose();

        ImageIO.write(blend,"PNG", new File("MapPics", "PlayerBlend"));
    }


    //Health & Game over Methods
    public void useHealthPot() {
        commandLocation = 3;
        controller.clearInput();
        controller.UIUpdate("Enter Item to use:", 0);
    }
    public void usingHealing(){
        String item = controller.getCommand();
        controller.clearInput();
        if(item == null || item.isEmpty()){
            controller.UIUpdate("No healing item entered.",0);
            commandLocation = 0;
            return;
        }
        playerController.useHealing(item);
        updateInventory();
    }
    public void gameOver(){
        monsterOnTile = false;
    }


    //Non-static Methods
    public Vector<String> getMonsters(){return gameMapController.getMonsters(playerController.getCords());}
    public Vector<String> getItems(){return gameMapController.getItems(playerController.getCords());}
    public int getInventoryTotal(){

        return playerController.InventoryCommands(null,3).size();
    }
    public int[] getMapSize(){return gameMapController.getCords();}


    //Gui Manipulation

    public void updateHealth(double health){
        controller.UIUpdate("Health: " + health,3);
    }
    public void updateInventory(){
        StringBuilder sendString = new StringBuilder();
        if (playerController.InventoryCommands(null,3) != null){
            for (String i : playerController.InventoryCommands(null,3)) {
                sendString.append(i).append("\n");
            }
        }
        if(!(playerController.getHealing_items() == null)){
            sendString.append(playerController.getHealing_items());
        }
        controller.UIUpdate(sendString.toString(),1);
    }
    public void sendMessage(String message){
        controller.UIUpdate(message,0);
    }
    public void minimapItems(){
        minimap();
        controller.UIUpdate(Arrays.toString(playerController.getRCords()) ,2);
        if (gameMapController.getItems(playerController.getCords()) != null) {
            controller.UIUpdate("Items at location: " +
                    gameMapController.getItems(playerController.getCords()) ,0);
        } else if (gameMapController.getMonsters(playerController.getCords()) != null) {
            controller.UIUpdate("Monsters at location: " +
                    gameMapController.getMonsters(playerController.getCords()),0);
            if(!getMonster()) {
                toggleMonster();
            }
        } else if(gameMapController.getMonsters(playerController.getCords()) == null
                || gameMapController.getMonsters(playerController.getCords()).isEmpty() ) {
            if(getMonster()){
                controller.UIUpdate("Monster Killed",0);
            }
            if(getMonster()) {
                toggleMonster();
            }
        }
    }
    public void writeCommands() {

        controller.UIUpdate("Enter a command: Take, Drop, or View Inventory. " +
                        "Then press enter.",0);
    }
    public void clear() {controller.clearInput();
    }

    //Monster Functions

    /**
     * needs:
     * Controller
     * player Controller
     * Map Controller
     */
    public void attack() {
        controller.UIUpdate("What monster?", 0);
        commandLocation = 4;
        controller.clearInput();
    }
    public void attacking() {
        String monster = controller.getCommand();
        controller.clearInput();
        if (Objects.equals(monster, "") || monster == null) {
            controller.UIUpdate("Missed", 0);
            commandLocation = 0;
            return;
        }
        controller.clearInput();
        gameMapController.attackMonster(monster, playerController.getAttack(), playerController.getCords());
        minimapItems();
        commandLocation = 0;
        for (Double i : gameMapController.getMonstersAttacks(playerController.getCords())) {
            MonsterAttack(i);
        }
    }
    public void MonsterAttack(Double damage) {
        controller.UIUpdate("Monster hits you for: " + damage, 0);
        playerController.damage(damage);
        if (playerController.getHealth() <= 0) {
            if (playerController.getHealing_items() == null) {
                gameOver();
            } else {
                playerController.EmergencyUse();
            }
        }
    }

}
