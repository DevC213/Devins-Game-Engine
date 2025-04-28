package com.adventure_logic.MapLogic;

import com.Armor.Armor;
import com.Weapons.Weapon;
import com.adventure_logic.Messenger;
import com.recoveryItems.HealingItem;

import java.io.InputStream;
import java.util.*;

class MapItemController {


    private final Map<String, Vector<String>> itemLocation = new HashMap<>();
    private final Map<String, Vector<String>> dItemLocation = new HashMap<>();

    private final Map<String, HealingItem> healingItems = new HashMap<>();
    private final Map<String,String> itemList = new HashMap<>();
    private final Map<String, Weapon> weapons = new HashMap<>();
    private final Map<String, Armor> armor = new HashMap<>();

    public MapItemController(String map){
        processItems(map);
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
        for(String j: itemLocation.keySet()){
            Vector<String> temp = new Vector<>(itemLocation.get(j));
            dItemLocation.put(j, temp);
        }
    }
    private void processWeapons(String map){
        InputStream input;
        Scanner reader;
        try{
            input = Objects.requireNonNull(getClass().getResourceAsStream(map));
            reader = new Scanner(input);
            while (reader.hasNext()) {
                Vector<String> weaponData = new Vector<>(List.of(reader.nextLine().split(";")));
                weapons.put(weaponData.get(0)+"."+weaponData.get(1),new Weapon(weaponData.get(2), Integer.parseInt(weaponData.get(3))));
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
                Vector<String> armorData = new Vector<>(List.of(reader.nextLine().split(";")));
                armor.put(armorData.get(0)+"."+armorData.get(1), new Armor(armorData.get(2), Integer.parseInt(armorData.get(3))));
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
                Vector<String> healingData = new Vector<>(List.of(reader.nextLine().split(";")));
                healingItems.put(healingData.get(0)+"."+healingData.get(1),new HealingItem(healingData.get(2), Integer.parseInt(healingData.get(3))));
                itemList.put(healingData.get(2), "healingItem");
            }
        } catch (Exception _) { }
    }
    public boolean itemsOnTile(final int[] location){
        if(healingItems.containsKey(location[0]+"."+location[1])){return true;}
        else if(weapons.containsKey(location[0]+"."+location[1])){return true;}
        else return armor.containsKey(location[0] + "." + location[1]);
    }

    public Messenger grabItems(final int[] location, final String item) {
        Messenger messenger = new Messenger();
        return switch (itemList.get(item)) {
            case "weapon" -> {
                if (Objects.equals(weapons.get(location[0] + "." + location[1]).name(), item)) {
                    messenger.setWeapon(weapons.get(location[0] + "." + location[1]));
                    weapons.remove(location[0] + "." + location[1]);
                } else {
                    messenger.setMessage("Invalid item");
                }
                yield messenger;
            }
            case "armor" -> {
                if (Objects.equals(armor.get(location[0] + "." + location[1]).name(), item)) {
                    messenger.setArmor(armor.get(location[0] + "." + location[1]));
                    armor.remove(location[0] + "." + location[1]);
                } else {
                    messenger.setMessage("Invalid item");
                }
                yield messenger;
            }
            case "healingItem" -> {
                if (Objects.equals(healingItems.get(location[0] + "." + location[1]).getName(), item)) {
                    messenger.setHealingItem(healingItems.get(location[0] + "." + location[1]));
                    healingItems.remove(location[0] + "." + location[1]);
                } else {
                    messenger.setMessage("Invalid item");
                }
                yield messenger;
            }
            default -> null;
        };
    }
    public void resetMap(){
        itemLocation.clear();
        for(String j: dItemLocation.keySet()){
            Vector<String> temp = new Vector<>(dItemLocation.get(j));
            itemLocation.put(j, temp);
        }
    }


    public Weapon weaponsOnTile(final int[] location){
        if (weapons.containsKey(location[0] + "." + location[1])) {
            return weapons.get(location[0] + "." + location[1]);
        }
        return null;
    }
    public Armor armorOnTile(int[] location) {
        if (armor.containsKey(location[0] + "." + location[1])) {
            return armor.get(location[0] + "." + location[1]);
        }
        return null;
    }
    public HealingItem healingItemsOnTile(int[] location) {
        if (healingItems.containsKey(location[0] + "." + location[1])) {
            return healingItems.get(location[0] + "." + location[1]);
        }
        return null;
    }
}
