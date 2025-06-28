package com.gamelogic.core;

import com.gamelogic.map.mapLogic.MapController;
import com.savesystem.MapState;

import java.util.HashMap;
import java.util.Map;

public class MapRegistry {
    private static final Map<Integer, MapController> mapRegistry =  new HashMap<>();

    public static void addMap(MapController mapController, int id){
        mapRegistry.put(id, mapController);
    }
    public static MapController getMapController(int id){
        return mapRegistry.get(id);
    }
    public static Map<Integer, MapState> getMapStates(){
        Map<Integer, MapState> mapStates = new HashMap<>();
        for(int i: mapRegistry.keySet()){
            mapStates.put(i,mapRegistry.get(i).getMapState());
        }
        return mapStates;
    }
    public static int getMapId(MapController mapController){
        for(MapController map: mapRegistry.values()){
            if(map == mapController){
                return map.getID();
            }
        }
        return -1;
    }

    public static void loadData(Map<Integer, MapState> mapStates) {
        for(int id: mapRegistry.keySet()){
            MapController mapController = mapRegistry.get(id);
            MapState mapState = mapStates.get(id);
            mapController.loadData(mapState);
        }
    }
}
