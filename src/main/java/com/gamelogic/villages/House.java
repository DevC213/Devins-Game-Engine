package com.gamelogic.villages;

import com.gamelogic.map.Coordinates;
import com.gamelogic.map.mapLogic.MapProcesser;

import java.util.*;

public class House {
    int houseNumber;
    Coordinates mainCoordinates;
    Coordinates exitCoordinates;
    List<List<String>> mapData;
    String theme = "overworld";

    House(int houseNumber, Coordinates mainCoordinates, Coordinates exitCoordinates, String fileMap) {
        this.houseNumber = houseNumber;
        this.mainCoordinates = mainCoordinates;
        this.exitCoordinates = exitCoordinates;
        mapData = new ArrayList<>();
        processHouseMap(fileMap);
    }

    private void processHouseMap(String filePath) {
        List<List<String>> mapTemp;
        MapProcesser mapProcesser = new MapProcesser();
        try {
           mapTemp = mapProcesser.processMap(filePath);
        }catch(Exception e){
            return;
        }
        mapData = mapTemp;
    }

    public Coordinates getCoordinates() {
        return mainCoordinates;
    }
    public Coordinates getExitCoordinates() {
        return exitCoordinates;
    }

    public String getMapValue(Coordinates mapCoordinates) {
        return mapData.get(mapCoordinates.x()).get(mapCoordinates.y());
    }
    public String getTheme() {
        return theme;
    }
}
