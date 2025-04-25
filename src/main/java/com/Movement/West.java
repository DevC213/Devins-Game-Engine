package com.Movement;

import com.adventure_logic.Adventure;

public class West extends Direction {
    West(){
    }
    @Override
    public void move(Adventure adventure) {
        adventure. movePlayer(-1, 'c');
    }
}
