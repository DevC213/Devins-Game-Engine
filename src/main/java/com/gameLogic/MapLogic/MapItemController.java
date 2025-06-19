package com.gameLogic.MapLogic;

import com.Armor.Armor;
import com.Weapons.Weapon;
import com.gameLogic.Coordinates;
import com.gameLogic.Messenger;
import com.recoveryItems.HealingItem;

import java.awt.geom.Point2D;
import java.io.InputStream;
import java.util.*;

class MapItemController {


    private final Map<String,String> itemList = new HashMap<>();
    private final Map<Point2D, HealingItem> healingItems = new HashMap<>();
    private final Map<Point2D, Weapon> weapons = new HashMap<>();
    private final Map<Point2D, Armor> armor = new HashMap<>();
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
                weapons.put(point2D,new Weapon(weaponData.get(2), Integer.parseInt(weaponData.get(3))));
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
                armor.put(point2D,new Armor(armorData.get(2), Integer.parseInt(armorData.get(3))));
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
                    point2D.setLocation(Math.floor(Math.random()* maxCords.getX()),Math.floor(Math.random()*maxCords.getY()));
                } else {
                    point2D.setLocation(Double.parseDouble(healingData.get(0)),Double.parseDouble(healingData.get(1)));
                }
                healingItems.put(point2D,new HealingItem(healingData.get(healingData.size()/2), Integer.parseInt(healingData.get(1+healingData.size()/2))));
                itemList.put(healingData.get(2), "healingItem");
            }
        } catch (Exception _) { }
    }
    public boolean itemsOnTile(Coordinates location){
        point2D = new Point2D.Double(location.x(),location.y());
        if(healingItems.containsKey(point2D)){return true;}
        else if(weapons.containsKey(point2D)){return true;}
        else return armor.containsKey(point2D);
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
                if (Objects.equals(armor.get(point2D).name(), item)) {
                    messenger.setArmor(armor.get(point2D));
                    armor.remove(point2D);
                } else {
                    messenger.setMessage("Invalid item");
                }
                yield messenger;
            }
            case "healingItem" -> {
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
        armor.clear();
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
        if (armor.containsKey(point2D)) {
            return armor.get(point2D);
        }
        return null;
    }
    public HealingItem healingItemsOnTile(Coordinates location) {
        point2D = new Point2D.Double(location.x(),location.y());
        if (healingItems.containsKey(point2D)) {
            return healingItems.get(point2D);
        }
        return null;
    }
}
