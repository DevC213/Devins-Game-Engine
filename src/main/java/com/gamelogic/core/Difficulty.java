package com.gamelogic.core;

public enum Difficulty {
    NORMAL,
    HARDCORE;
    public boolean endGame(){
        return this == HARDCORE;
    }
}
