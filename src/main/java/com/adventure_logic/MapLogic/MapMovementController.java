package com.adventure_logic.MapLogic;

import java.util.*;

class MapMovementController {

    MapController mapController;
    private final Map<String,Integer> damageMap = new HashMap<>();
    MapMovementController(MapController mapController){
        this.mapController = mapController;
        initializeDamageMap();
    }

    private void initializeDamageMap(){
        damageMap.put("l", 5);
        damageMap.put("p", 8);
        damageMap.put("a", 11);
        damageMap.put("X", 3);
        damageMap.put("&", 7);
        damageMap.put("G", 5);
        damageMap.put("t", 2);
        damageMap.put("H", -3);
        damageMap.put("A", -5);
    }
    public boolean getCanCross(final String terrain, final int command){
        switch (command) {
            case 1 -> {
                for (Vector<String> j : mapController.getKey()) {
                    if (j.contains(terrain)) {
                        return j.get(1).toLowerCase(Locale.ROOT).equals("ladder") || j.get(1).toLowerCase(Locale.ROOT).equals("cave");
                    }
                }
            }
            case 2 -> {
                for (Vector<String> j : mapController.getKey()) {
                    if (j.contains(terrain)) {
                        return Objects.equals(j.get(j.size() - 2), "yes");
                    }
                }
            }
            default -> {
                return false;
            }
        }
        return false;
    }
    public int getDamage(final String terrain){
        if (damageMap.containsKey(terrain)){
            return damageMap.get(terrain);
        }
        return 0;
    }
    public int getVisibility(final String terrain){
        for (Vector<String> j : mapController.getKey()) {
            if (j.contains(terrain)) {
                return Integer.parseInt(j.getLast());
            }
        }
        return 0;
    }
    public boolean isLadder(final String terrain){
        for (Vector<String> j : mapController.getKey()) {
            if (j.contains(terrain)) {
                return j.get(1).toLowerCase(Locale.ROOT).equals("ladder");
            }
        }
        return false;
    }
    public boolean isCave(final String terrain){
        for (Vector<String> j : mapController.getKey()) {
            if (j.contains(terrain)) {
                return j.get(1).toLowerCase(Locale.ROOT).equals("cave");
            }
        }
        return false;
    }
}

