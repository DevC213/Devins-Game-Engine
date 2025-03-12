package com.Commands;

import com.adventure_logic.Adventure;

public class Drop_Item implements Command{

    final int DROP_I = 1;

    Drop_Item(){

    }
    @Override
    public int process(Adventure adventure) {
        if (adventure.getInventoryTotal() == 0) {
            adventure.sendMessage("Inventory is empty");
            return 0;
        } else {
            adventure.sendMessage("Which Item do you want to drop?\n"
                    + "Enter any other key to cancel.");
            return DROP_I;
        }
    }


}
