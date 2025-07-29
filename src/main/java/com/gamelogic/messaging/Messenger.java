package com.gamelogic.messaging;

import com.armor.Armor;
import com.weapons.Weapon;
import com.recoveryitems.RecoveryItem;

import java.util.List;

public class Messenger {
    private String message;
    private List<Double> payloadDouble;
    private String payloadString;
    private Weapon weapon;
    private Armor armor;
    private RecoveryItem recoveryItem;
    private int itemType = -1;

    public Messenger(){}
    public Messenger(String message) {
        this.message = message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

    public void addListPayloadDouble(List<Double> payload){
        payloadDouble = payload;
    }
    public List<Double> getListPayloadDouble() {
        return payloadDouble;
    }

    public void addPayloadString(String string){
        payloadString = string;
    }
    public String getPayloadString(){
        return payloadString;
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
