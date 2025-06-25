package com.gamelogic.villages;

import com.gamelogic.map.Coordinates;
import com.gamelogic.rawdataclasses.RHouse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;

public class Village{
    String name;
    Coordinates topCoordinates;
    Coordinates bottomCoordinates;
    Map<Integer, House> houseMap = new HashMap<>();

    public Village(String name, Coordinates topCoordinates, Coordinates bottomCoordinates, String filePath){
        this.name = name;
        this.topCoordinates = topCoordinates;
        this.bottomCoordinates = bottomCoordinates;
        processMap(filePath);
    }
    private void processMap(String filePath){
        Gson gson = new Gson();
        InputStream input = Objects.requireNonNull(getClass().getResourceAsStream(filePath));
        InputStreamReader reader = new InputStreamReader(input);
        Type listType = new TypeToken<List<RHouse>>() {}.getType();
        List<RHouse> tempHouseList = gson.fromJson(reader, listType);
        for(RHouse rHouse : tempHouseList) {
            Coordinates map = new Coordinates(rHouse.mapCoords()[0], rHouse.mapCoords()[1]);
            Coordinates exit = new Coordinates(rHouse.exit()[1], rHouse.exit()[0]);
            houseMap.put(rHouse.houseNumber(),new House(rHouse.houseNumber(), map, exit, rHouse.map()));
        }
    }
    public Coordinates getTopCoordinates(){
        return topCoordinates;
    }
    public Coordinates getBottomCoordinates(){
        return bottomCoordinates;
    }
    public String getName(){
        return name;
    }
    public int atHouse(Coordinates coordinates){
        for(House house: houseMap.values()){
            if(coordinates.y() == house.mainCoordinates.x() && coordinates.x() == house.mainCoordinates.y()){
                return house.houseNumber;
            }
        }
        return -1;
    }
    public House getHouseMap(int number){
        return houseMap.get(number);
    }

}
