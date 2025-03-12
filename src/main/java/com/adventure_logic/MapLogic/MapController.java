package com.adventure_logic.MapLogic;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;
import java.util.Vector;

public class MapController {

    private final Vector<MapGeneration> maps = new Vector<>();
    private final Vector<MapItemController> items = new Vector<>();
    private final  Vector<MapMonsterController> monsters = new Vector<>();
    private final MapMovementController mapMovementController;
    private int level = 0;


    //Constructors/Map Generation:
    public MapController(final String mapName) {

        File myfile;
        Scanner reader;
        int fileLine = 0;
        mapMovementController = new MapMovementController(this);
        Vector<String> files = new Vector<>();
        try {
            myfile = new File(mapName);
            reader = new Scanner(myfile);
            while (reader.hasNext()) {
                if(fileLine == 0){
                    MapGeneration.processKey(reader.nextLine());
                } else{
                    processMaps(reader.nextLine());
                }
                fileLine ++;
            }
        } catch (FileNotFoundException e){
            System.out.println("Error Reading Map info, loading default map");
            maps.add(new MapGeneration());
        }
    }
    private void processMaps(String File) throws FileNotFoundException {
        File myfile;
        Scanner reader;
        int fileLine = 0;
        myfile = new File(File);
        reader = new Scanner(myfile);
        while (reader.hasNext()) {
            switch (fileLine) {
                case 0 -> maps.add(new MapGeneration(reader.nextLine()));
                case 1 -> items.add(new MapItemController(reader.nextLine()));
                case 2 -> monsters.add(new MapMonsterController(reader.nextLine()));
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
    public int Damage(final String terrain){return mapMovementController.Damage(terrain);}
    public boolean getMovementOrDamage(final String terrain, final int command){
        return mapMovementController.getMovementOrDamage(terrain, command);
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
    public String getImage(final String terrain) {return maps.get(0).getImage(terrain);}
    public Vector<String> getItems(final int[] location) {return items.get(level).getItems(location);}
    public Vector<String> getMonsters(final int[] location){return monsters.get(level).getMonsters(location);}
    public void addItem(final int[] location, final String item) {
        items.get(level).addItem(location,item);
    }
    public String grabItem(final int[] location, final String item) {
        return items.get(level).grabItem(location,item);
    }
    public int[] getCords(){return maps.get(level).getColumnsAndRows();}
    public void attackMonster(String monster, int attack, final int[] location) {monsters.get(level).attackMonster(monster, attack ,location);}

    public Vector<Double> getMonstersAttacks(final int[] location) {
        return monsters.get(level).getMonsterAttacks(location);
    }


}
