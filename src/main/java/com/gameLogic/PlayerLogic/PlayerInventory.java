package com.gameLogic.PlayerLogic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

class PlayerInventory {

    private List<String> inventory;

    PlayerInventory(){ inventory  = new ArrayList<>(); }
    public List<String> viewInventory() {
        if (inventory.isEmpty()) {
            return null;
        }
        return inventory;
    }

    public void addToInventory(final String[] items) {
        inventory.addAll(Arrays.asList(items));
    }

    public void resetInventory(){
        inventory  = new ArrayList<>();
    }

}
