package com.Movement;

import com.adventure_logic.Adventure;

public class East extends Direction {

    East(){

    }
    @Override
    public void move(Adventure adventure) {
        adventure.movePlayer(1,'c');
    }
}
