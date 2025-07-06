package com.gamelogic.gameflow;

enum Movement{LEFT, RIGHT, UP, DOWN, DEFAULT;
    public static Movement getmovement(String string){
        try {
            return Movement.valueOf(string.toUpperCase());
        } catch (IllegalArgumentException e) {
            return DEFAULT;
        }
    }
}