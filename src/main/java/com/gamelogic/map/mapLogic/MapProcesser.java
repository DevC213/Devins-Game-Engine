package com.gamelogic.map.mapLogic;

import java.io.InputStream;
import java.util.*;

public class MapProcesser {

    public MapProcesser(){}

    public List<List<String>> processMap(String filePath){
        InputStream input;
        Scanner reader;
        List<List<String>> mapTemp = new ArrayList<>();
        input = Objects.requireNonNull(getClass().getResourceAsStream(filePath));
        reader = new Scanner(input);
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
}
