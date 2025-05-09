package com.gameLogic.PlayerLogic;

import com.Armor.Armor;
import com.Weapons.Weapon;
import com.gameLogic.IGuiEventListener;
import com.gameLogic.MapLogic.MapController;
import com.gameLogic.Messenger;

import java.util.Vector;

public class PlayerController implements PlayerDamageListener{

    /**
     * Player logic classes
     */
    PlayerMovement playerMovement;
    PlayerEquipment playerEquipment;
    PlayerHealth playerHealth;
    PlayerInventory playerInventory;
    private final IGuiEventListener guiEventListener;
    boolean gameOver = false;
    /**
     * Constructor for player.
     *
     * @param c    starting column
     * @param r    starting row
     * @param maxc max column value
     * @param maxr max row value
     */
    public PlayerController(final int c, final int r, final int maxc, final int maxr,
                            IGuiEventListener guiEventListener, MapController mapController) {
        playerEquipment = new PlayerEquipment();
        playerHealth = new PlayerHealth();
        playerInventory = new PlayerInventory();
        this.guiEventListener = guiEventListener;
        playerMovement = new PlayerMovement(c,r,maxc,maxr, this, mapController);
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
            case 4 -> playerInventory.dropItem(String.join("",items));
            default -> {
            }
        }
        return null;
    }
    public void addToInventory(Messenger messenger) {
        playerHealth.addHealthItem(messenger.getHealingItem());
    }
    public int movement(int deltaX, int deltaY, String currentTile, String newTile){
        return playerMovement.move(deltaX,deltaY,currentTile,newTile);
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
    public void damage(double damage) {
        if (damage > 0) {
            guiEventListener.UIUpdate("Health: " + playerHealth.UpdateHealth(-damage * (100.00 / (100 + playerEquipment.getDefence()))), 3);
             damage(0,0);
        } else {
            if (getHealth() < playerHealth.getSecondaryMaxHealth()) {
                guiEventListener.UIUpdate("Health: " + playerHealth.UpdateHealth(-damage), 3);
            } else{
                guiEventListener.UIUpdate("Temporary Health cant be increased farther",0);
            }
        }
    }


    public int[] getCoords() {
        return playerMovement.getCords();
    }
    public int[] getRCords() {
        return playerMovement.getRCords();
    }
    public double getHealth() {
        return playerHealth.UpdateHealth(0);
    }
    public StringBuilder getHealing_items() {
        return playerHealth.getHealing_items();
    }

    public void resetPlayer(int[] coords) {
        playerMovement.resetLocation(coords);
        playerHealth.reset();
        playerInventory.resetInventory();
        playerEquipment.Reset();
    }

    public int getAttack() {
       return playerEquipment.attack();
    }
    public Weapon getWeapon(){
        return playerEquipment.getWeapon();
    }

    public void equipArmor(Armor armor){
        playerEquipment.setArmor(armor);
    }
    public void gameOver(){
        this.gameOver = true;
        guiEventListener.GameOver(false);
    }

    @Override
    public void damage(double damage, int type) {
        double finalDamage;
        if(type == 1) {
            if (damage * 1.25 - playerEquipment.getDefence() > 1) {
                finalDamage = damage * 1.25 - playerEquipment.getDefence();
            } else {
                finalDamage = 1.0;
            }
            guiEventListener.UIUpdate("Monster hits you for: " + finalDamage, 0);
            damage(finalDamage);
        }
        if (getHealth() <= 0) {
            if (getHealing_items() == null) {
               gameOver();
            } else {
                EmergencyUse();
            }
        }
    }
    public void equipWeapon(Weapon weapon) {
        playerEquipment.setWeapon(weapon);
    }
    public void increaseMaxHealth(double v) {
        guiEventListener.UIUpdate("Health: " + playerHealth.UpdateHealth(20),3);
        playerHealth.increaseMaxHealth(v);
    }
    public void toggleGameOver() {
        gameOver = !gameOver;
    }

    public boolean isGameOver() {
        return gameOver;
    }
}
