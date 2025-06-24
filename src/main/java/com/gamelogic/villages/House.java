package com.gamelogic.villages;

import com.gamelogic.map.Coordinates;
import com.gamelogic.map.mapLogic.MapProcesser;
import com.gamelogic.rawdataclasses.RHouse;
import com.gamelogic.rawdataclasses.RVillage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.monsters.Monster;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;

public class House {
    int houseNumber;
    Coordinates mainCoordinates;
    Coordinates exitCoordinates;
    List<List<String>> MapData;

    House(int houseNumber, Coordinates mainCoordinates, Coordinates exitCoordinates, String fileMap) {
        this.houseNumber = houseNumber;
        this.mainCoordinates = mainCoordinates;
        this.exitCoordinates = exitCoordinates;
        MapData = new ArrayList<>();
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
        MapData = mapTemp;
    }

}
