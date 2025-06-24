package com.gamelogic.map.mapLogic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapData {
    private final List<LevelData> levelDataList;

    MapData(){
        levelDataList = new ArrayList<>();
    }
    public void processMap(int level, Map<String, String> levelMap, String theme, String voice, String sound) {
        MapGeneration mapGeneration = null;
        MapItemController mapItemController = null;
        MapMonsterController mapMonsterController = null;

        for(Map.Entry<String, String> entry : levelMap.entrySet()) {
            String file = entry.getKey();
            String path = entry.getValue();

            switch (file) {
                case "Map" -> mapGeneration = new MapGeneration(path);
                case "Items" -> {
                    if (mapGeneration == null) {
                        throw new IllegalStateException("MapGeneration has not been set for: " + level);
                    }
                    mapItemController = new MapItemController(path, mapGeneration.getColumnsAndRows());
                }
                case "Monsters" -> mapMonsterController = new MapMonsterController(path);
                case "SpawnTable" -> {
                    if (mapMonsterController != null) {
                        mapMonsterController.processSpawnChances(path);
                    }
                }
                default -> throw new RuntimeException("Unknown file found: " + path);
            }

        }
        if(mapItemController == null ||  mapMonsterController == null) {
            throw new IllegalStateException("Incomplete MapData for level: " + level);
        }
        levelDataList.add(new LevelData(mapGeneration, mapItemController, mapMonsterController,theme,voice,sound));
    }
    public void defaultLevel(){
        levelDataList.add(new LevelData(new MapGeneration(),null,null,"Default",null,null));
    }

    public LevelData getLevel(int level){
        return levelDataList.get(level);
    }
    public int getTotalLevels(){
        return levelDataList.size();
    }
}
