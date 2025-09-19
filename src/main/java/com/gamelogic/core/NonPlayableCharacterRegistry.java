package com.gamelogic.core;

import com.gamelogic.map.Coordinates;
import com.gamelogic.villages.NPC;
import com.savesystem.QuestData;

import java.util.HashMap;
import java.util.Map;

public class NonPlayableCharacterRegistry {

    static int ID = 0;
    private static final Map<Integer, NPC> nonPlayableCharacters = new HashMap<>();

    public static NPC getNPC(int id) {
        return nonPlayableCharacters.get(id);
    }
    public static int addNPC(NPC npc) {
        int id = ID;
        ID++;
        nonPlayableCharacters.put(id, npc);
        return id;
    }
    public static Map<Integer, NPC> getNonPlayableCharacters() {
        return nonPlayableCharacters;
    }
    public static void loadNonPlayableCharacters(QuestData questData) {

    }
}
