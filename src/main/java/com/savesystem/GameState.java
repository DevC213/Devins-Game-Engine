package com.savesystem;

import java.util.HashMap;
import java.util.Map;

public class GameState {
    public PlayerState playerState;
    public Map<Integer, MapState> mapStates;
    public int level;
    public int currentMapID;
    public int deepestLevel;
    public int mainMapLevel;
    public GameState(){
        mapStates = new HashMap<>();
    }

}
