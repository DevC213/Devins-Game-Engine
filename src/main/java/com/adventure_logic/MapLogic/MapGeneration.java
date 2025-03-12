package com.adventure_logic.MapLogic;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

class MapGeneration {

    private final static Vector<Vector<String>> key = new Vector<>();
    private int columns;
    private int rows;
    private String[] MapData;

    /**
     * Default Map constructor(10x10), calls default map method.
     */
    public MapGeneration() {
        defaultMap();
    }

    /**
     * Generates map from file, in case of error calls default map method.
     * @param mapName map file
     */
    public MapGeneration(final String mapName) {
        processMap(mapName);
    }
    public static void processKey(String keyFile) throws FileNotFoundException {
        File myfile = new File(keyFile);
        Scanner reader = new Scanner(myfile);
        Vector<String> temp = new Vector<>();
        while (reader.hasNext()) {
            temp.addAll(Arrays.asList(reader.nextLine().split(";")));
            key.add(new Vector<>(temp));
            temp.clear();
        }
    }

    public static Vector<Vector<String>> getKey() {
        return key;
    }

    /**
     * Generates default map, in-case of error, or no file.
     * Default map: 10x10 of terrain type "."
     */
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
        File myfile;
        Scanner reader;
        Vector<String> mapTemp = new Vector<>();
        try {
            myfile = new File(map_file);
            reader = new Scanner(myfile);
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
    public String[] getMap() {
        return MapData;
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
