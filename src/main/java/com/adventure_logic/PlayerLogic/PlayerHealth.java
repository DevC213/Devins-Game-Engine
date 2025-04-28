package com.adventure_logic.PlayerLogic;

import com.recoveryItems.*;

import java.util.Arrays;
import java.util.Objects;
import java.util.Vector;

class PlayerHealth {
    private Vector<HealingItem> healingItems;
    private double health;
    private double maxHealth;
    private double secondaryMaxHealth;
    public PlayerHealth(){
        health = 100;
        maxHealth = 100;
        secondaryMaxHealth = 150;
        healingItems = new Vector<>(Arrays.asList(new HealingItem("Bread", 10), new HealingItem("Health Pot", 20)));
    }
    public double UpdateHealth(double change){
        health += change;
        return health;
    }
    public String useHealthItem(String item){

        for (HealingItem i: healingItems) {
            if(Objects.equals(i.getName(), item)){
                if(health >= maxHealth){
                    return "Can't consume Healing Item.";
                }else {
                    health += i.use();
                }
                healingItems.remove(i);
                return "Used " + item;
            }
        }
        return null;
    }
    public void reset(){
        health = 100;
        secondaryMaxHealth = 150;
        maxHealth = 100;
        healingItems = new Vector<>(Arrays.asList(new HealingItem("Bread", 10), new HealingItem("Health Pot", 20)));
    }
    public void addHealthItem(HealingItem item){
        healingItems.add(item);
    }
    public StringBuilder getHealing_items() {
        StringBuilder rtnString = new StringBuilder();
        if(healingItems.isEmpty()){
            return null;
        } else{
            for(HealingItem i: healingItems){
                rtnString.append(i.getName()).append("\n");
            }
        }
        return rtnString;
    }
    public void EmergencyUse(){
        if(healingItems.isEmpty()){
            return;
        }
        useHealthItem(healingItems.getFirst().getName());

    }
    public void increaseMaxHealth(double increase){
        maxHealth += increase;
        secondaryMaxHealth += increase;
    }
    public double getSecondaryMaxHealth(){
        return secondaryMaxHealth;
    }

    public double getMaxHealth() {
        return maxHealth;
    }
}
