package com.gamelogic.messaging;

import com.armor.Armor;
import com.weapons.Weapon;
import com.recoveryitems.RecoveryItem;

import java.util.Vector;

public class Messenger {
    private String message;
    private Vector<Double> payloadDouble;
    private Weapon weapon;
    private Armor armor;
    private RecoveryItem recoveryItem;
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
    public RecoveryItem getHealingItem() {
        return recoveryItem;
    }
    public void setHealingItem(RecoveryItem recoveryItem) {
        itemType = 2;
        this.recoveryItem = recoveryItem;
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
