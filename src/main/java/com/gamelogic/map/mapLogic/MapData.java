package com.gamelogic.map.mapLogic;

import com.gamelogic.rawdataclasses.RHouseMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MapData {
    private final List<LevelData> levelDataList;
    private final MapType mapType;

    MapData(MapType mapType) {
        levelDataList = new ArrayList<>();
        this.mapType = mapType;
    }
    public void processHouse(int level, Map<String, String> levelMap, String theme, String voice, String sound) {
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
    public void processHouse(String filePath){
        MapGeneration mapGeneration;
        Gson gson = new Gson();
        InputStream input = Objects.requireNonNull(getClass().getResourceAsStream(filePath));
        InputStreamReader reader = new InputStreamReader(input);
        Type listType = new TypeToken<List<RHouseMap>>() {}.getType();
        List<RHouseMap> tempHouseFloorList = gson.fromJson(reader, listType);
        for(RHouseMap rHouseMap : tempHouseFloorList) {
            mapGeneration = new MapGeneration(rHouseMap.map());
            levelDataList.add(new LevelData(mapGeneration, null, null,null,rHouseMap.theme(),null,null, mapType));
        }
    }
    public void processDungeon(String string){}
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
