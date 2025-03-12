package com.adventure_logic;

import com.adventure_logic.PlayerLogic.PlayerController;

import java.util.Arrays;

public class GUIControl {

    /**
        To-do:
        PlayerController
        Controller
     */
    private GUIControl(){}
    // GUI/Game control
    public static void sendMessage(String message){
        Adventure.getController().updateGUI(message,0);
    }

    public static void gameOver(){
        Adventure.getController().gameOver();
        Adventure.gameOver();
    }

    public static void clear() {
        Adventure.getController().clearInput();
    }

    public static void updateInventory(){
        StringBuilder sendString = new StringBuilder();
        if (Adventure.getPlayerController().InventoryCommands(null,3) != null){
            for (String i : Adventure.getPlayerController().InventoryCommands(null,3)) {
                sendString.append(i).append("\n");
            }
        }
        if(!(Adventure.getPlayerController().getHealing_items() == null)){
            sendString.append(Adventure.getPlayerController().getHealing_items());
        }
        Adventure.getController().updateGUI(sendString.toString(),1);
    }
    public static void updateHealth(double health){
        Adventure.getController().updateGUI("Health: " + health,3);





    }
    public static void resetGame(){
        Adventure.getPlayerController().resetPlayer();
        updateHealth(Adventure.getPlayerController().getHealth());
        updateInventory();
        String cordOrigins = "[" + (-Adventure.getMapSize()[1] / 2) + (-Adventure.getMapSize()[0]/2) + "]";
        Adventure.getController().updateGUI(cordOrigins,2);
        Adventure.getGameMapController().setLevel(0);
        Adventure.getGameMapController().resetMap();
    }
    public static void writeCommands() {

        Adventure.getController()
                .updateGUI("Enter a command: Take, Drop, or View Inventory. " +
                        "Then press enter.",0);
    }
    public static void minimapItems(){
        Adventure.minimap();
        Adventure.getController().updateGUI(Arrays.toString(Adventure.getPlayerController().getRCords()) ,2);
        if (Adventure.getGameMapController().getItems(Adventure.getPlayerController().getCords()) != null) {
            Adventure.getController().updateGUI("Items at location: " +
                    Adventure.getGameMapController().getItems(Adventure.getPlayerController().getCords()) ,0);
        } else if (Adventure.getGameMapController().getMonsters(Adventure.getPlayerController().getCords()) != null) {
            Adventure.getController().updateGUI("Monsters at location: " +
                    Adventure.getGameMapController().getMonsters(Adventure.getPlayerController().getCords()),0);
            if(!Adventure.getMonster()) {
                Adventure.toggleMonster();
            }
        } else if(Adventure.getGameMapController().getMonsters(Adventure.getPlayerController().getCords()) == null
                || Adventure.getGameMapController().getMonsters(Adventure.getPlayerController().getCords()).isEmpty() ) {
            if(Adventure.getMonster()){
                Adventure.getController().updateGUI("Monster Killed",0);
            }
            if(Adventure.getMonster()) {
                Adventure.toggleMonster();
            }
        }
    }


}
