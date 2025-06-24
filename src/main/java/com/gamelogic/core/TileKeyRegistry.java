package com.gamelogic.core;

import com.gamelogic.map.TileKey;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TileKeyRegistry {
    private static final Map<String, TileKey> tileKeyMap = new HashMap<>();
    private static boolean initialized = false;

    private TileKeyRegistry(){
    }
    public static void initialize(String tileKeyPath){
        if(initialized){
            return;
        }
        Gson gson = new Gson();
        InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(TileKeyRegistry.class.getResourceAsStream(tileKeyPath)));
        Type listType = new TypeToken<List<TileKey>>() {}.getType();
        List<TileKey> keyList = gson.fromJson(reader, listType);
        for(TileKey tileKey : keyList){
            tileKeyMap.put(tileKey.tileId(),tileKey);
        }
        initialized = true;
    }


    public static TileKey getTileKey(String tile){
        return tileKeyMap.get(tile);
    }
    public static Map<String, TileKey> getTileKeyList(){
        return tileKeyMap;
    }

}
