package com.Movement;

import com.adventure_logic.Adventure;

public class South extends direction {

    South(){

    }
    @Override
    public void move(Adventure adventure) {
        adventure.movePlayer(1,'r');
    }
}
