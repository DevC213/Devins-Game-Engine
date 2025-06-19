package com.gameLogic.MapLogic;

import com.Armor.Armor;
import com.Weapons.Weapon;
import com.gameLogic.Messenger;
import com.recoveryItems.HealingItem;

import java.awt.geom.Point2D;
import java.io.InputStream;
import java.util.*;

class MapItemController {


    private final Map<String,String> itemList = new HashMap<>();
    private final Map<Point2D, HealingItem> healingItemsP = new HashMap<>();
    private final Map<Point2D, Weapon> weaponsP = new HashMap<>();
    private final Map<Point2D, Armor> armorP = new HashMap<>();
    private final String itemFile;
    private final int[] mapSize;

    Point2D point2D = new Point2D.Double();

    public MapItemController(String map, int[] mapSize){
        itemFile = map;
        this.mapSize = mapSize;
        processItems(itemFile);
    }

    private void processItems(String map){
        InputStream input;
        Scanner reader;
        int line = 0;
        try {
            input = Objects.requireNonNull(getClass().getResourceAsStream(map));
            reader = new Scanner(input);
            while (reader.hasNext()) {
                switch(line){
                    case 0 -> processArmor(reader.nextLine());
                    case 1 -> processHealingItems(reader.nextLine());
                    case 2 -> processWeapons(reader.nextLine());
                    default -> {}
                }
                line++;
            }
        } catch (Exception _) { }
    }
    private void processWeapons(String map){
        InputStream input;
        Scanner reader;

        try{
            input = Objects.requireNonNull(getClass().getResourceAsStream(map));
            reader = new Scanner(input);

            while (reader.hasNext()) {
                point2D = new Point2D.Double();
                Vector<String> weaponData = new Vector<>(List.of(reader.nextLine().split(";")));
                point2D.setLocation(Double.parseDouble(weaponData.get(0)),Double.parseDouble(weaponData.get(1)));
                //weapons.put(weaponData.get(0)+"."+weaponData.get(1),new Weapon(weaponData.get(2), Integer.parseInt(weaponData.get(3))));
                weaponsP.put(point2D,new Weapon(weaponData.get(2), Integer.parseInt(weaponData.get(3))));
                itemList.put(weaponData.get(2), "weapon");
            }
        } catch (Exception _) { }
    }
    private void processArmor(String map){
        InputStream input;
        Scanner reader;
        try{
            input = Objects.requireNonNull(getClass().getResourceAsStream(map));
            reader = new Scanner(input);
            while (reader.hasNext()) {
                point2D = new Point2D.Double();
                Vector<String> armorData = new Vector<>(List.of(reader.nextLine().split(";")));
                point2D.setLocation(Double.parseDouble(armorData.get(0)),Double.parseDouble(armorData.get(1)));
                armorP.put(point2D,new Armor(armorData.get(2), Integer.parseInt(armorData.get(3))));
                itemList.put(armorData.get(2), "armor");
            }
        } catch (Exception _) { }
    }
    private void processHealingItems(String map){
        InputStream input;
        Scanner reader;
        try{
            input = Objects.requireNonNull(getClass().getResourceAsStream(map));
            reader = new Scanner(input);
            while (reader.hasNext()) {
                point2D = new Point2D.Double();
                Vector<String> healingData = new Vector<>(List.of(reader.nextLine().split(";")));

                if(healingData.size() == 2){
                    point2D.setLocation(Math.floor(Math.random()*mapSize[0]),Math.floor(Math.random()*mapSize[1]));
                } else {
                    point2D.setLocation(Double.parseDouble(healingData.get(0)),Double.parseDouble(healingData.get(1)));
                }
                healingItemsP.put(point2D,new HealingItem(healingData.get(healingData.size()/2), Integer.parseInt(healingData.get(1+healingData.size()/2))));
                itemList.put(healingData.get(2), "healingItem");
            }
        } catch (Exception _) { }
    }
    public boolean itemsOnTile(final int[] location){
        point2D = new Point2D.Double(location[0],location[1]);
        if(healingItemsP.containsKey(point2D)){return true;}
        else if(weaponsP.containsKey(point2D)){return true;}
        else return armorP.containsKey(point2D);
    }

    public Messenger grabItems(final int[] location, final String item) {
        Messenger messenger = new Messenger();
        point2D = new Point2D.Double(location[0],location[1]);
        return switch (itemList.get(item)) {
            case "weapon" -> {
                if (Objects.equals(weaponsP.get(point2D).name(), item)) {
                    messenger.setWeapon(weaponsP.get(point2D));
                    weaponsP.remove(point2D);
                } else {
                    messenger.setMessage("Invalid item");
                }
                yield messenger;
            }
            case "armor" -> {
                if (Objects.equals(armorP.get(point2D).name(), item)) {
                    messenger.setArmor(armorP.get(point2D));
                    armorP.remove(point2D);
                } else {
                    messenger.setMessage("Invalid item");
                }
                yield messenger;
            }
            case "healingItem" -> {
                if (Objects.equals(healingItemsP.get(point2D).getName(), item)) {
                    messenger.setHealingItem(healingItemsP.get(point2D));
                    healingItemsP.remove(point2D);
                } else {
                    messenger.setMessage("Invalid item");
                }
                yield messenger;
            }
            default -> null;
        };
    }
    public void resetMap(){
        healingItemsP.clear();
        weaponsP.clear();
        armorP.clear();
        processItems(itemFile);
    }


    public Weapon weaponsOnTile(final int[] location){
        point2D = new Point2D.Double(location[0],location[1]);
        if (weaponsP.containsKey(point2D)) {
            return weaponsP.get(point2D);
        }
        return null;
    }
    public Armor armorOnTile(int[] location) {
        point2D = new Point2D.Double(location[0],location[1]);
        if (armorP.containsKey(point2D)) {
            return armorP.get(point2D);
        }
        return null;
    }
    public HealingItem healingItemsOnTile(int[] location) {
        point2D = new Point2D.Double(location[0],location[1]);
        if (healingItemsP.containsKey(point2D)) {
            return healingItemsP.get(point2D);
        }
        return null;
    }
}
