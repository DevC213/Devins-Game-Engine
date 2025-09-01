package com.gamelogic.map.mapLogic;

import com.gamelogic.core.GameConfig;
import com.gamelogic.map.Coordinates;

import java.util.*;

public class MapGeneration {

    private Coordinates maxCoords;
    private List<List<String>> mapData;
    private static final int DEFAULT = GameConfig.DEFAULT_MAP_SIZE;

    public MapGeneration() {
        defaultMap();
    }
    public MapGeneration(final String mapName) {
        processMap(mapName);
    }
    private void defaultMap() {
        List<List<String>> temp = new ArrayList<>();

        maxCoords = new Coordinates(DEFAULT, DEFAULT);

        for (int i = 0; i < DEFAULT; i++) {
            List<String> tempRow = new ArrayList<>();
            for (int j = 0; j < DEFAULT; j++) {
                tempRow.add(GameConfig.DEFAULT_TILE);
            }
            temp.add(tempRow);
        }
        mapData = temp;
    }
    private void processMap(String filePath) {
        MapProcesser mapProcesser = new MapProcesser();
        List<List<String>> mapTemp;
        try {
            mapTemp = mapProcesser.processMap(filePath);
            maxCoords = new Coordinates(mapTemp.getFirst().size(), mapTemp.size());
        }catch(Exception e){
            defaultMap();
            return;
        }

        mapData = mapTemp;
    }
    public Coordinates getColumnsAndRows(){return maxCoords;}
    public String getMapValue(Coordinates coordinates) {
        int x = coordinates.x();
        int y = coordinates.y();
        if(y < 0 || y >= maxCoords.y()){
            return GameConfig.FOG_TILE;
        }
        List<String> tempRow = mapData.get(y);
        if (x < 0 || x >= maxCoords.x()){
            return GameConfig.FOG_TILE;
        }
        return tempRow.get(x);
    }

    public List<List<String>> getMapData() {
        return mapData;
    }

    public void loadMap(List<List<String>> mapData) {
        this.mapData = mapData;
    }
}
