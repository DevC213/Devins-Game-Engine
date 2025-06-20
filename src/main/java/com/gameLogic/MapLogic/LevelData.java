package com.gameLogic.MapLogic;

public record LevelData(MapGeneration map, MapItemController item, MapMonsterController monster) {
    public void resetGame() {
        item.resetMap();
        monster.resetMonsters();
    }
}
