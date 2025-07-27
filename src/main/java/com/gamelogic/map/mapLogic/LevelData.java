package com.gamelogic.map.mapLogic;

import java.util.Optional;

public record LevelData(MapGeneration map, MapItemController item, MapMonsterController monster, MapVillageController villages , String theme, String voice, String sound) {
    public void resetGame() {
        if(item != null ) {
            item.resetMap();
        }
        if(monster != null) {
            monster.resetMonsters();
        }
    }
}
