package com.gamelogic.villages;

import com.gamelogic.map.Coordinates;
import com.savesystem.NPCState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NPC {
    private static int questID = 0;
    private final List<String> dialogue;
    private final Coordinates coordinates;
    private final String name;
    private final String Quest;
    private final List<Quest> quests;
    private final int totalQuests;
    private int activeQuest;
    private final Random random = new Random();

    public NPC(Coordinates coordinates, String name, String Quest) {
        this.dialogue = new ArrayList<>();
        quests = new ArrayList<>();
        this.coordinates = coordinates;
        this.name = name;
        this.Quest = Quest;
        quests.add(new Quest("test", questID++, 0,"",""));
        totalQuests = quests.size();
        activeQuest = 0;
    }
    public void addDialogue(String dialogue) {
        this.dialogue.add(dialogue);
    }
    public String getDialogue() {

        List<String> dialogueList = dialogue;

        if (dialogue.isEmpty()) {
            return "";
        }
       if(activeQuest != -1){
            dialogue.addAll(quests.get(activeQuest).getActiveDialogues());
        }
        return dialogueList.get(random.nextInt(dialogueList.size()));
    }
    public String getName() {
        return name;
    }
    public Coordinates getCoordinates() {
        return coordinates;
    }
    public String getQuest() {
        return Quest;
    }
    public NPCState createNPCState(){
        NPCState state = new NPCState();
        state.x = coordinates.x();
        state.y = coordinates.y();
        state.currentQuest = activeQuest;
        state.totalQuests = totalQuests;
        for(Quest q: quests){
            state.quests.add(q.id);
        }
        state.dialogue = this.dialogue;
        return state;

    }
}
