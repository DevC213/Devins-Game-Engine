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

    public Quest(String title, int id, int reward, String prologueDialogue, String epilogueDialogue) {
        this.title = title;
        this.id = id;
        this.reward = reward;
        this.prologueDialogue = prologueDialogue;
        this.activeDialogues = new ArrayList<>();
        this.epilogueDialogue = epilogueDialogue;
    }
    public void addActiveDialogues(String dialogue){
        activeDialogues.add(dialogue);
    }
    public int getId() {
        return id;
    }
    public int getReward() {
        return reward;
    }
}
