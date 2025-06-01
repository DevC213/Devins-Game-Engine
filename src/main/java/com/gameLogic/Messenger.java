package com.gameLogic;

import com.Armor.Armor;
import com.Weapons.Weapon;
import com.recoveryItems.HealingItem;

import java.util.Vector;

public class Messenger {
    private String message;
    private Vector<Double> payloadDouble;
    private Weapon weapon;
    private Armor armor;
    private HealingItem healingItem;
    private int itemType = -1;
    public enum MessageType {
        INFO, WARNING, ERROR, EVENT
    }
    public Messenger(){}
    public Messenger(String message) {
        this.message = message;
    }
    public void addPayloadD(Vector<Double> payload){
        payloadDouble = payload;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
    public Vector<Double> getPayloadD() {
        return payloadDouble;
    }
    public Armor getArmor() {
        return armor;
    }
    public void setArmor(Armor armor) {
        itemType = 1;
        this.armor = armor;
    }
    public HealingItem getHealingItem() {
        return healingItem;
    }
    public void setHealingItem(HealingItem healingItem) {
        itemType = 2;
        this.healingItem = healingItem;
    }
    public Weapon getWeapon(){
        return weapon;
    }
    public void setWeapon(Weapon weapon){
        itemType = 0;
        this.weapon = weapon;
    }
    public int getItemType() {
        return itemType;
    }
}
