package com.adventure_logic.PlayerLogic;

import com.adventure_logic.Adventure;
import com.adventure_logic.GuiEventListener;

import java.util.Vector;

public class PlayerController {

    /**
     * Player logic classes
     */
    PlayerMovement playerMovement;
    PlayerEquipment playerEquipment;
    PlayerHealth playerHealth;
    PlayerInventory playerInventory;
    private final GuiEventListener guiEventListener;
    /**
     * Constructor for player.
     *
     * @param c    starting column
     * @param r    starting row
     * @param maxc max column value
     * @param maxr max row value
     */
    public PlayerController(final int c, final int r, final int maxc, final int maxr, GuiEventListener guiEventListener) {
        playerEquipment = new PlayerEquipment();
        playerHealth = new PlayerHealth();
        playerInventory = new PlayerInventory();
        this.guiEventListener = guiEventListener;
        playerMovement = new PlayerMovement(c,r,maxc,maxr, this);
    }
    public Vector<String> InventoryCommands(String[] items, int command){
        switch (command) {
            case 1, 2 -> {
                playerInventory.addToInventory(items);
                return null;
            }
            case 3 -> {
                return playerInventory.viewInventory();
            }
            case 4 ->{
                playerInventory.dropItem(String.join("",items));
            }
            default -> {
            }
        }
        return null;
    }

    public void movement(int movement, int command){
        switch (command) {
            case 1 -> playerMovement.changeRow(movement);
            case 2 -> playerMovement.changeColumn(movement);
            default -> {
            }
        }
    }
    public StringBuilder getHealing_items() {
        return playerHealth.getHealing_items();
    }
    public void useHealing(String item){
        String rtnString = playerHealth.useHealthItem(item);
        if (rtnString != null){
            guiEventListener.UIUpdate("Health: " + playerHealth.UpdateHealth(0),3);
            guiEventListener.UIUpdate(rtnString,0);
            return;
        }
        guiEventListener.UIUpdate("Item not in inventory",0);
    }
    public void EmergencyUse(){
        playerHealth.EmergencyUse();
        guiEventListener.UIUpdate("Out of Health, using healing item.", 0);
        StringBuilder sendString = new StringBuilder();
        if (InventoryCommands(null,3) != null){
            for (String i : InventoryCommands(null,3)) {
                sendString.append(i).append("\n");
            }
        }
        if(!(getHealing_items() == null)){
            sendString.append(getHealing_items());
        }
        guiEventListener.UIUpdate(sendString.toString(),1);
        guiEventListener.UIUpdate("Health: " + playerHealth.UpdateHealth(0),3);
    }
    public void sendMessage(String message){
        guiEventListener.UIUpdate(message,0);
    }
    public void damage(double damage){
        guiEventListener.UIUpdate("Health: " + playerHealth.UpdateHealth(-damage*(100.00/(100+playerEquipment.getDefence()))),3);
    }
    /**
     * gets players coordinates as an array.
     * @return coordinates as array
     */
    public int[] getCords() {
        return playerMovement.getCords();
    }
    public int[] getRCords() {
        return playerMovement.getRCords();
    }
    public double getHealth() {
        return playerHealth.UpdateHealth(0);
    }
    public void resetPlayer(){
        playerMovement.resetLocation();
        playerHealth.reset();
        playerInventory.resetInventory();
        playerEquipment.Reset();

    }

    public int getAttack() {
       return playerEquipment.attack();
    }

    public void gameOver(){
        guiEventListener.GameOver();
    }
}
