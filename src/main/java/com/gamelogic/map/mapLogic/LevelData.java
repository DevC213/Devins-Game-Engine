package com.gamelogic.map.mapLogic;

public record LevelData(MapGeneration map, MapItemController item, MapMonsterController monster, MapVillageController villages, MapNPCController NPCs, String theme, String voice, String sound) {
    public void resetGame() {
        if(item != null ) {
            item.resetMap();
        }
        if(monster != null) {
            monster.resetMonsters();
        }
        if(NPCs != null) {
            NPCs.resetNPCS();
        }
    }
}
