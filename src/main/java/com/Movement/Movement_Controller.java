package com.Movement;

import com.adventure_logic.Adventure;

import java.io.Console;

public class Movement_Controller {


    direction East;
    direction West;
    direction North;
    direction South;
    direction movement;

    public Movement_Controller(){
        East = new East();
        West = new West();
        North = new North();
        South = new South();
    }
    public void direction(String direction){
        switch (direction) {
            case "west" -> movement = West;
            case "east" -> movement = East;
            case "north" -> movement = North;
            case "south" -> movement = South;
            default -> {
            }
        }
        movement.move(Adventure.getAdventure());
    }

}
