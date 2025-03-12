package com.Movement;

import com.adventure_logic.Adventure;

public class North extends direction {

    North(){

    }

    @Override
    public void move(Adventure adventure) {
        adventure.movePlayer(-1, 'r');
    }

}
