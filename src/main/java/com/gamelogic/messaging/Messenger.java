package com.gamelogic.messaging;

import com.armor.Armor;
import com.gamelogic.commands.ItemType;
import com.monsters.Monster;
import com.weapons.Weapon;
import com.recoveryitems.RecoveryItem;

import java.util.List;

public class Messenger {
    private String message;
    private List<Monster> monsters;
    private List<Double> payloadDouble;
    private String payloadString;
    private Weapon weapon;
    private Armor armor;
    private RecoveryItem recoveryItem;
    private ItemType itemType = ItemType.NONE;

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
    public void addMonsters(List<Monster> monsters) {
        this.monsters = monsters;
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
        itemType = ItemType.ARMOR;
        this.armor = armor;
    }

    public RecoveryItem getHealingItem() {
        return recoveryItem;
    }
    public void setHealingItem(RecoveryItem recoveryItem) {
        itemType = ItemType.HEALING;
        this.recoveryItem = recoveryItem;
    }

    public Weapon getWeapon(){
        return weapon;
    }
    public void setWeapon(Weapon weapon){
        itemType = ItemType.WEAPON;
        this.weapon = weapon;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public List<Monster> getMonsters() {
        return monsters;
    }
}
