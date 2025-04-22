package com.adventure_logic.MapLogic;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Vector;

class MapMovementController {

    MapController mapController;
    MapMovementController(MapController mapController){
        this.mapController = mapController;
    }
    private final Vector<String> damage_tiles = new Vector<>(Arrays.asList("l", "p", "a", "X", "&", "G", "t"));
    private final Vector<Integer> damage = new Vector<>(Arrays.asList(5,8,11,3,7,5,2));
    public boolean getMovementOrDamage(final String terrain, final int command){
        System.out.println(terrain);
        switch (command) {
            case 0 -> {
                return damage_tiles.contains(terrain);
            }
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
    public int getVisibility(final String terrain){
        for (Vector<String> j : mapController.getKey()) {
            if (j.contains(terrain)) {
                return Integer.parseInt(j.getLast());
            }
        }
        return 0;
    }
    public int Damage(final String terrain) {
        if (damage_tiles.contains(terrain)) {
            return damage.get(damage_tiles.indexOf(terrain));
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

