package com.gamelogic.map.mapLogic;

import com.armor.Armor;
import com.gamelogic.core.TileKeyRegistry;
import com.gamelogic.villages.House;
import com.weapons.Weapon;
import com.gamelogic.combat.IMonsters;
import com.gamelogic.commands.IGuiEventListener;
import com.gamelogic.gameflow.ValidStart;
import com.gamelogic.inventory.IAccessItems;
import com.gamelogic.map.Coordinates;
import com.gamelogic.map.IMapState;
import com.gamelogic.messaging.Messenger;
import com.gamelogic.rawdataclasses.RMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.recoveryitems.RecoveryItem;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MapController implements IDoesDamage, IMapState, IAccessItems, IMonsters {

    private final MapData mapData;
    private final ValidStart validStart;
    private int level = 0;
    private final Random random = new Random();

    //Constructors/Map Generation:
    public MapController(final String filePath, final IGuiEventListener guiEventListener) {

        validStart = new ValidStart(this, this, this);
        mapData = new MapData();
        try {
            Gson gson = new Gson();
            InputStream input = Objects.requireNonNull(getClass().getResourceAsStream(filePath));
            InputStreamReader reader = new InputStreamReader(input);
            Type listType = new TypeToken<List<RMap>>() {}.getType();
            List<RMap> tempMapList = gson.fromJson(reader, listType);
            for(RMap rMap : tempMapList) {
                mapData.processMap(rMap.level(), getStringMap(rMap.file()), rMap.theme(),  rMap.voice(), rMap.sound());
            }
        } catch (Exception e) {
            guiEventListener.UIUpdate(e.getMessage(),0);
            mapData.defaultLevel();
        }

    }
    public Coordinates generateValidStartPosition() {
        return validStart.validStartingCoordinates(TileKeyRegistry.getTileKeyList());
    }

    private Map<String, String> getStringMap(String filePath) {
        InputStream input = Objects.requireNonNull(getClass().getResourceAsStream(filePath));
        InputStreamReader isr = new InputStreamReader(input, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(isr);
        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<String, String>>() {}.getType();
        return gson.fromJson(reader, mapType);
    }

    //IMapState
    public void changeLevel(int levelDelta) {
        if ((levelDelta == -1 && level > 0) || (levelDelta == 1 && level < mapData.getTotalLevels())) {
            level += levelDelta;
        }
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public int getLevel() {
        return level;
    }
    public void resetMap() {
        for (int i = 0; i < mapData.getTotalLevels(); i++) {
            mapData.getLevel(i).resetGame();
        }
    }
    public String getMapValue(Coordinates coordinates) {
        return mapData.getLevel(level).map().getMapValue(coordinates);
    }

    //IDoesDamage
    public Messenger attackMonsters(String monster, int attack, Coordinates location) {
        return mapData.getLevel(level).monster().attackMonsters(monster, attack, location);
    }
    public Messenger getMonstersAttack(Coordinates location) {
        return mapData.getLevel(level).monster().getMonsterAttack(location);
    }

    //IAccessItems
    public boolean itemsOnTile(Coordinates location) {
        return mapData.getLevel(level).item().itemsOnTile(location);
    }
    public StringBuilder itemList(Coordinates location) {
        Weapon weapons = getWeapons(location);
        Armor armor = getArmor(location);
        RecoveryItem recoveryItems = getHealing(location);
        StringBuilder str = new StringBuilder();
        if (weapons != null) {
            str.append(weapons.name());
        }
        if (armor != null) {
            str.append(armor.name());
        }
        if (recoveryItems != null) {
            str.append(recoveryItems.getName());
        }
        return str;
    }
    public Weapon getWeapons(Coordinates location) {
        return mapData.getLevel(level).item().weaponsOnTile(location);
    }
    public Coordinates getCoordinates() {
        return mapData.getLevel(level).map().getColumnsAndRows();
    }
    public Messenger grabItem(Coordinates location, final String item) {
        return mapData.getLevel(level).item().grabItems(location, item);
    }
    public Armor getArmor(Coordinates location) {
        return mapData.getLevel(level).item().armorOnTile(location);
    }
    public RecoveryItem getHealing(Coordinates location) {
        return mapData.getLevel(level).item().healingItemsOnTile(location);
    }

    //IMonsters
    public Messenger spawnMonsters(Coordinates location, int moves) {
        Messenger messenger = new Messenger();
        int RANDOM_RANGE = 20;
        int SPAWN_THRESHOLD = Integer.MAX_VALUE - 1; /* <-- This huge value is so I can test everything else without monsters spawning. Will be more resonable once I want to test with spawning */
        if (random.nextInt(RANDOM_RANGE) > 15 && moves >= SPAWN_THRESHOLD) {
            mapData.getLevel(level).monster().spawnMonster(location);
            messenger.setMessage("Monster Spawned");
        }
        return messenger;
    }
    public  List<String> getMonsters(Coordinates location) {
        return mapData.getLevel(level).monster().getMonsters(location);
    }
    public boolean isMonsterOnTile(Coordinates location) {
        return (mapData.getLevel(level).monster().getMonsters(location) != null);
    }
    public String getTheme(){
        return mapData.getLevel(level).theme();
    }

    //ISound
    public String getVoice(){
        return mapData.getLevel(level).voice();
    }
    public String getSound(){
        return mapData.getLevel(level).sound();
    }

    public Messenger checkForVillages(Coordinates location) {
        return mapData.getLevel(level).villages().checkVillage(location);
    }
    public int getHouseNumber(Coordinates coordinates, String string){
        return mapData.getLevel(level).villages().checkHouse(coordinates, string);
    }
    public House getHouse(int houseNum, String village){
        return mapData.getLevel(level).villages().getHouseMap(houseNum,village);
    }

}

