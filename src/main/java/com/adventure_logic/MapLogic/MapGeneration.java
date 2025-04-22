package com.adventure_logic.MapLogic;

import java.io.InputStream;
import java.util.*;

class MapGeneration {

    private final static Vector<Vector<String>> key = new Vector<>();
    private int columns;
    private int rows;
    private String[] MapData;


    public MapGeneration() {
        defaultMap();
    }
    public MapGeneration(final String mapName) {
        processMap(mapName);
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
    public String getImage(final String terrain) {
        for (Vector<String> j: MapGeneration.key) {
            if (j.contains(terrain)) {
                return j.get(2);
            }
        }
        return null;
    }
    public int[] getColumnsAndRows(){return new int[]{rows,columns}; }
    public String getMapValue(final int c, final int r) {
        if (c < 0 || r < 0 || c >= columns|| r >= rows) {
            return "-";
        }
        return String.valueOf(MapData[r].charAt(c));
    }
}
