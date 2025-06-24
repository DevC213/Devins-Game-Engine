package com.gamelogic.map.mapLogic;

import com.gamelogic.map.Coordinates;
import com.gamelogic.messaging.Messenger;
import com.gamelogic.rawdataclasses.RVillage;
import com.gamelogic.villages.House;
import com.gamelogic.villages.Village;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;

public class MapVillageController {
    Map<String, Village> villageMap = new HashMap<>();
    boolean inVillage;

    MapVillageController(String filePath) {
        processVillages(filePath);
        inVillage = false;
    }
    Messenger checkVillage(Coordinates coordinates) {
        Messenger messenger = new Messenger();
        Village village;
        Coordinates top;
        Coordinates bottom;
        int column = coordinates.x();
        int row = coordinates.y();
        if(!inVillage) {
            for (String coords : villageMap.keySet()) {
                village = villageMap.get(coords);
                top = village.getTopCoordinates();
                bottom = village.getBottomCoordinates();
                if (row <= bottom.x() && row >= top.x() && column <= bottom.y() && column >= top.y()) {
                    inVillage = true;
                    messenger.setMessage("Welcome to: " + village.getName());
                    messenger.addPayloadS(village.getName());
                }
            }
        } else{
            for (String coords : villageMap.keySet()) {
                village = villageMap.get(coords);
                top = village.getTopCoordinates();
                bottom = village.getBottomCoordinates();
                if (row < top.x() || row > bottom.x() || column < top.y() || column > bottom.y()) {
                    inVillage = false;
                    messenger.setMessage("Now Leaving: " + village.getName());
                }
            }
        }
        return messenger;
    }
    private void processVillages(String filePath){
        Gson gson = new Gson();
        InputStream input = Objects.requireNonNull(getClass().getResourceAsStream(filePath));
        InputStreamReader reader = new InputStreamReader(input);
        Type listType = new TypeToken<List<RVillage>>() {}.getType();
        List<RVillage> tempVillageList = gson.fromJson(reader, listType);
        for(RVillage rVillage : tempVillageList) {
            Coordinates top = new Coordinates(rVillage.topCoords()[0], rVillage.topCoords()[1]);
            Coordinates bottom = new Coordinates(rVillage.bottomCoords()[0], rVillage.bottomCoords()[1]);
            villageMap.put(rVillage.name(), new Village(rVillage.name(), top, bottom, rVillage.houses()));
        }
    }

    public int checkHouse(Coordinates coordinates, String villageName){
        return villageMap.get(villageName).atHouse(coordinates);
    }
    public House getHouseMap(int number, String villageName){
        return villageMap.get(villageName).getHouseMap(number);
    }
}
