package com.gameLogic.MapLogic;

public record LevelData(MapGeneration map, MapItemController item, MapMonsterController monster, String theme) {
    public void resetGame() {
        item.resetMap();
        monster.resetMonsters();
    }
}
