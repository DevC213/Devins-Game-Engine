package com.gamelogic.map.mapLogic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class OverworldMapData extends MapData {
    private final List<LevelData> levelDataList;

    public OverworldMapData() {
        levelDataList = new ArrayList<>();
    }
    public void processMap(int level, Map<String, String> levelMap, String theme, String voice, String sound) {
        MapGeneration mapGeneration = null;
        MapItemController mapItemController = null;
        MapMonsterController mapMonsterController = null;
        MapVillageController mapVillageController = null;
        MapNPCController mapNPCController = null;
        for(Map.Entry<String, String> entry : levelMap.entrySet()) {
            String file = entry.getKey();
            String path = entry.getValue();

            switch (file) {
                case "Map" -> mapGeneration = new MapGeneration(path);
                case "Items" -> {
                    if (mapGeneration == null) {
                        throw new IllegalStateException("MapGeneration has not been set for: " + level);
                    }
                    mapItemController = new MapItemController(path);
                }
                case "Monsters" -> mapMonsterController = new MapMonsterController(path);
                case "SpawnTable" -> {
                    if (mapMonsterController != null) {
                        mapMonsterController.processSpawnChances(path);
                    }

                }
                case "Villages" -> mapVillageController = new MapVillageController(path);
                case "Dungeons" -> {
                }
                case "NPCs" -> {mapNPCController = new MapNPCController(path);}
                default -> throw new RuntimeException("Unknown file found: " + path);
            }

        }
        if(mapItemController == null ||  mapMonsterController == null) {
            throw new IllegalStateException("Incomplete MapData for level: " + level);
        }
        levelDataList.add(new LevelData(mapGeneration, mapItemController, mapMonsterController,mapVillageController,mapNPCController,theme,voice,sound));
    }

    @Override
    public void processMap(String filePath) {}

    public void defaultLevel(){
        levelDataList.add(new LevelData(new MapGeneration(),null,null,null,null,"Default",null,null));
    }

    public LevelData getLevel(int level){
        return levelDataList.get(level);
    }
    public int getTotalLevels(){
        return levelDataList.size();
    }

    @Override
    public List<List<List<String>>> getMap() {
        List<List<List<String>>> map = new ArrayList<>();
        for(LevelData levelData : levelDataList){
            map.add(levelData.map().getMapData());
        }
        return map;
    }

    @Override
    public void loadMap(List<List<List<String>>> map) {
        for(int i = 0; i < map.size(); i++){
            levelDataList.get(i).map().loadMap(map.get(i));
        }
    }
}
