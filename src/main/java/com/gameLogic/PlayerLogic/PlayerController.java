package com.gameLogic.PlayerLogic;

import com.Armor.Armor;
import com.Weapons.Weapon;
import com.gameLogic.Coordinates;
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
    double monstersKilled = 0;
    int playerLevel = 1;
    int mapLevel = 0;
    double levelUp = 5;
    private final IGuiEventListener guiEventListener;
    boolean gameOver = false;

    public PlayerController(Coordinates playerLocation, Coordinates maxCoordinates,
                            IGuiEventListener guiEventListener, MapController mapController) {
        playerEquipment = new PlayerEquipment();
        playerHealth = new PlayerHealth();
        playerInventory = new PlayerInventory();
        this.guiEventListener = guiEventListener;
        playerMovement = new PlayerMovement(playerLocation,maxCoordinates, this, mapController);
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
    public void changeHealth(double healthDelta) {
        if (healthDelta < 0) {
            guiEventListener.UIUpdate("Health: " + playerHealth.UpdateHealth(healthDelta * (100.00 / (100 + playerEquipment.getDefence()))), 3);
             damage(0,0);
        } else {
            if (getHealth() < playerHealth.getSecondaryMaxHealth()) {
                guiEventListener.UIUpdate("Health: " + playerHealth.UpdateHealth(healthDelta), 3);
            } else{
                guiEventListener.UIUpdate("Temporary Health cant be increased farther",0);
            }
        }
    }


    public Coordinates getCoords() {
        return playerMovement.getCords();
    }
    public Coordinates getRCords() {
        return playerMovement.getRCords();
    }
    public double getHealth() {
        return playerHealth.UpdateHealth(0);
    }
    public StringBuilder getHealing_items() {
        return playerHealth.getHealing_items();
    }

    public void resetPlayer(Coordinates coords) {
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
            changeHealth(-finalDamage);
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

    public void monsterKilled(){
        monstersKilled = monstersKilled + (1 + Math.floor(1.5 * mapLevel));
        if(monstersKilled >= levelUp){
            increaseMaxHealth((playerHealth.getSecondaryMaxHealth() * 0.1));
            guiEventListener.UIUpdate("You gain more confidence, and can withstand more",0);
            playerLevel++;
            levelUp*=1.5;
        }
    }
    public void increaseLevel(){
        mapLevel++;
    }
}
