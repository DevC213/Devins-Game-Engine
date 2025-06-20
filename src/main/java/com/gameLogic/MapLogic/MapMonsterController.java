package com.gameLogic.MapLogic;

import com.Monsters.Monster;
import com.Monsters.MonsterFactory;
import com.gameLogic.Coordinates;
import com.gameLogic.MapLogic.rawClasses.RMonster;
import com.gameLogic.Messenger;
import com.gameLogic.SpawnTable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;



import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;

class MapMonsterController {

    Map<Coordinates, List<Monster>> monsterVectorMap;
    Map<String, SpawnTable> spawnChanges;
    MonsterFactory monsterFactory;
    private final String monsterLocations;
    MapMonsterController(String fileLocation) {
        this.monsterLocations = fileLocation;
        monsterFactory = new MonsterFactory();
        monsterVectorMap = new HashMap<>();
        spawnChanges = new HashMap<>();
        processFiles(fileLocation);
    }
    public void processFiles(String file){
        Gson gson = new Gson();
        InputStream input = Objects.requireNonNull(getClass().getResourceAsStream(file));
        InputStreamReader reader = new InputStreamReader(input);
        Type listType = new TypeToken<List<RMonster>>() {}.getType();
        List<RMonster> tempMonsterList = gson.fromJson(reader, listType);
        for(RMonster rMonster : tempMonsterList) {
            Coordinates coordinates = new Coordinates(rMonster.position()[0], rMonster.position()[1]);
            List<Monster> tempList = new ArrayList<>();
            for (int i = 0; i < rMonster.quantity(); i++) {
                tempList.add(monsterFactory.MonsterFac(rMonster.damage(), rMonster.health(), rMonster.name(), i + 1));
            }
            monsterVectorMap.put(coordinates, tempList);
        }
    }
    public void processSpawnChances(String file) {
        InputStream input = Objects.requireNonNull(getClass().getResourceAsStream(file));;
        Gson gson = new Gson();
        InputStreamReader reader = new InputStreamReader(input);
        Type listType = new TypeToken<List<SpawnTable>>() {}.getType();
        List<SpawnTable> tempTable = gson.fromJson(reader, listType);
        for(SpawnTable spawnTable : tempTable) {
            spawnChanges.put(spawnTable.name(), spawnTable);
        }
    }
    public void spawnMonster(Coordinates location){
        int number = (int) (Math.random() * 100);
        int cumulative = 0;
        for(String j: spawnChanges.keySet()) {
            cumulative += spawnChanges.get(j).weight();
            if (number < cumulative) {
                monsterVectorMap.put(location, new ArrayList<>(Collections.singletonList(monsterFactory.MonsterFac(spawnChanges.get(j).damage(), spawnChanges.get(j).hp(), j, 1))));
                return;
            }
        }
    }
    public List<String> getMonsters(Coordinates location){
        List<String> rtnStrVec = new ArrayList<>();
        List<Monster> monsters = monsterVectorMap.get(location);
        Map<String,Integer> monstersNum = new HashMap<>();
        if(monsters == null){
            return null;
        }
        for(Monster i: monsters){
            monstersNum.merge(i.getName(),1,Integer::sum);
        }
        if (!monsters.isEmpty()) {
            for(Monster i: monsters){
                if(monstersNum.get(i.getName()) > 1) {
                    rtnStrVec.add(i.getFullName() + ": Health - " + i.getHealth());
                } else {
                    rtnStrVec.add(i.getName() + ": Health - " + i.getHealth());
                }
            }
            return rtnStrVec;
        }
        return null;
    }
    public synchronized Messenger attackMonsters(String monster, int attack, Coordinates location){
        Messenger rtnMessage = new Messenger();
        boolean monsterKilled = false;
        List<Monster> monsters = monsterVectorMap.get(location);
        Map<String,Integer> monstersNum = new HashMap<>();

        int index = 0;
        if(monsters == null){
            rtnMessage.setMessage("No monster on tile");
            return rtnMessage;
        }
        for(Monster i: monsters){
            monstersNum.merge(i.getName(),1,Integer::sum);
        }
        if(monstersNum.containsKey(monster) && monstersNum.get(monster) > 1){
            rtnMessage.setMessage("What "+ monster+ "? Enter name with number.");
            return rtnMessage;
        }
        for(Monster i: monsters){
            if(i.getName().equalsIgnoreCase(monster) || i.getFullName().equalsIgnoreCase(monster)){
                i.attack(attack);
                if (i.getHealth() <= 0) {
                    monsterKilled = true;
                    index = monsterVectorMap.get(location).indexOf(i);
                }
            }
        }

        if(monsterKilled) {
            if (monstersNum.get(monsters.get(index).getName()) > 1) {
                rtnMessage.setMessage(monsters.get(index).getFullName() + " Was killed");
            } else {
                rtnMessage.setMessage(monsters.get(index).getName() + " Was killed");
            }
            monsterVectorMap.get(location).remove(index);
        }
        return rtnMessage;
    }
    public void resetMonsters(){
        monsterVectorMap.clear();
        processFiles(monsterLocations);
    }
    public Messenger getMonsterAttack(Coordinates location){
        Messenger rtnMessenger = new Messenger();
        Vector<Double> rtnVec = new Vector<>();
        if(monsterVectorMap.get(location) == null){
            rtnMessenger = new Messenger("No monster on tile");
            return rtnMessenger;
        }
        for(Monster i: monsterVectorMap.get(location)){
            rtnVec.add(i.getBaseAttack());
        }
        rtnMessenger.addPayloadD(rtnVec);
        return rtnMessenger;
    }
}
