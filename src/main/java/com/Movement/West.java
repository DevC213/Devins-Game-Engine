package com.Movement;

import com.adventure_logic.Adventure;

public class West extends direction {
    West(){
    }
    @Override
    public void move(Adventure adventure) {
        adventure.movePlayer(-1, 'c');
    }
}
