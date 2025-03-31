package com.adventure_logic.PlayerLogic;

import java.util.Arrays;
import java.util.Vector;

class PlayerInventory {

    private Vector<String> inventory;

    PlayerInventory(){
        inventory  = new Vector<>(Arrays.asList("brass", "lantern", "rope"));
    }
    public Vector<String> viewInventory() {
        if (inventory.isEmpty()) {
            return null;
        }
        return inventory;
    }

    public void addToInventory(final String[] items) {
        for(String i: items) {
            inventory.add(i);
        }
    }
    public boolean itemExists(final String items) {
        return inventory.contains(items);
    }
    public void dropItem(final String item) {
        inventory.remove(item);
    }

    public void resetInventory(){
        inventory  = new Vector<>(Arrays.asList("brass", "lantern", "rope"));
    }

}
