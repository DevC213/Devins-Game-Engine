package com.gamelogic.commands;

enum Movement {
    LEFT(-1,0), RIGHT(1,0), UP(0,-1), DOWN(0,1), DEFAULT(0,0);
    final int dx, dy;
    Movement(int x, int y) {
        this.dx = x;
        this.dy = y;
    }

    public static Movement getMovement(String string) {
        try {
            return Movement.valueOf(string.toUpperCase());
        } catch (IllegalArgumentException e) {
            return DEFAULT;
        }
    }
}
