package com.gamelogic.playerlogic;

import com.armor.Armor;
import com.recoveryitems.RecoveryItem;
import com.savesystem.PlayerState;
import com.weapons.Weapon;
import com.gamelogic.map.Coordinates;
import com.gamelogic.commands.IGuiEventListener;
import com.gamelogic.messaging.Messenger;
import javafx.collections.ObservableList;

import java.util.List;

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
    int level = 0;
    double levelUp = 5;
    int gold;
    private final IGuiEventListener guiEventListener;
    boolean gameOver = false;

    public PlayerController(Coordinates playerLocation, Coordinates maxCoordinates,
                            IGuiEventListener guiEventListener) {
        playerEquipment = new PlayerEquipment();
        playerHealth = new PlayerHealth();
        playerInventory = new PlayerInventory();
        this.guiEventListener = guiEventListener;
        playerMovement = new PlayerMovement(playerLocation,maxCoordinates, this);
        gold = 100;
    }
    public List<String> viewInventory(){
        return playerInventory.viewInventory();
    }
    public void addToInventory(Messenger messenger) {
        playerHealth.addHealthItem(messenger.getHealingItem());
    }
    public int movement(Coordinates delta, String currentTile, String newTile){
        return playerMovement.move(delta,currentTile,newTile);
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
        if (viewInventory() != null){
            for (String i : viewInventory()) {
                sendString.append(i).append("\n");
            }
        }
        if(!(getHealingItems().isEmpty())){
            sendString.append(getHealingItems());
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
        } else {
            if (getHealth() < playerHealth.getSecondaryMaxHealth()) {
                guiEventListener.UIUpdate("Health: " + playerHealth.UpdateHealth(healthDelta), 3);
            } else{
                guiEventListener.UIUpdate("Temporary Health cant be increased farther",0);
            }
        }
    }


    public Coordinates getMapCoordinates() {
        return playerMovement.getMapCoordinates();
    }
    public Coordinates getDisplayCoordinates() {
        return playerMovement.getDisplayCoordinates();
    }
    public double getHealth() {
        return playerHealth.UpdateHealth(0);
    }
    public StringBuilder getHealingItems() {
        return playerHealth.getHealingItems();
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
        guiEventListener.GameOver(false);
    }

    @Override
    public void damage(double damage, String monsterName) {
        double finalDamage;
        if (damage * 1.25 - playerEquipment.getDefence() > 1) {
            finalDamage = damage * 1.25 - playerEquipment.getDefence();
        } else {
            finalDamage = 1.0;
        }
        guiEventListener.UIUpdate( monsterName + " hits you for: " + finalDamage, 0);
        changeHealth(-finalDamage);

        if (getHealth() <= 0) {
            if (getHealingItems().isEmpty()) {
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

    public void monsterKilled(){ //<- this is my XP system
        monstersKilled = monstersKilled + (1 + Math.floor(1.5 * level));
    }
    public void levelUp(){
        if(monstersKilled >= levelUp){
            increaseMaxHealth((playerHealth.getSecondaryMaxHealth() * 0.1));
            guiEventListener.UIUpdate("You gain more confidence, and can withstand more",0);
            playerLevel++;
            levelUp*=1.5;
        }
    }
    public void increaseLevel(){
        level++;
    }

    public void setHealth(int playerHealth) {
        this.playerHealth.setHealth(playerHealth);
        guiEventListener.UIUpdate("Health: " + this.playerHealth.getHealth(), 3);
    }

    public void setCoordinates(int x, int y) {
        playerMovement.setLocation(x,y);
    }
    public void setMaxCoordinates(Coordinates maxCoords) {
        playerMovement.setMaxCoords(maxCoords);
    }

    public void respawn(Coordinates coordinates) {
        setCoordinates(coordinates.x(), coordinates.y());
        gold -= 50;
        playerHealth.setHealth((int)Math.floor(playerHealth.getSecondaryMaxHealth() * .55));
    }
    public int getGold() {
        return gold;
    }

    public PlayerState createPlayerState() {
        PlayerState playerState = new PlayerState();
        playerState.x = getMapCoordinates().x();
        playerState.y = getMapCoordinates().y();
        playerState.health = getHealth();
        playerState.maxHealth = playerHealth.getHealth();
        playerState.secMaxHealth = playerHealth.getSecondaryMaxHealth();

        if (playerEquipment.getArmor() != null) {
            playerState.Armor = playerEquipment.getArmor().name();
            playerState.damage = playerEquipment.getArmor().defence();
        }
        playerState.inventory = playerInventory.viewInventory();
        playerState.healingItems = playerHealth.getHealingItemsMap();
        if (playerEquipment.getWeapon() != null) {
            playerState.sword = playerEquipment.getWeapon().name();
            playerState.damage = playerEquipment.getWeapon().damage();
        }
        playerState.levelUp = levelUp;
        playerState.gold = gold;
        playerState.maxLevel = level;
        return playerState;
    }
    public void loadFromPlayerState(PlayerState playerState) {

        this.setCoordinates(playerState.x,playerState.y);
        playerHealth.setHealthFromFile(playerState.health,playerState.maxHealth,playerState.secMaxHealth);
        playerEquipment.setArmor(new Armor(playerState.Armor, playerState.defence));
        playerInventory.resetInventory();
        playerInventory.addToInventory(playerState.inventory);
        playerHealth.clearHealingItems();
        for(String item: playerState.healingItems.keySet()) {
            int healing = playerState.healingItems.get(item);
            playerHealth.addHealthItem(new RecoveryItem(item,healing));
        }
        playerEquipment.setWeapon(new Weapon(playerState.sword, playerState.damage));
        this.levelUp = playerState.levelUp;
        this.gold = playerState.gold;
        this.level = playerState.maxLevel;
    }

    public int getDefence() {
        return playerEquipment.getDefence();
    }

    public ObservableList<RecoveryItem> getRecoveryItems() {
        return playerHealth.getRecoveryItems();
    }
}
