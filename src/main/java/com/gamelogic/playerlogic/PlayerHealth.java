package com.gamelogic.playerlogic;

import com.recoveryitems.*;

import java.util.*;

class PlayerHealth {
    private List<RecoveryItem> recoveryItems;
    private double health;
    private double maxHealth;
    private double secondaryMaxHealth;
    public PlayerHealth(){
        recoveryItems = new ArrayList<>(Arrays.asList(new RecoveryItem("Bread", 10), new RecoveryItem("Health Pot", 20)));
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
    public void clearHealingItems(){
        recoveryItems.clear();
    }
    public void addHealthItem(RecoveryItem item){
        recoveryItems.add(item);
    }
    public StringBuilder getHealingItems() {
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
    public double getMaxHealth(){
        return maxHealth;
    }
    public void setHealth(int health){
        this.health = health;
        maxHealth = this.health;
        secondaryMaxHealth = health*1.5;
    }
    public void setHealthFromFile(double health, double maxHealth, double secondaryMaxHealth){
        this.health = health;
        this.maxHealth = maxHealth;
        this.secondaryMaxHealth = secondaryMaxHealth;
    }
    public Map<String, Integer> getHealingItemsMap(){
        Map<String, Integer> rtnMap = new HashMap<>();
        for(RecoveryItem i: recoveryItems){
            rtnMap.put(i.getName(), i.getHealValue());
        }
        return rtnMap;
    }

    public double getHealth() {
        return this.health;
    }
}
