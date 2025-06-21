package com.gameLogic.MapLogic;

import com.gameLogic.Coordinates;
import com.gameLogic.TileKey;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;

public class MapGeneration {

    private final static Map<String, TileKey> tileKey= new HashMap<>();
    private Coordinates maxCoords;
    private List<List<String>> MapData;
    private final Map<String,String> multiFileMap = new HashMap<>();
    private final Map<Integer,String> playerTiles = new HashMap<>();


    public MapGeneration() {
        defaultMap();
    }
    public MapGeneration(final String mapName) {
        processMap(mapName);
    }
    private void initialIzeMultiFileMap(){
        multiFileMap.put("Overworld","/MapPics/overworldTiles/");
        multiFileMap.put("Underground","/MapPics/undergroundTiles/");
        multiFileMap.put("Caverns","/MapPics/cavernTiles/");
        multiFileMap.put("TheDarkness","/MapPics/darknessTiles/");
        multiFileMap.put("TheVoid","/MapPics/voidTiles/");
    }
    private void initializePlayerTiles(){
        playerTiles.put(0,"/MapPics/playerImages/down.png");
        playerTiles.put(1,"/MapPics/playerImages/left.png");
        playerTiles.put(2,"/MapPics/playerImages/right.png");
        playerTiles.put(3,"/MapPics/playerImages/up.png");
    }
    public static void processKey(InputStream keyFile){
        Gson gson = new Gson();
        InputStreamReader reader = new InputStreamReader(keyFile);
        Type listType = new TypeToken<List<TileKey>>() {}.getType();
        List<TileKey> keyList = gson.fromJson(reader, listType);
        for(TileKey tileKey : keyList){
            MapGeneration.tileKey.put(tileKey.tileId(),tileKey);
        }
    }
    public static Map<String, TileKey> getTileKey() {
        return tileKey;
    }
    private void defaultMap() {
        List<List<String>> temp = new ArrayList<>();

        final int defaultColumns = 10;
        final int defaultRows = 10;

        maxCoords = new Coordinates(defaultColumns, defaultRows);

        for (int i = 0; i < defaultRows; i++) {
            List<String> tempRow = new ArrayList<>();
            for (int j = 0; j < defaultColumns; j++) {
                tempRow.add(".");
            }
            temp.add(tempRow);
        }
        MapData = temp;
    }
    private void processMap(String map_file) {
        InputStream input;
        Scanner reader;
        List<List<String>> mapTemp = new ArrayList<>();
        initialIzeMultiFileMap();
        initializePlayerTiles();
        try {
            input = Objects.requireNonNull(getClass().getResourceAsStream(map_file));
            reader = new Scanner(input);
            while (reader.hasNext()) {
                    List<String> tempRow;
                    String mapLine = reader.nextLine();
                    tempRow = lineParser(mapLine);
                    mapTemp.add(tempRow);
                }

                checkLengths(mapTemp);
                maxCoords = new Coordinates(mapTemp.getFirst().size(), mapTemp.size());
        }catch(Exception e){
            defaultMap();
            return;
        }

        MapData = mapTemp;
    }
    private List<String> lineParser(String line) {
        Vector<String> temp = new Vector<>();
        int i = 0;
        while(i < line.length()){
            if(line.startsWith("//",i)){
                int end = line.indexOf("//",i+2);
                if (end == -1 || end == i + 2) {
                    temp.add("/");
                    i += 1;
                } else {
                    String key = line.substring(i + 2, end);
                    temp.add(key);
                    i = end + 2;
                }
            } else {
                temp.add(String.valueOf(line.charAt(i)));
                i++;
            }
        }
        return temp;
    }
    private void checkLengths(List<List<String>> vec){
        int expectedLength = vec.getFirst().size();
        for(int i = 1; i < vec.size(); i++){
            if(vec.get(i).size() < expectedLength){
                while(vec.get(i).size() < expectedLength){
                    vec.get(i).add(vec.get(i).getLast());
                }
            }else{
                while(vec.get(i).size() > expectedLength){
                    vec.get(i).removeLast();
                }
            }

        }
    }
    public String getImage(final String terrain, String levelName) {
        if (tileKey.get(terrain).multiFile()) {
            return multiFileMap.get(levelName) + MapGeneration.tileKey.get(terrain).fileLocation();
        }
        return MapGeneration.tileKey.get(terrain).fileLocation();
    }
    public String getPlayerImage(int direction){
        return playerTiles.get(direction);
    }
    public Coordinates getColumnsAndRows(){return maxCoords;}
    public String getMapValue(Coordinates coordinates) {
        if (coordinates.x() < 0 ||
                coordinates.y() < 0 ||
                coordinates.x() >= maxCoords.x() ||
                coordinates.y() >= maxCoords.y())
        {
            return "-";
        }
        return MapData.get(coordinates.y()).get(coordinates.x());
    }
}
