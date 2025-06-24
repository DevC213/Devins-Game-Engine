package com.gamelogic.map.mapLogic;

import com.gamelogic.map.Coordinates;

import java.util.*;

public class MapGeneration {

    private Coordinates maxCoords;
    private List<List<String>> mapData;
    private static final int DEFAULT_COLUMNS = 10;
    private static final int DEFAULT_ROWS = 10;

    public MapGeneration() {
        defaultMap();
    }
    public MapGeneration(final String mapName) {
        processMap(mapName);
    }
    private void defaultMap() {
        List<List<String>> temp = new ArrayList<>();

        maxCoords = new Coordinates(DEFAULT_COLUMNS, DEFAULT_ROWS);

        for (int i = 0; i < DEFAULT_COLUMNS; i++) {
            List<String> tempRow = new ArrayList<>();
            for (int j = 0; j < DEFAULT_ROWS; j++) {
                tempRow.add(".");
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
            return "-";
        }
        List<String> tempRow = mapData.get(y);
        if (x < 0 || x >= maxCoords.x()){
            return "-";
        }
        return tempRow.get(x);
    }
}
