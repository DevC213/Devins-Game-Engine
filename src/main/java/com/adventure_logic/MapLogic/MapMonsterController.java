package com.adventure_logic.MapLogic;

import com.Monsters.Monster;
import com.Monsters.MonsterFactory;
import com.adventure_logic.Adventure;


import java.io.File;
import java.util.*;

class MapMonsterController {
    /*
        Todo:
            Add monster logic
     */
    Map<String, Vector<Monster>> monsterVectorMap;
    Map<String, Vector<Monster>> defaultMonsterVectorMap;
    MonsterFactory monsterFactory;
    Adventure adventure = Adventure.getAdventure();
    MapMonsterController(String data){
        monsterFactory = new MonsterFactory();
        monsterVectorMap = new HashMap<>();
        defaultMonsterVectorMap = new HashMap<>();
        processFiles(data);
    }
    private void processFiles(String file){
        File myFile;
        Scanner reader;
        try {
            myFile = new File(file);
            reader = new Scanner(myFile);
            while (reader.hasNext()) {
                String[] monsterData =  reader.nextLine().split(";");
                if(monsterVectorMap.containsKey(monsterData[0]+"."+monsterData[1])){
                    monsterVectorMap.get(monsterData[0]+"."+monsterData[1]).add(monsterFactory.MonsterFac(Integer.parseInt(monsterData[2]),Integer.parseInt(monsterData[3]),monsterData[4]));
                } else{
                Vector<Monster> m = new Vector<>();
                m.add(monsterFactory.MonsterFac(Integer.parseInt(monsterData[2]),Integer.parseInt(monsterData[3]),monsterData[4]));
                monsterVectorMap.put(monsterData[0]+"."+monsterData[1],new Vector<>(m));
            }}
        } catch (Exception e) {
            System.out.println(e + "Error loading items for: " + file);
        }
        for(String j: monsterVectorMap.keySet()){
            Vector<Monster> temp = new Vector<>(monsterVectorMap.get(j));
            monsterVectorMap.put(j, temp);
        }
    }
    public Vector<String> getMonsters(final int[] location){
        Vector<String> rtnStrVec = new Vector<>();

        if (monsterVectorMap.containsKey(location[0] + "." + location[1])) {
            for(Monster i: monsterVectorMap.get(location[0] + "." + location[1])){
                rtnStrVec.add(i.getName() + ", Health: " + i.getHealth());
            }
            if(monsterVectorMap.get(location[0] + "." + location[1]).isEmpty()){
                return null;
            }
            return rtnStrVec;
        }
        return null;
    }
    public synchronized void attackMonster(String monster, int attack, final int[] location){
        boolean monsterKilled = false;
        int index = 0;
        if(monsterVectorMap.get(location[0] + "." + location[1]) == null){
            adventure.sendMessage("No monster on tile");
            return;
        }
        for(Monster i: monsterVectorMap.get(location[0] + "." + location[1])){
            if(i.getName().equalsIgnoreCase(monster)){
                i.attack(attack);
                if (i.getHealth() <= 0) {
                    monsterKilled = true;
                    index = monsterVectorMap.get(location[0] + "." + location[1]).indexOf(i);
                }

            }
        }
        if(monsterKilled){
            monsterVectorMap.get(location[0] + "." + location[1]).remove(index);
        }
    }

    public void resetMonsters(){
        monsterVectorMap.clear();
        for(String j: defaultMonsterVectorMap.keySet()){
            Vector<Monster> temp = new Vector<>(defaultMonsterVectorMap.get(j));
            defaultMonsterVectorMap.put(j, temp);
        }
    }

    public Vector<Double> getMonsterAttacks(int[] location) {

        Vector<Double> rtnVec = new Vector<>();
        if(monsterVectorMap.get(location[0] + "." + location[1]) == null){
            adventure.sendMessage("No monster on tile");
            return rtnVec;
        }
        for(Monster i: monsterVectorMap.get(location[0] + "." + location[1])){
            rtnVec.add(i.getBaseAttack());
        }
        return rtnVec;

    }
}
