package com.savesystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapState {
    public int ID;
    public String type;
    public Map<Integer,List<ArmorData>> armorList;
    public Map<Integer,List<ItemState>> recoveryItemsList;
    public Map<Integer,List<WeaponState>> weaponList;
    public Map<Integer,List<MonsterState>> monsterList;
    public List<List<List<String>>> map;
    public MapState(){
        armorList = new HashMap<>();
        recoveryItemsList = new HashMap<>();
        weaponList = new HashMap<>();
        monsterList = new HashMap<>();
        map = new ArrayList<>();
    }
}
