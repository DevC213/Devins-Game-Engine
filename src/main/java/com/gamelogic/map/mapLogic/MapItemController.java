package com.gamelogic.map.mapLogic;

import com.armor.Armor;
import com.savesystem.ArmorData;
import com.savesystem.ItemState;
import com.savesystem.MapState;
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

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;

class MapItemController {


    private final Map<String,String> itemList = new HashMap<>();
    private final Map<Point2D, RecoveryItem> healingItems = new HashMap<>();
    private final Map<Point2D, Weapon> weapons = new HashMap<>();
    private final Map<Point2D, Armor> armorList = new HashMap<>();
    private final String itemFile;
    Point2D point2D = new Point2D.Double();
    Point2D maxCords = new Point2D.Double();
    public MapItemController(String map, Coordinates coordinates){
        itemFile = map;
        maxCords.setLocation(coordinates.x(),coordinates.y());
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
            weapons.put(new Point2D.Double(rWeapon.position()[0], rWeapon.position()[1]), new Weapon(rWeapon.name(), rWeapon.damage()));
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
            armorList.put(new Point2D.Double(rArmor.position()[0], rArmor.position()[1]), new Armor(rArmor.name(), rArmor.defence()));
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
            healingItems.put(new Point2D.Double(rRecoveryItem.position()[0], rRecoveryItem.position()[1]), new RecoveryItem(rRecoveryItem.name(), rRecoveryItem.hpRecovered()));
            itemList.put(rRecoveryItem.name(), "recoveryItem");
        }
    }
    public boolean itemsOnTile(Coordinates location){
        point2D = new Point2D.Double(location.x(),location.y());
        if(healingItems.containsKey(point2D)){return true;}
        else if(weapons.containsKey(point2D)){return true;}
        else return armorList.containsKey(point2D);
    }

    public Messenger grabItems(Coordinates location, final String item) {
        Messenger messenger = new Messenger();
        point2D = new Point2D.Double(location.x(),location.y());
        String tempItem = itemList.get(item);
        if(tempItem == null){
            return null;
        }
        return switch (tempItem) {
            case "weapon" -> {
                if (Objects.equals(weapons.get(point2D).name(), item)) {
                    messenger.setWeapon(weapons.get(point2D));
                    weapons.remove(point2D);
                } else {
                    messenger.setMessage("Invalid item");
                }
                yield messenger;
            }
            case "armor" -> {
                if (Objects.equals(armorList.get(point2D).name(), item)) {
                    messenger.setArmor(armorList.get(point2D));
                    armorList.remove(point2D);
                } else {
                    messenger.setMessage("Invalid item");
                }
                yield messenger;
            }
            case "recoveryItem" -> {
                if (Objects.equals(healingItems.get(point2D).getName(), item)) {
                    messenger.setHealingItem(healingItems.get(point2D));
                    healingItems.remove(point2D);
                } else {
                    messenger.setMessage("Invalid item");
                }
                yield messenger;
            }
            default -> null;
        };
    }
    public Messenger grabItem(Coordinates location) {
        Messenger messenger = new Messenger();
        point2D = new Point2D.Double(location.x(),location.y());
        String item = "";
        if(weapons.containsKey(point2D)){
            item =  weapons.get(point2D).name();
        } else if (armorList.containsKey(point2D)) {
            item = armorList.get(point2D).name();
        } else if (healingItems.containsKey(point2D)) {
            item = healingItems.get(point2D).getName();
        }
        return grabItems(location, item);
    }
    public void resetMap(){
        healingItems.clear();
        weapons.clear();
        armorList.clear();
        processItems(itemFile);
    }

    public Weapon weaponsOnTile(Coordinates location){
        point2D = new Point2D.Double(location.x(),location.y());
        if (weapons.containsKey(point2D)) {
            return weapons.get(point2D);
        }
        return null;
    }
    public Armor armorOnTile(Coordinates location) {
        point2D = new Point2D.Double(location.x(),location.y());
        if (armorList.containsKey(point2D)) {
            return armorList.get(point2D);
        }
        return null;
    }
    public RecoveryItem healingItemsOnTile(Coordinates location) {
        point2D = new Point2D.Double(location.x(),location.y());
        if (healingItems.containsKey(point2D)) {
            return healingItems.get(point2D);
        }
        return null;
    }

    public List<ItemState> getItems(){
        List<ItemState> itemList = new ArrayList<>();
        for(Point2D point2D : healingItems.keySet()){
            ItemState itemState = new ItemState();
            RecoveryItem recoveryItem = healingItems.get(point2D);
            itemState.name = recoveryItem.getName();
            itemState.type = this.itemList.get(itemState.name);
            itemState.value = recoveryItem.getHealValue();
            itemState.x = point2D.getX();
            itemState.y = point2D.getY();
            itemList.add(itemState);
        }
        return itemList;
    }
    public List<ArmorData> getArmor() {
        List<ArmorData> tempList = new ArrayList<>();

        for(Point2D point2D : armorList.keySet()){
            ArmorData armorData = new ArmorData();
            Armor armor = armorList.get(point2D);
            armorData.x = point2D.getX();
            armorData.y = point2D.getY();
            armorData.Name = armor.name();
            armorData.defence = armor.defence();
            tempList.add(armorData);
        }
        return tempList;
    }
    public List<WeaponState> getWeapons(){
        List<WeaponState> tempList = new ArrayList<>();
        for(Point2D point2D : weapons.keySet()){
            WeaponState weaponState = new WeaponState();
            Weapon weapon = weapons.get(point2D);
            weaponState.x = point2D.getX();
            weaponState.y = point2D.getY();
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
        Point2D point2D = new Point2D.Double();
        for(ItemState item : itemStates) {
            point2D.setLocation(item.x,item.y);
            healingItems.put(point2D, new RecoveryItem(item.name,item.value));
            itemList.put(item.name, "recoveryItem");
        }
    }

    public void loadArmor(List<ArmorData> armorData) {
        Point2D point2D = new Point2D.Double();
        armorList.clear();
        for(ArmorData armor: armorData){
            point2D.setLocation(armor.x,armor.y);
            armorList.put(point2D, new Armor(armor.Name,armor.defence));
            itemList.put(armor.Name, "armor");
        }
    }

    public void loadWeapons(List<WeaponState> weaponStates) {
        Point2D point2D = new Point2D.Double();
        weapons.clear();
        for(WeaponState weaponState: weaponStates){
            point2D.setLocation(weaponState.x, weaponState.y);
            weapons.put(point2D, new Weapon(weaponState.name, weaponState.attack));
            itemList.put(weaponState.name, "weapon");
        }
    }

    public int items(Coordinates location) {
        int items = 0;
        Point2D point2D = new Point2D.Double(location.x(),location.y());
        if(healingItems.containsKey(point2D)){
            items++;
        }
        if(armorList.containsKey(point2D)){
            items++;
        }
        if(weapons.containsKey(point2D)){
            items++;
        }
        return items;
    }
    public String itemOnTile(Coordinates coordinates){
        String item = "";
        if(healingItems.containsKey(point2D)){
            item = healingItems.get(point2D).getName();
        }
        if(armorList.containsKey(point2D)){
            item = armorList.get(point2D).name();
        }
        if(weapons.containsKey(point2D)){
            item = weapons.get(point2D).name();
        }
        return item;
    }
}
