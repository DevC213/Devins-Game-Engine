package com.Commands;

import com.adventure_logic.Adventure;


import java.util.Locale;


public class Command_Controller {
    Adventure adventure;
    Command take_item;
    Command drop_item;
    Command current;
    public Command_Controller(Adventure adventure){

        this.adventure = adventure;
        take_item = new Take_Item();
        drop_item = new Drop_Item();
    }
    public int Command(String command){
        if (command.isEmpty()) {
             adventure.sendMessage("No command entered");
            return 0;
        }else{
            switch (command.toLowerCase(Locale.ROOT).charAt(0)) {
                case 't' -> {current = take_item;}
                case 'd' -> {
                    if(command.split(" ").length > 1){
                        adventure.dropItem(command.split( " ")[1]);
                    }else {
                        current = drop_item;
                    }
                }
                default -> {current = null;}
            }
        }
        if(current != null){
            return current.process(adventure);
        }
        return 0;
    }

}
