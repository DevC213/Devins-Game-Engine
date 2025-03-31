package com.adventure_logic.MapLogic;

import java.io.File;
import java.util.*;

class MapItemController {


    private final Map<String, Vector<String>> itemLocation = new HashMap<>();
    private final Map<String, Vector<String>> dItemLocation = new HashMap<>();
    public MapItemController(String map) {
        processItems(map);
    }

    private void processItems(String map){
        File myFile;
        Scanner reader;
        try {
            myFile = new File(map);
            reader = new Scanner(myFile);
            while (reader.hasNext()) {
                String[] cordsAndItems = reader.nextLine().split(";");
                Vector<String> item = new Vector<>();
                Collections.addAll(item, cordsAndItems[2]);
                Integer[] cordinants = {Integer.parseInt(cordsAndItems[0]),
                        Integer.parseInt(cordsAndItems[1])};
                if (itemLocation.containsKey(cordinants[0] + "."
                        + cordinants[1])) {
                    itemLocation.get(cordinants[0] + "."
                            + cordinants[1]).add(cordsAndItems[2]);
                } else {
                    itemLocation.put(cordinants[0] + "." + cordinants[1], item);
                }
            }
        } catch (Exception e) {
            System.out.println(e + "Error loading items for: " + map);
        }
        for(String j: itemLocation.keySet()){
            Vector<String> temp = new Vector<>(itemLocation.get(j));
            dItemLocation.put(j, temp);
        }
    }
    public Vector<String> getItems(final int[] location){
        if (itemLocation.containsKey(location[0] + "." + location[1])) {
            return itemLocation.get(location[0] + "." + location[1]);
        }
        return null;
    }
    public String grabItem(final int[] location, final String item) {
        StringBuilder rtnString = new StringBuilder();
        if (Objects.equals(item.toLowerCase(Locale.ROOT), "all")) {
            for (String i: itemLocation.get(location[0] + "." + location[1])) {
                rtnString.append(i).append(",");
                itemLocation.remove(location[0] + "." + location[1]);
            }
            return rtnString.toString();
        } else if (itemLocation.get(location[0] + "." + location[1]).contains(item)) {
            rtnString = new StringBuilder(itemLocation.get(location[0] + "." + location[1])
                    .get(itemLocation.get(location[0] + "." + location[1])
                            .indexOf(item)));
            itemLocation.get(location[0] + "." + location[1]).remove(item);
            if (itemLocation.get(location[0] + "." + location[1]).isEmpty()) {
                itemLocation.remove(location[0] + "." + location[1]);
            }
            return rtnString.toString();
        } else {
            return null;
        }

    }
    public void addItem(final int[] location, final String item){
        if (itemLocation.containsKey(location[0] + "." + location[1])) {
            itemLocation.get(location[0] + "." + location[1]).add(item);
        } else {
            itemLocation.put(location[0] + "." + location[1],
                    new Vector<>(List.of(item)));
        }
    }
    public void resetMap(){
        itemLocation.clear();
        for(String j: dItemLocation.keySet()){
            Vector<String> temp = new Vector<>(dItemLocation.get(j));
            itemLocation.put(j, temp);
        }
    }

}
