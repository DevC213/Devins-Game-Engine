package com.adventure_logic.PlayerLogic;

import com.recovery_items.*;

import java.util.Arrays;
import java.util.Objects;
import java.util.Vector;

class PlayerHealth {
    private Vector<RecoveryItem> healing_items;
    private double health;
    public PlayerHealth(){
        health = 100;
        healing_items = new Vector<>(Arrays.asList(new bread(), new health_pot()));
    }
    public double UpdateHealth(double change){
        health += change;
        return health;
    }
    public String useHealthItem(String item){

        for (RecoveryItem i: healing_items) {
            if(Objects.equals(i.getName(), item)){
                if(health == 100){
                    return "You're at max health!";
                }
                if(i.getHealValue() + health > 100){
                    health = 100;
                }else {
                    health += i.use();
                }
                healing_items.remove(i);
                return "Used " + item;
            }
        }
        return null;
    }
    public void reset(){
        health = 100;
        healing_items = new Vector<>(Arrays.asList(new bread(), new health_pot()));
    }
    public void addHealthItem(RecoveryItem item){
        healing_items.add(item);
    }
    public StringBuilder getHealing_items() {
        StringBuilder rtnString = new StringBuilder();
        if(healing_items.isEmpty()){
            return null;
        } else{
            for(RecoveryItem i: healing_items){
                rtnString.append(i.getName()).append("\n");
            }
        }
        return rtnString;
    }
    public void EmergencyUse(){
        if(healing_items.isEmpty()){
            return;
        }
        useHealthItem(healing_items.get(0).getName());

    }
}
