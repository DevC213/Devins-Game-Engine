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

public class HouseData extends MapData{
    private final List<LevelData> levelDataList;
    private final MapType mapType;

    public HouseData(MapType mapType) {
        levelDataList = new ArrayList<>();
        this.mapType = mapType;
    }
    public void processMap(int level, Map<String, String> levelMap, String theme, String voice, String sound) {}
    public void processMap(String filePath){
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
