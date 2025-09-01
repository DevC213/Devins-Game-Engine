package com.gamelogic.map.mapLogic;

import com.gamelogic.map.Coordinates;
import com.gamelogic.rawdataclasses.RVillager;
import com.gamelogic.villages.NPC;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MapNPCController {

    private final Map<Coordinates, NPC> NPCs = new HashMap<>();
    private final String mapPath;

    public MapNPCController(String mapPath) {
        this.mapPath = mapPath;
        processFile();
    }
    public void processFile() {
        Gson gson = new Gson();
        InputStream input = Objects.requireNonNull(getClass().getResourceAsStream(this.mapPath));
        InputStreamReader reader = new InputStreamReader(input);

        Type listType = new TypeToken<List<RVillager>>(){}.getType();
        List<RVillager> tempNPCs = gson.fromJson(reader, listType);
        for(RVillager rVillager : tempNPCs) {
            Coordinates location = new Coordinates(rVillager.location()[0], rVillager.location()[1]);
            NPC npc = new NPC(location, rVillager.name(), rVillager.quests());
            for(String dialogue: rVillager.messages()){
                npc.addDialogue(dialogue);
            }
            NPCs.put(location, npc);
        }
    }
    public NPC getNPC(Coordinates coordinates) {
        return NPCs.get(coordinates);
    }
    public void addNPC(Coordinates coordinates, NPC npc) {
        NPCs.put(coordinates, npc);
    }
    public void resetNPCS() {
        NPCs.clear();
        processFile();
    }
}
