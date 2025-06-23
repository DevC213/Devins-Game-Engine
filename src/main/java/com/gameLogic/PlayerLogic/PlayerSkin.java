package com.gameLogic.PlayerLogic;

import java.util.HashMap;
import java.util.Map;

public class PlayerSkin {
    String[] directions;

    PlayerSkin(){

    }
    public String getDirection(int direction) {
        return directions[direction];
    }
}
