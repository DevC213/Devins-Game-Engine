package com.adventure_logic.MapLogic;

import com.Monsters.Monster;
import com.Monsters.MonsterFactory;
import com.adventure_logic.GuiEventListener;


import java.io.InputStream;
import java.util.*;

class MapMonsterController {
    /*
        Todo:
            Add monster logic
     */
    Map<String, Vector<Monster>> monsterVectorMap;
    Map<String, Vector<Monster>> defaultMonsterVectorMap;
    MonsterFactory monsterFactory;
    GuiEventListener guiEventListener;
    MapMonsterController(String data,GuiEventListener guiEventListener){
        monsterFactory = new MonsterFactory();
        monsterVectorMap = new HashMap<>();
        defaultMonsterVectorMap = new HashMap<>();
        this.guiEventListener = guiEventListener;
        processFiles(data);
    }
    private void processFiles(String file){
        InputStream input;
        Scanner reader;
        Map<String,Integer> monsters = new TreeMap<>();
        String key;
        try {
            input = Objects.requireNonNull(getClass().getResourceAsStream(file));
            reader = new Scanner(input);
            while (reader.hasNext()) {
                String[] monsterData =  reader.nextLine().split(";");
                key = monsterData[0] + "." + monsterData[1];
                if(!monsters.containsKey(key + monsterData[4])) {
                    monsters.put(key + monsterData[4], 1);
                } else{
                    monsters.replace(key + monsterData[4], monsters.get(key + monsterData[4]) + 1);
                }
                if(monsterVectorMap.containsKey(key)){
                    monsterVectorMap.get(key).add(monsterFactory.MonsterFac(Integer.parseInt(monsterData[2]),Integer.parseInt(monsterData[3]),monsterData[4], monsters.get(key + monsterData[4])));
                } else {
                Vector<Monster> m = new Vector<>();
                m.add(monsterFactory.MonsterFac(Integer.parseInt(monsterData[2]),Integer.parseInt(monsterData[3]),monsterData[4],monsters.get(key + monsterData[4])));
                monsterVectorMap.put(key,new Vector<>(m));
            }}
        } catch (Exception e) {
            guiEventListener.UIUpdate(e + "Error loading monsters for: " + file, 0);
        }
        for(String j: monsterVectorMap.keySet()){
            Vector<Monster> temp = new Vector<>(monsterVectorMap.get(j));
            monsterVectorMap.put(j, temp);
        }
    }
    public Vector<String> getMonsters(final int[] location){
        String key = location[0] + "." + location[1];
        Vector<String> rtnStrVec = new Vector<>();
        Vector<Monster> monsters = monsterVectorMap.get(key);
        Map<String,Integer> monstersNum = new HashMap<>();
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
            if(monsters.isEmpty()){
                return null;
            }
            return rtnStrVec;
        }
        return null;
    }
    public synchronized void attackMonster(String monster, int attack, final int[] location){
        boolean monsterKilled = false;
        Vector<Monster> monsters = monsterVectorMap.get(location[0] + "." + location[1]);
        Map<String,Integer> monstersNum = new HashMap<>();

        int index = 0;
        if(monsters == null){
            guiEventListener.UIUpdate("No monster on tile",0);
            return;
        }
        for(Monster i: monsters){
            monstersNum.merge(i.getName(),1,Integer::sum);
        }
        if(monstersNum.containsKey(monster) && monstersNum.get(monster) > 1){
            guiEventListener.UIUpdate("What "+ monster+ "? Enter name with number.",0);
            return;
        }
        for(Monster i: monsters){
            if(i.getName().equalsIgnoreCase(monster) || i.getFullName().equalsIgnoreCase(monster)){
                i.attack(attack);
                if (i.getHealth() <= 0) {
                    monsterKilled = true;
                    index = monsterVectorMap.get(location[0] + "." + location[1]).indexOf(i);
                }
            }
        }

        if(monsterKilled) {
                if (monstersNum.get(monsters.get(index).getName()) > 1) {
                    guiEventListener.UIUpdate(monsters.get(index).getFullName() + " Was killed", 0);
                } else {
                    guiEventListener.UIUpdate(monsters.get(index).getName() + " Was killed", 0);
                }
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
            guiEventListener.UIUpdate("No monster on tile",0);
            return rtnVec;
        }
        for(Monster i: monsterVectorMap.get(location[0] + "." + location[1])){
            rtnVec.add(i.getBaseAttack());
        }
        return rtnVec;

    }
}
