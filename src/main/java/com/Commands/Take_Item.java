package com.Commands;

import com.adventure_logic.Adventure;

public class Take_Item implements Command{

    final int PICK_UP = 2;
    Take_Item(){}

    @Override
    public int process(Adventure adventure) {
        if (adventure.getItems() == null) {
            adventure.sendMessage("No items to pick up.");
            return 0;
        } else {
            adventure.sendMessage("Enter Item or All");
            return PICK_UP;
        }
    }
}
