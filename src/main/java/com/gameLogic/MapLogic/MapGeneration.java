package com.gameLogic.MapLogic;

import java.io.InputStream;
import java.util.*;

class MapGeneration {

    private final static Vector<Vector<String>> key = new Vector<>();
    private int columns;
    private int rows;
    private String[] MapData;
    private final Map<Integer,String> multiFileMap = new HashMap<>();
    private final Vector<String> multiFileTiles = new Vector<>(List.of("e", "r", "c", "~"));
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
            key.add(new Vector<>(temp));
            temp.clear();
        }
    }
    public static Vector<Vector<String>> getKey() {
        return key;
    }
    private void defaultMap() {
        StringBuilder row = new StringBuilder();

        final int defaultColumns = 10;
        final int defaultRows = 10;
        String[] map;

        columns = defaultColumns;
        this.rows = defaultRows;

        map = new String[this.rows];
        for (int i = 0; i < map.length; i++) {
            map[i] = String.valueOf(row.append(".".repeat(columns)));
        }
        MapData = map.clone();
    }
    private void processMap(String map_file) {
        InputStream input;
        Scanner reader;
        Vector<String> mapTemp = new Vector<>();
        initialIzeMultiFileMap();
        initializePlayerTiles();
        try {
            input = Objects.requireNonNull(getClass().getResourceAsStream(map_file));
            reader = new Scanner(input);
            while (reader.hasNext()) {
                    String mapLine = reader.nextLine();
                    columns = mapLine.length();
                    mapTemp.add(mapLine);
                }
                rows = mapTemp.size();
        }catch(Exception e){
            defaultMap();
        }
        MapData = mapTemp.toArray(new String[0]);
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
        return String.valueOf(MapData[r].charAt(c));
    }
}
