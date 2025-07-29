package com.gamelogic.map.mapLogic;

import com.armor.Armor;
import com.gamelogic.villages.House;
import com.gamelogic.villages.NPC;
import com.savesystem.MapState;
import com.weapons.Weapon;
import com.gamelogic.map.IMonsters;
import com.gamelogic.inventory.IAccessItems;
import com.gamelogic.map.Coordinates;
import com.gamelogic.map.IMapState;
import com.gamelogic.messaging.Messenger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.recoveryitems.RecoveryItem;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

public abstract class MapController implements IDoesDamage, IMapState, IAccessItems, IMonsters  {

    protected MapData mapData;

    protected int level = 0;
    protected int ID;
    protected MapType mapType;

    //Constructors/Map Generation:
    protected MapController() {}

    public abstract Coordinates generateValidStartPosition();

    public boolean usesFog() {
        return mapType.hasFog();
    }

     protected Map<String, String> getStringMap(String filePath){
        InputStream input = Objects.requireNonNull(getClass().getResourceAsStream(filePath));
         InputStreamReader isr = new InputStreamReader(input, StandardCharsets.UTF_8);
         BufferedReader reader = new BufferedReader(isr);
         Gson gson = new Gson();
         Type mapType = new TypeToken<Map<String, String>>() {
         }.getType();
         return gson.fromJson(reader, mapType);}
    //IMapStat

    public String getTheme() {
        return mapData.getLevel(level).theme();
    }

    //ISound
    public String getVoice() {
        return mapData.getLevel(level).voice();
    }

    public String getSound() {
        return mapData.getLevel(level).sound();
    }

    public Messenger checkForVillages(Coordinates location) {

        if(!mapType.hasVillages()) {
            return new Messenger();
        }
        return mapData.getLevel(level).villages().checkVillage(location);
    }
    public House getHouse(Coordinates coordinates, String village) {
        return mapData.getLevel(level).villages().getHouseMap(coordinates, village);
    }

    public abstract MapState getMapState();

    public int getID() {
        return ID;
    }
    public boolean progressesGame(){
        return mapType.progressesGame();
    }
    public NPC getNPC(Coordinates location, String villageName) {
        return mapData.getLevel(level).villages().checkNPCs(location, villageName);
    }

    public abstract void loadData(MapState mapState);
    @Override
    public void setLevel(int level) {
    }
    @Override
    public Messenger spawnMonsters(Coordinates location, int moves) {
        return new Messenger();
    }

    @Override
    public List<String> getMonsters(Coordinates location) {
        return List.of();
    }

    @Override
    public boolean isMonsterOnTile(Coordinates location) {
        return false;
    }

    @Override
    public List<String> getMonsterNames(Coordinates mapCoordinates) {
        return List.of();
    }

    @Override
    public Messenger attackMonsters(String monster, int attack, Coordinates location) {
        return null;
    }

    @Override
    public List<Messenger> attackAllMonsters(int attack, Coordinates location) {
        return List.of();
    }

    @Override
    public Messenger getMonstersAttack(Coordinates location) {
        return null;
    }
    public StringBuilder itemList(Coordinates location) {
        return new StringBuilder();
    }

    @Override
    public Weapon getWeapons(Coordinates location) {
        return null;
    }
    @Override
    public String getItemName(Coordinates location) {
        return "";
    }
    @Override
    public Messenger grabItem(Coordinates location, String item) {
        return null;
    }

    @Override
    public Armor getArmor(Coordinates location) {
        return null;
    }

    @Override
    public RecoveryItem getHealing(Coordinates location) {
        return null;
    }
    public boolean inVillage() {
        if(!mapType.hasVillages()) {
            return false;
        }
        return mapData.getLevel(level).villages().inVillage();
    }
}

