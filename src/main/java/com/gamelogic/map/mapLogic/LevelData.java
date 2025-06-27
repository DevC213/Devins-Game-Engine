package com.gamelogic.map.mapLogic;

public record LevelData(MapGeneration map, MapItemController item, MapMonsterController monster, MapVillageController villages ,String theme, String voice, String sound, MapType type) {
    public void resetGame() {
        if( type() != MapType.HOUSE) {
            item.resetMap();
            monster.resetMonsters();
        }
    }
}
