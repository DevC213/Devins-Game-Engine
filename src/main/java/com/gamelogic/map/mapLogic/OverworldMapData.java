package com.gamelogic.map.mapLogic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class OverworldMapData extends MapData {
    private final List<LevelData> levelDataList;
    private final MapType mapType;

    public OverworldMapData(MapType mapType) {
        levelDataList = new ArrayList<>();
        this.mapType = mapType;
    }
    public void processMap(int level, Map<String, String> levelMap, String theme, String voice, String sound) {
        MapGeneration mapGeneration = null;
        MapItemController mapItemController = null;
        MapMonsterController mapMonsterController = null;
        MapVillageController mapVillageController = null;
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
                case "Villages" -> mapVillageController = new MapVillageController(path);
                default -> throw new RuntimeException("Unknown file found: " + path);
            }

        }
        if(mapItemController == null ||  mapMonsterController == null) {
            throw new IllegalStateException("Incomplete MapData for level: " + level);
        }
        levelDataList.add(new LevelData(mapGeneration, mapItemController, mapMonsterController,mapVillageController,theme,voice,sound, mapType));
    }

    @Override
    public void processMap(String filePath) {}

    public void defaultLevel(){
        levelDataList.add(new LevelData(new MapGeneration(),null,null,null,"Default",null,null, mapType));
    }

    public LevelData getLevel(int level){
        return levelDataList.get(level);
    }
    public int getTotalLevels(){
        return levelDataList.size();
    }
}
