package com.gamelogic.map.mapLogic;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MapProcesser {

    public MapProcesser(){}

    public List<List<String>> processMap(String filePath){
        InputStream input;
        Scanner reader;
        List<List<String>> mapTemp = new ArrayList<>();
        input = Objects.requireNonNull(getClass().getResourceAsStream(filePath));
        reader = new Scanner(input, StandardCharsets.UTF_8);
        while (reader.hasNext()) {
            List<String> tempRow;
            String mapLine = reader.nextLine();
            tempRow = lineParser(mapLine);
            mapTemp.add(tempRow);
        }

        checkLengths(mapTemp);
        return mapTemp;
    }
    private List<String> lineParser(String line) {
        List<String> temp = new ArrayList<>();
        int i = 0;
        while (i < line.length()) {
            int codePoint = line.codePointAt(i);
            temp.add(new String(Character.toChars(codePoint)));
            i += Character.charCount(codePoint);
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
}
