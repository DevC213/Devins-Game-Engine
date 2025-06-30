package com.gamelogic.map.mapLogic;

public enum MapType {
    OVERWORLD,
    DUNGEON,
    HOUSE;

    public boolean hasItems(){
        return this != HOUSE;
    }
    public boolean hasMonsters(){
        return this != HOUSE;
    }
    public boolean progressesGame(){
        return this == OVERWORLD;
    }
    public boolean hasFog(){
        return this != HOUSE;
    }
    public boolean hasVillages(){
        return this == OVERWORLD;
    }
}
