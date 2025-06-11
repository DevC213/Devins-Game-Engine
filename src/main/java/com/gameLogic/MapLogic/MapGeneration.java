package com.gameLogic.MapLogic;

import java.io.InputStream;
import java.util.*;

class MapGeneration {

    private final static Vector<Vector<String>> key = new Vector<>();
    private int columns;
    private int rows;
    private Vector<Vector<String>> MapData;
    private final Map<Integer,String> multiFileMap = new HashMap<>();
    private final Vector<String> multiFileTiles = new Vector<>(List.of(
            "e", "r", "c",
            "~", "s","g"
    ));
    private final Map<Integer,String> playerTiles = new HashMap<>();

    public MapGeneration() {
        defaultMap();
    }
    public MapGeneration(final String mapName) {
        processMap(mapName);
    }

    private void initialIzeMultiFileMap(){
        multiFileMap.put(0,"/MapPics/overworldTiles/");
        multiFileMap.put(1,"/MapPics/undergroundTiles/");
        multiFileMap.put(2,"/MapPics/cavernTiles/");
        multiFileMap.put(3,"/MapPics/darknessTiles/");
        multiFileMap.put(4,"/MapPics/voidTiles/");
    }
    private void initializePlayerTiles(){
        playerTiles.put(0,"/MapPics/playerImages/down.png");
        playerTiles.put(1,"/MapPics/playerImages/left.png");
        playerTiles.put(2,"/MapPics/playerImages/right.png");
        playerTiles.put(3,"/MapPics/playerImages/up.png");
    }
    public static void processKey(InputStream keyFile){
        Scanner reader = new Scanner(keyFile);
        Vector<String> temp = new Vector<>();
        while (reader.hasNext()) {
            String line = reader.nextLine();
            temp.addAll(Arrays.asList(line.split(";")));
            String tile = temp.getFirst();
            if(tile.startsWith("{") && tile.endsWith("}")){
                if(tile.contains("semi")){
                    temp.set(0,";");
                }
            }
            key.add(new Vector<>(temp));
            temp.clear();
        }
    }
    public static Vector<Vector<String>> getKey() {
        return key;
    }
    private void defaultMap() {
        Vector<Vector<String>> temp = new Vector<>();


        final int defaultColumns = 10;
        final int defaultRows = 10;

        columns = defaultColumns;
        rows = defaultRows;

        for (int i = 0; i < defaultRows; i++) {
            Vector<String> tempRow = new Vector<>();
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
        Vector<Vector<String>> mapTemp = new Vector<>();
        initialIzeMultiFileMap();
        initializePlayerTiles();
        try {
            input = Objects.requireNonNull(getClass().getResourceAsStream(map_file));
            reader = new Scanner(input);
            while (reader.hasNext()) {
                    Vector<String> tempRow;
                    String mapLine = reader.nextLine();
                    tempRow = lineParser(mapLine);
                    mapTemp.add(tempRow);
                }

                checkLengths(mapTemp);
                rows = mapTemp.size();
                columns = mapTemp.getFirst().size();
        }catch(Exception e){
            defaultMap();
            return;
        }

        MapData = mapTemp;
    }
    private Vector<String> lineParser(String line) {
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
    private void checkLengths(Vector<Vector<String>> vec){
        int expectedLength = vec.getFirst().size();
        for(int i = 1; i < vec.size(); i++){
            if(vec.get(i).size() < expectedLength){
                while(vec.get(i).size() < expectedLength){
                    vec.get(i).add(vec.get(i).getLast());
                }
            }else{
                while(vec.get(i).size() > expectedLength){
                    vec.get(i).removeLast();
                };
            }

        }
    }
    public String getImage(final String terrain, int level) {
        for (Vector<String> j: MapGeneration.key) {
            if (j.contains(terrain)) {
                if(multiFileTiles.contains(terrain)) {
                    return multiFileMap.get(level) + j.get(2);
                }
                return j.get(2);
            }
        }
        return null;
    }
    public String getPlayerImage(int direction){
        return playerTiles.get(direction);
    }
    public int[] getColumnsAndRows(){return new int[]{rows,columns}; }
    public String getMapValue(final int c, final int r) {
        if (c < 0 || r < 0 || c >= columns|| r >= rows) {
            return "-";
        }
        return MapData.get(r).get(c);
    }
}
