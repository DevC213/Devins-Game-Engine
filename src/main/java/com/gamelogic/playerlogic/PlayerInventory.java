package com.gamelogic.playerlogic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class PlayerInventory {

    private List<String> inventory;

    PlayerInventory(){ inventory  = new ArrayList<>(); }
    public List<String> viewInventory() {
        if (inventory.isEmpty()) {
            return Collections.emptyList();
        }
        return inventory;
    }

    public void addToInventory(final List<String> items) {
        if (items!= null && !items.isEmpty()) {
            inventory.addAll(items);
        }
    }

    public void resetInventory(){
        inventory  = new ArrayList<>();
    }

}
