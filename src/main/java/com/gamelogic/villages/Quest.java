package com.gamelogic.villages;

import java.util.ArrayList;
import java.util.List;

public class Quest {
    String title;
    int id;
    int reward;
    String prologueDialogue;
    List<String> activeDialogues;
    String epilogueDialogue;
    boolean completed;
    boolean active;


    public Quest(String title, int id, int reward, String prologueDialogue, String epilogueDialogue) {
        this.title = title;
        this.id = id;
        this.reward = reward;
        this.prologueDialogue = prologueDialogue;
        this.activeDialogues = new ArrayList<>();
        this.epilogueDialogue = epilogueDialogue;
        this.completed = false;
    }
    public void addActiveDialogues(String dialogue){
        activeDialogues.add(dialogue);
    }
    public List<String> getActiveDialogues(){
        return activeDialogues;
    }
    public int getId() {
        return id;
    }

    public String startQuest(){
        active = true;
        return prologueDialogue;
    }

    public String completeQuest(){
        completed = true;
        active = false;
        return epilogueDialogue;
    }
    public int getReward() {
        return reward;
    }

    public boolean isActive() {
        return active;
    }
}
