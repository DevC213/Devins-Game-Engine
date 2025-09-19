package com.gamelogic.villages;

import com.gamelogic.core.NonPlayableCharacterRegistry;
import com.gamelogic.map.Coordinates;

import com.gamelogic.rawdataclasses.RHouse;
import com.gamelogic.rawdataclasses.RVillager;
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
    Map<Coordinates, House> houseMap = new HashMap<>();
    Map<Coordinates, Integer> NPCs = new HashMap<>();


    public Village(String name, Coordinates topCoordinates, Coordinates bottomCoordinates, String filePath, String npcData){
        this.name = name;
        this.topCoordinates = topCoordinates;
        this.bottomCoordinates = bottomCoordinates;
        processMap(filePath);
        //processNPCs(npcData);
    }
    private void processMap(String filePath){
        Gson gson = new Gson();
        InputStream input = Objects.requireNonNull(getClass().getResourceAsStream(filePath));
        InputStreamReader reader = new InputStreamReader(input);
        Type listType = new TypeToken<List<RHouse>>() {}.getType();
        List<RHouse> tempHouseList = gson.fromJson(reader, listType);
        for(RHouse rHouse : tempHouseList) {
            Coordinates map = new Coordinates(rHouse.mapCoords()[0], rHouse.mapCoords()[1]);
            Coordinates exit = new Coordinates(rHouse.exit()[0], rHouse.exit()[1]);
            House house = new House(map, exit, rHouse.map(), rHouse.mapID());
            houseMap.put(map, house);
        }
    }
    private void processNPCs(String filePath){
        Gson gson = new Gson();
        InputStream input = Objects.requireNonNull(getClass().getResourceAsStream(filePath));
        InputStreamReader reader = new InputStreamReader(input);
        Type listType = new TypeToken<List<RVillager>>() {}.getType();
        List<RVillager> tempNPCList = gson.fromJson(reader, listType);
        for(RVillager rVillager : tempNPCList) {
            Coordinates location = new Coordinates(rVillager.location()[1], rVillager.location()[0]);
            NPC npc = new NPC(location,rVillager.name(), rVillager.quests());
            for(String dialogue: rVillager.messages()){
                npc.addDialogue(dialogue);
            }
            NPCs.put(location, NonPlayableCharacterRegistry.addNPC(npc));
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
    public House getHouseMap(Coordinates coordinates){
        Coordinates flippedCoords = new Coordinates(coordinates.y(), coordinates.x());
        return houseMap.get(flippedCoords);
    }
}
