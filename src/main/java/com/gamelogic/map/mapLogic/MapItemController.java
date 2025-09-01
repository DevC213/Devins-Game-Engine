package com.gamelogic.map.mapLogic;

import com.armor.Armor;
import com.savesystem.ArmorData;
import com.savesystem.ItemState;
import com.savesystem.WeaponState;
import com.weapons.Weapon;
import com.gamelogic.map.Coordinates;
import com.gamelogic.rawdataclasses.RArmor;
import com.gamelogic.rawdataclasses.RRecoveryItem;
import com.gamelogic.rawdataclasses.RWeapon;
import com.gamelogic.messaging.Messenger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.recoveryitems.RecoveryItem;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;

class MapItemController {


    private final Map<String,String> itemList = new HashMap<>();
    private final Map<Coordinates, RecoveryItem> healingItems = new HashMap<>();
    private final Map<Coordinates, Weapon> weapons = new HashMap<>();
    private final Map<Coordinates, Armor> armorList = new HashMap<>();
    private final String itemFile;
    public MapItemController(String map){
        itemFile = map;
        processItems(itemFile);
    }

    private void processItems(String map){
        InputStream input;
        try {
            input = Objects.requireNonNull(getClass().getResourceAsStream(map));
            InputStreamReader streamReader = new InputStreamReader(input);
            BufferedReader reader = new BufferedReader(streamReader);
            Gson gson = new Gson();
            Type mapType = new TypeToken<Map<String, String>>() {}.getType();
            Map<String, String> levelMap = gson.fromJson(reader, mapType);
            for(Map.Entry<String, String> entry : levelMap.entrySet()){
                String file = entry.getKey();
                String path = entry.getValue();
                switch (file) {
                    case "armor":
                        processArmor(path);
                        break;
                    case "weapons":
                        processWeapons(path);
                        break;
                    case "recoveryItems":
                        processHealingItems(path);
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid file found");

                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    private void processWeapons(String map){
        Gson gson = new Gson();
        InputStream input = Objects.requireNonNull(getClass().getResourceAsStream(map));
        InputStreamReader reader = new InputStreamReader(input);
        Type listType = new TypeToken<List<RWeapon>>() {}.getType();
        List<RWeapon> tempWeaponList = gson.fromJson(reader, listType);
        for(RWeapon rWeapon : tempWeaponList){
            weapons.put(new Coordinates(rWeapon.position()[0], rWeapon.position()[1]), new Weapon(rWeapon.name(), rWeapon.damage()));
            itemList.put(rWeapon.name(), "weapon");
        }
    }
    private void processArmor(String map){
        Gson gson = new Gson();
        InputStream input = Objects.requireNonNull(getClass().getResourceAsStream(map));
        InputStreamReader reader = new InputStreamReader(input);
        Type listType = new TypeToken<List<RArmor>>() {}.getType();
        List<RArmor> tempArmorList = gson.fromJson(reader, listType);
        for(RArmor rArmor : tempArmorList){
            armorList.put(new Coordinates(rArmor.position()[0], rArmor.position()[1]), new Armor(rArmor.name(), rArmor.defence()));
            itemList.put(rArmor.name(), "armor");
        }
    }
    private void processHealingItems(String map){
        Gson gson = new Gson();
        InputStream input = Objects.requireNonNull(getClass().getResourceAsStream(map));
        InputStreamReader reader = new InputStreamReader(input);
        Type listType = new TypeToken<List<RRecoveryItem>>() {}.getType();
        List<RRecoveryItem> tempRecoveryList = gson.fromJson(reader, listType);
        for(RRecoveryItem rRecoveryItem : tempRecoveryList){
            healingItems.put(new Coordinates(rRecoveryItem.position()[0], rRecoveryItem.position()[1]), new RecoveryItem(rRecoveryItem.name(), rRecoveryItem.hpRecovered()));
            itemList.put(rRecoveryItem.name(), "recoveryItem");
        }
    }
    public boolean itemsOnTile(Coordinates location){
        if(healingItems.containsKey(location)){return true;}
        else if(weapons.containsKey(location)){return true;}
        else return armorList.containsKey(location);
    }

    public Messenger grabItems(Coordinates location, final String item) {
        Messenger messenger = new Messenger();
        String tempItem = itemList.get(item);
        if(tempItem == null){
            return new Messenger();
        }
        return switch (tempItem) {
            case "weapon" -> {
                if (Objects.equals(weapons.get(location).name(), item)) {
                    messenger.setWeapon(weapons.get(location));
                    weapons.remove(location);
                } else {
                    messenger.setMessage("Invalid item");
                }
                yield messenger;
            }
            case "armor" -> {
                if (Objects.equals(armorList.get(location).name(), item)) {
                    messenger.setArmor(armorList.get(location));
                    armorList.remove(location);
                } else {
                    messenger.setMessage("Invalid item");
                }
                yield messenger;
            }
            case "recoveryItem" -> {
                if (Objects.equals(healingItems.get(location).getName(), item)) {
                    messenger.setHealingItem(healingItems.get(location));
                    healingItems.remove(location);
                } else {
                    messenger.setMessage("Invalid item");
                }
                yield messenger;
            }
            default -> null;
        };
    }
    public void resetMap(){
        healingItems.clear();
        weapons.clear();
        armorList.clear();
        processItems(itemFile);
    }
    public Weapon weaponsOnTile(Coordinates location){
        return weapons.get(location);

    }
    public Armor armorOnTile(Coordinates location) {
        return armorList.get(location);

    }
    public RecoveryItem healingItemsOnTile(Coordinates location) {
        return healingItems.get(location);
    }

    public List<ItemState> getItems(){
        List<ItemState> itemList = new ArrayList<>();
        for(Coordinates coordinates : healingItems.keySet()){
            ItemState itemState = new ItemState();
            RecoveryItem recoveryItem = healingItems.get(coordinates);
            itemState.name = recoveryItem.getName();
            itemState.type = this.itemList.get(itemState.name);
            itemState.value = recoveryItem.getHealValue();
            itemState.x = coordinates.x();
            itemState.y = coordinates.y();
            itemList.add(itemState);
        }
        return itemList;
    }
    public List<ArmorData> getArmor() {
        List<ArmorData> tempList = new ArrayList<>();

        for(Coordinates coordinates : armorList.keySet()){
            ArmorData armorData = new ArmorData();
            Armor armor = armorList.get(coordinates);
            armorData.x = coordinates.x();
            armorData.y = coordinates.y();
            armorData.Name = armor.name();
            armorData.defence = armor.defence();
            tempList.add(armorData);
        }
        return tempList;
    }
    public List<WeaponState> getWeapons(){
        List<WeaponState> tempList = new ArrayList<>();
        for(Coordinates coordinates : weapons.keySet()){
            WeaponState weaponState = new WeaponState();
            Weapon weapon = weapons.get(coordinates);
            weaponState.x = coordinates.x();
            weaponState.y = coordinates.y();
            weaponState.name = weapon.name();
            weaponState.attack = weapon.damage();
            tempList.add(weaponState);
        }
        return tempList;
    }
    public void clearItemList(){
        itemList.clear();
    }
    public void loadItems(List<ItemState> itemStates) {
        healingItems.clear();
        Coordinates coordinates;
        for(ItemState item : itemStates) {
            coordinates = new Coordinates(item.x,item.y);
            healingItems.put(coordinates, new RecoveryItem(item.name,item.value));
            itemList.put(item.name, "recoveryItem");
        }
    }

    public void loadArmor(List<ArmorData> armorData) {
        armorList.clear();
        Coordinates coordinates;
        for(ArmorData armor: armorData){
            coordinates = new Coordinates(armor.x,armor.y);
            armorList.put(coordinates, new Armor(armor.Name,armor.defence));
            itemList.put(armor.Name, "armor");
        }
    }

    public void loadWeapons(List<WeaponState> weaponStates) {
        weapons.clear();
        Coordinates coordinates;
        for(WeaponState weaponState: weaponStates){
            coordinates = new Coordinates(weaponState.x, weaponState.y);
            weapons.put(coordinates, new Weapon(weaponState.name, weaponState.attack));
            itemList.put(weaponState.name, "weapon");
        }
    }

    public int items(Coordinates location) {
        int items = 0;
        if(healingItems.containsKey(location)){
            items++;
        }
        if(armorList.containsKey(location)){
            items++;
        }
        if(weapons.containsKey(location)){
            items++;
        }
        return items;
    }
    public String itemOnTile(Coordinates coordinates){
        String item = "";
        if(healingItems.containsKey(coordinates)){
            item = healingItems.get(coordinates).getName();
        }
        if(armorList.containsKey(coordinates)){
            item = armorList.get(coordinates).name();
        }
        if(weapons.containsKey(coordinates)){
            item = weapons.get(coordinates).name();
        }
        return item;
    }
}
