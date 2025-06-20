package com.gameLogic.MapLogic;

import com.Armor.Armor;
import com.Weapons.Weapon;
import com.gameLogic.Coordinates;
import com.gameLogic.MapLogic.rawClasses.RArmor;
import com.gameLogic.MapLogic.rawClasses.RRecoveryItem;
import com.gameLogic.MapLogic.rawClasses.RWeapon;
import com.gameLogic.Messenger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.recoveryItems.RecoveryItem;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
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
            InputStreamReader isr = new InputStreamReader(input, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(isr);
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
            e.printStackTrace();
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
        return switch (itemList.get(item)) {
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
}
