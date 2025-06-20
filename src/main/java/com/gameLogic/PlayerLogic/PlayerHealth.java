package com.gameLogic.PlayerLogic;

import com.recoveryItems.*;

import java.util.Arrays;
import java.util.Objects;
import java.util.Vector;

class PlayerHealth {
    private Vector<RecoveryItem> recoveryItems;
    private double health;
    private double maxHealth;
    private double secondaryMaxHealth;
    public PlayerHealth(){
        health = 100;
        maxHealth = 100;
        secondaryMaxHealth = 150;
        recoveryItems = new Vector<>(Arrays.asList(new RecoveryItem("Bread", 10), new RecoveryItem("Health Pot", 20)));
    }
    public double UpdateHealth(double change){
        health += change;
        return health;
    }
    public String useHealthItem(String item){

        for (RecoveryItem i: recoveryItems) {
            if(Objects.equals(i.getName(), item)){
                if(health >= maxHealth){
                    return "Can't consume Healing Item.";
                }else {
                    health += i.use();
                }
                recoveryItems.remove(i);
                return "Used " + item;
            }
        }
        return null;
    }
    public void reset(){
        health = 100;
        secondaryMaxHealth = 150;
        maxHealth = 100;
        recoveryItems = new Vector<>(Arrays.asList(new RecoveryItem("Bread", 10), new RecoveryItem("Health Pot", 20)));
    }
    public void addHealthItem(RecoveryItem item){
        recoveryItems.add(item);
    }
    public StringBuilder getHealing_items() {
        StringBuilder rtnString = new StringBuilder();
        if(recoveryItems.isEmpty()){
            return null;
        } else{
            for(RecoveryItem i: recoveryItems){
                rtnString.append(i.getName()).append(" — ").append(i.getHealValue()).append("\n");
            }
        }
        return rtnString;
    }
    public void EmergencyUse(){
        if(recoveryItems.isEmpty()){
            return;
        }
        useHealthItem(recoveryItems.getFirst().getName());

    }
    public void increaseMaxHealth(double increase){
        maxHealth += increase;
        secondaryMaxHealth += increase;
    }
    public double getSecondaryMaxHealth(){
        return secondaryMaxHealth;
    }

}
