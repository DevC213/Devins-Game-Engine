package com.gameLogic.MapLogic;

import com.Monsters.Monster;
import com.Monsters.MonsterFactory;
import com.gameLogic.Messenger;


import java.io.InputStream;
import java.util.*;

class MapMonsterController {

    Map<String, Vector<Monster>> monsterVectorMap;
    Map<String, Vector<String>> spawnChanges;
    MonsterFactory monsterFactory;
    private final String monsterLocations;
    int mapSizeX;
    int mapSizeY;
    MapMonsterController(int[] rowsAndColumns, String fileLocation) {
        this.monsterLocations = fileLocation;
        monsterFactory = new MonsterFactory();
        monsterVectorMap = new HashMap<>();
        spawnChanges = new HashMap<>();
        this.mapSizeX = rowsAndColumns[0];
        this.mapSizeY = rowsAndColumns[1];
    }
    public Messenger processFiles(String file){
        Messenger messenger = new Messenger();
        InputStream input;
        Scanner reader;
        Map<String,Integer> monsters = new TreeMap<>();
        String key;
        try {
            input = Objects.requireNonNull(getClass().getResourceAsStream(file));
            reader = new Scanner(input);
            while (reader.hasNext()) {
                String[] monsterData =  reader.nextLine().split(";");
                key = keyString(monsterData);
                monsters.merge(key + monsterData[4], 1, Integer::sum);
                if(monsterVectorMap.containsKey(key)){
                    monsterVectorMap.get(key).add(monsterFactory.MonsterFac(Integer.parseInt(monsterData[2]),Integer.parseInt(monsterData[3]),monsterData[4], monsters.get(key + monsterData[4])));
                } else {
                Vector<Monster> m = new Vector<>();
                m.add(monsterFactory.MonsterFac(Integer.parseInt(monsterData[2]),Integer.parseInt(monsterData[3]),monsterData[4],monsters.get(key + monsterData[4])));
                monsterVectorMap.put(key,new Vector<>(m));
            }}
        } catch (Exception e) {
            messenger.setMessage(e + "Error loading monsters for: " + file);
        }
        return messenger;
    }
    public Messenger processSpawnChances(String file) {
        Messenger messenger = new Messenger();
        InputStream input;
        Scanner reader;
        try {
            input = Objects.requireNonNull(getClass().getResourceAsStream(file));
            reader = new Scanner(input);
            while (reader.hasNext()) {
                Vector<String> monsterData = new Vector<>(List.of(reader.nextLine().split(";")));
                String monster = monsterData.getFirst();
                monsterData.removeFirst();
                spawnChanges.put(monster, monsterData);
            }
        } catch (Exception e) {
            messenger.setMessage(e + "Error loading monsters for: " + file);
        }
        return messenger;
    }
    public void spawnMonster(int[] location){
        int number = (int) (Math.random() * 100);
        int cumulative = 0;
        for(String j: spawnChanges.keySet()) {
            cumulative += Integer.parseInt(spawnChanges.get(j).getFirst());
            if (number < cumulative) {
                monsterVectorMap.put(keyString(location), new Vector<>(Collections.singletonList(monsterFactory.MonsterFac(Integer.parseInt(spawnChanges.get(j).get(2)), Integer.parseInt(spawnChanges.get(j).get(1)), j, 1))));
                return;
            }
        }
    }
    public Vector<String> getMonsters(final int[] location){
        String key = keyString(location);
        Vector<String> rtnStrVec = new Vector<>();
        Vector<Monster> monsters = monsterVectorMap.get(key);
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
    public synchronized Messenger attackMonsters(String monster, int attack, final int[] location){
        Messenger rtnMessage = new Messenger();
        boolean monsterKilled = false;
        Vector<Monster> monsters = monsterVectorMap.get(keyString(location));
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
                    index = monsterVectorMap.get(keyString(location)).indexOf(i);
                }
            }
        }

        if(monsterKilled) {
            if (monstersNum.get(monsters.get(index).getName()) > 1) {
                rtnMessage.setMessage(monsters.get(index).getFullName() + " Was killed");
            } else {
                rtnMessage.setMessage(monsters.get(index).getName() + " Was killed");
            }
            monsterVectorMap.get(keyString(location)).remove(index);
        }
        return rtnMessage;
    }
    public void resetMonsters(){
        monsterVectorMap.clear();
        processFiles(monsterLocations);
    }
    public Messenger getMonsterAttack(int[] location){
        Messenger rtnMessenger = new Messenger();
        Vector<Double> rtnVec = new Vector<>();
        if(monsterVectorMap.get(keyString(location)) == null){
            rtnMessenger = new Messenger("No monster on tile");
            return rtnMessenger;
        }
        for(Monster i: monsterVectorMap.get(keyString(location))){
            rtnVec.add(i.getBaseAttack());
        }
        rtnMessenger.addPayloadD(rtnVec);
        return rtnMessenger;
    }
    private String keyString(int[] location){
        return location[0] + "." + location[1];
    }
    private String keyString(String[] location){
        return location[0] + "." + location[1];
    }
}
