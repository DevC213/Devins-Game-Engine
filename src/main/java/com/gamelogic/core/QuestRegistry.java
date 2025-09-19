package com.gamelogic.core;

import com.gamelogic.villages.Quest;

import java.util.HashMap;
import java.util.Map;

public class QuestRegistry {

    private static final Map<Integer, Quest> questRegistry = new HashMap<>();

    public static void addQuest(Quest quest){
        questRegistry.put(quest.getId(), quest);
    }
    public static Quest getQuest(int id){
        return questRegistry.get(id);
    }
}
