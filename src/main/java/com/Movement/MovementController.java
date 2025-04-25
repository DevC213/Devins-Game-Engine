package com.Movement;

import com.adventure_logic.Adventure;

public class MovementController {


    Direction East;
    Direction West;
    Direction North;
    Direction South;
    Direction movement;

    public MovementController(){
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
