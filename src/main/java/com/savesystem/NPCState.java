package com.savesystem;

import java.util.ArrayList;
import java.util.List;

public class NPCState {
    public String name;
    public List<String> dialogue;
    public List<Integer> quests;
    public int totalQuests;
    public int currentQuest;
    public int x;
    public int y;
    public NPCState(){
        dialogue = new ArrayList<>();
        quests = new ArrayList<>();
    }
}
