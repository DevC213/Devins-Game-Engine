package com.adventure_logic.MapLogic;

import com.Armor.Armor;
import com.Weapons.Weapon;
import com.adventure_logic.IGuiEventListener;
import com.adventure_logic.Messenger;
import com.recoveryItems.HealingItem;

import java.io.InputStream;
import java.util.Objects;
import java.util.Scanner;
import java.util.Vector;

public class MapController implements ICanCross, IDoesDamage, IVisibility {

    private final Vector<MapGeneration> maps = new Vector<>();
    private final Vector<MapItemController> items = new Vector<>();
    private final  Vector<MapMonsterController> monsters = new Vector<>();
    private final MapMovementController mapMovementController;
    private final IGuiEventListener guiEventListener;
    private int level = 0;

    //Constructors/Map Generation:
    public MapController(final String mapName, final IGuiEventListener guiEventListener) {

        InputStream input;
        int fileLine = 0;
        mapMovementController = new MapMovementController(this);
        this.guiEventListener = guiEventListener;
        try {
            input = Objects.requireNonNull(getClass().getResourceAsStream(mapName));
            Scanner reader = new Scanner(input);
            while (reader.hasNext()) {
                String line = reader.nextLine();
                if(fileLine == 0){
                    MapGeneration.processKey(Objects.requireNonNull(getClass().getResourceAsStream(line.trim())));
                } else{
                    processMaps(line);
                }
                fileLine ++;
            }
        } catch (Exception e){
            this.guiEventListener.UIUpdate("Error Reading Map info, loading default map",0);
            maps.add(new MapGeneration());
        }

    }
    private void processMaps(String File){
        int fileLine = 0;
        InputStream input = Objects.requireNonNull(getClass().getResourceAsStream(File));
        Scanner reader = new Scanner(input);
        while (reader.hasNext()) {
            switch (fileLine) {
                case 0 -> maps.add(new MapGeneration(reader.nextLine()));
                case 1 -> items.add(new MapItemController(reader.nextLine()));
                case 2 -> {
                    monsters.add(new MapMonsterController(maps.getFirst().getColumnsAndRows()));
                    Messenger messenger = monsters.get(level).processFiles(reader.nextLine());
                    if(messenger.getMessage() != null){
                        guiEventListener.UIUpdate(messenger.getMessage(),0);
                    }
                }
                case 3 -> {
                    Messenger messenger = monsters.get(level).processSpawnChances(reader.nextLine());
                    if(messenger.getMessage() != null){
                        guiEventListener.UIUpdate(messenger.getMessage(),0);
                    }
                }
            }
            fileLine++;

        }
    }
    public void change_level(int dir){
        if((dir == 1 && level >0) || (dir == -1 && level < maps.size()-1)){
            level-= dir;
        }
    }
    public void setLevel(int level) {this.level = level;}
    //Reset Map info:
    public void resetMap(){
        for(int i = 0; i < maps.size(); i++){
            items.get(i).resetMap();
            monsters.get(i).resetMonsters();
        }
    }


    //Facade functions:
    public String getMapValue(final int c, final int r) {return maps.get(level).getMapValue(c,r);}

    @Override
    public int effect(final String terrain){return mapMovementController.getDamage(terrain);}
    @Override
    public boolean getMovement(final String terrain, final int command){
        return mapMovementController.getCanCross(terrain, command);
    }
    @Override
    public int getVisibility(final String terrain){
        return mapMovementController.getVisibility(terrain);
    }
    public boolean isLadder(final String terrain){
        return mapMovementController.isLadder(terrain);
    }
    public boolean isCave(final String terrain){
        return mapMovementController.isCave(terrain);
    }
    public Vector<Vector<String>> getKey(){
        return MapGeneration.getKey();
    }
    public String getImage(final String terrain) {
        return maps.getFirst().getImage(terrain);}
    public boolean getItems(final int[] location) {return items.get(level).itemsOnTile(location);}
    public StringBuilder itemList(final int[] location){
        Weapon weapons = getWeapons(location);
        Armor armor = getArmor(location);
        HealingItem healingItems = getHealing(location);
        StringBuilder str = new StringBuilder();
        if(weapons != null) {
            str.append(weapons.name());
        }
        if(armor != null) {
            str.append(armor.name());
        }
        if(healingItems != null) {
            str.append(healingItems.getName());
        }
        return str;
    }
    public Weapon getWeapons(final int[] location) {return items.get(level).weaponsOnTile(location);}
    public int[] getCords(){return maps.get(level).getColumnsAndRows();}
    public Vector<String> getMonsters(final int[] location){return monsters.get(level).getMonsters(location);}
    public Messenger grabItem(final int[] location, final String item) {
        return items.get(level).grabItems(location,item);
    }
    public Messenger attackMonsters(String monster, int attack, final int[] location) {return monsters.get(level).attackMonsters(monster, attack ,location);}
    public Messenger getMonstersAttack(final int[] location) {
        return monsters.get(level).getMonsterAttack(location);
    }
    public Messenger spawnMonsters(int[] location, int moves){
        int MOVES_BEFORE_SPAWN = 15000000;
        int random = (int) Math.floor(Math.random() * 20);
        Messenger messenger = new Messenger();
        if (random > 15 & moves >= MOVES_BEFORE_SPAWN) {
            monsters.get(level).spawnMonster(location);
            messenger.setMessage("Monster Spawned");
        }
        return messenger;
    }
    public Armor getArmor(final int[] location) {return items.get(level).armorOnTile(location);}

    public HealingItem getHealing(int[] cords) {return items.get(level).healingItemsOnTile(cords);}
    public boolean isMonsterOnTile(int[] cords) { return (monsters.get(level).getMonsters(cords) != null);
    }
    public int getLevel(){
        return level;
    }
}
