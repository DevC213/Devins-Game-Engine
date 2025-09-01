package com.gamelogic.core;

import com.gamelogic.map.Coordinates;
import com.gamelogic.villages.NPC;

import java.util.HashMap;
import java.util.Map;

public class NonPlayableCharacterRegistry {

    static int ID = 0;
    private static final Map<Coordinates, NPC> nonPlayableCharacters = new HashMap<>();

    public static NPC getNPC(Coordinates coordinates) {
        return nonPlayableCharacters.get(coordinates);
    }
    public static int addNPC(NPC npc, Coordinates coordinates) {
        int id = ID;
        ID++;
        nonPlayableCharacters.put(coordinates, npc);
        return id;
    }
    public static Map<Coordinates, NPC> getNonPlayableCharacters() {
        return nonPlayableCharacters;
    }
}
