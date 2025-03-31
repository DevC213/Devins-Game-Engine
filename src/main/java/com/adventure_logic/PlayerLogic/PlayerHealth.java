package com.adventure_logic.PlayerLogic;

import com.recovery_items.*;

import java.util.Arrays;
import java.util.Objects;
import java.util.Vector;

class PlayerHealth {
    private Vector<healingItem> healingItems;
    private double health;
    public PlayerHealth(){
        health = 100;
        healingItems = new Vector<>(Arrays.asList(new healingItem("Bread", 10), new healingItem("Health Pot", 20)));
    }
    public double UpdateHealth(double change){
        health += change;
        return health;
    }
    public String useHealthItem(String item){

        for (healingItem i: healingItems) {
            if(Objects.equals(i.getName(), item)){
                if(health == 100){
                    return "You're at max health!";
                }
                if(i.getHealValue() + health > 100){
                    health = 100;
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
        healingItems = new Vector<>(Arrays.asList(new healingItem("Bread", 10), new healingItem("Health Pot", 20)));
    }
    public void addHealthItem(healingItem item){
        healingItems.add(item);
    }
    public StringBuilder getHealing_items() {
        StringBuilder rtnString = new StringBuilder();
        if(healingItems.isEmpty()){
            return null;
        } else{
            for(healingItem i: healingItems){
                rtnString.append(i.getName()).append("\n");
            }
        }
        return rtnString;
    }
    public void EmergencyUse(){
        if(healingItems.isEmpty()){
            return;
        }
        useHealthItem(healingItems.get(0).getName());

    }
}
