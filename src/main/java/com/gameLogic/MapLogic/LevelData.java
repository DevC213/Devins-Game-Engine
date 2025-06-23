package com.gameLogic.MapLogic;

public record LevelData(MapGeneration map, MapItemController item, MapMonsterController monster, String theme, String voice, String sound) {
    public void resetGame() {
        item.resetMap();
        monster.resetMonsters();
    }
}
