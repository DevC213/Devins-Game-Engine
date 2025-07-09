package com.gamelogic.villages;

import com.gamelogic.map.Coordinates;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NPC {
    private final List<String> dialogue;
    private final Coordinates coordinates;
    private final String name;
    private final String Quest;
    private final Random random = new Random();

    public NPC(Coordinates coordinates, String name, String Quest) {
        this.dialogue = new ArrayList<>();
        this.coordinates = coordinates;
        this.name = name;
        this.Quest = Quest;
    }
    public void addDialogue(String dialogue) {
        this.dialogue.add(dialogue);
    }
    public String getDialogue() {
        if (dialogue.isEmpty()) {
            return "";
        }
        return dialogue.get(random.nextInt(dialogue.size()));
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
}
