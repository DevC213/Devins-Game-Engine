package com.adventure_logic.MapLogic;

import com.adventure_logic.GuiEventListener;

import java.io.File;
import java.io.InputStream;
import java.util.Objects;
import java.util.Scanner;
import java.util.Vector;

public class MapController {

    private final Vector<MapGeneration> maps = new Vector<>();
    private final Vector<MapItemController> items = new Vector<>();
    private final  Vector<MapMonsterController> monsters = new Vector<>();
    private final MapMovementController mapMovementController;
    private final GuiEventListener guiEventListener;
    private int level = 0;


    //Constructors/Map Generation:
    public MapController(final String mapName, final GuiEventListener guiEventListener) {

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
    private void processMaps(String File) throws Exception {
        int fileLine = 0;
        InputStream input = Objects.requireNonNull(getClass().getResourceAsStream(File));
        Scanner reader = new Scanner(input);
        while (reader.hasNext()) {
            switch (fileLine) {
                case 0 -> maps.add(new MapGeneration(reader.nextLine()));
                case 1 -> items.add(new MapItemController(reader.nextLine()));
                case 2 -> monsters.add(new MapMonsterController(reader.nextLine(),guiEventListener));
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
    public Vector<String> getItems(final int[] location) {return items.get(level).getItems(location);}
    public int[] getCords(){return maps.get(level).getColumnsAndRows();}
    public Vector<String> getMonsters(final int[] location){return monsters.get(level).getMonsters(location);}

    public void addItem(final int[] location, final String item) {
        items.get(level).addItem(location,item);
    }
    public String grabItem(final int[] location, final String item) {
        return items.get(level).grabItem(location,item);
    }


    public void attackMonster(String monster, int attack, final int[] location) {monsters.get(level).attackMonster(monster, attack ,location);}
    public Vector<Double> getMonstersAttacks(final int[] location) {
        return monsters.get(level).getMonsterAttacks(location);
    }
}
