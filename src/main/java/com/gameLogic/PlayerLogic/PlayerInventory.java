package com.gameLogic.PlayerLogic;

import java.util.Arrays;
import java.util.Vector;

class PlayerInventory {

    private Vector<String> inventory;

    PlayerInventory(){ inventory  = new Vector<>(); }
    public Vector<String> viewInventory() {
        if (inventory.isEmpty()) {
            return null;
        }
        return inventory;
    }

    public void addToInventory(final String[] items) {
        inventory.addAll(Arrays.asList(items));
    }
    public void dropItem(final String item) {
        inventory.remove(item);
    }

    public void resetInventory(){
        inventory  = new Vector<>();
    }

}
