package com.gameLogic.MapLogic;

import com.Armor.Armor;
import com.Weapons.Weapon;
import com.gameLogic.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.recoveryItems.RecoveryItem;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MapController implements ICanCross, IDoesDamage, IVisibility, IMapState, IImage, IAccessItems, IMonsters {


    private final MapData mapData;
    private final IGuiEventListener guiEventListener;
    private final ValidStart validStart;
    private int level = 0;
    private final Random random = new Random();

    //Constructors/Map Generation:
    public MapController(final String filePath, final IGuiEventListener guiEventListener) {

        this.guiEventListener = guiEventListener;
        validStart = new ValidStart(this, this, this, this);
        mapData = new MapData();
        int levelBeingProcessed = 0;
        try {
            Map<String, String> levelMap = getStringMap(filePath);
            for (Map.Entry<String, String> entry : levelMap.entrySet()) {
                String levelName = entry.getKey();
                String fileLocation = entry.getValue();
                switch (levelName) {
                    case "Key":
                        MapGeneration.processKey(Objects.requireNonNull(getClass().getResourceAsStream(fileLocation.trim())));
                        break;
                    case "Overworld":
                    case "Underground":
                    case "Caverns":
                    case "TheDarkness":
                    case "TheVoid":
                        mapData.processMap(levelBeingProcessed, getStringMap(fileLocation));
                        levelBeingProcessed++;
                        break;
                    default:
                        System.out.println("Unknown level type: " + levelName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.guiEventListener.UIUpdate("Error Reading Map info, loading default map", 0);
            mapData.defaultMap(new MapGeneration());
        }

    }
    private Map<String, TileKey> getMapTileKey() {
        return MapGeneration.getTileKey();

    }
    public Coordinates generateValidStartPosition() {
        Map<String, TileKey> tileKey = getMapTileKey();
        Coordinates startingCords = validStart.validStartingCoordinents(tileKey);
        if (getVisibility(getMapValue(startingCords)) != 2) {
            guiEventListener.UIUpdate("Player: The air is thick here", 0);
        }
        return startingCords;
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
        return mapData.getLevel(level).map().getMapValue(coordinates.x(), coordinates.y());
    }

    //IDoesDamage
    @Override
    public int getHealthDelta(final String terrain) {
        return getMapTileKey().get(terrain).healthDelta();
    }
    public Messenger attackMonsters(String monster, int attack, Coordinates location) {
        return mapData.getLevel(level).monster().attackMonsters(monster, attack, location);
    }
    public Messenger getMonstersAttack(Coordinates location) {
        return mapData.getLevel(level).monster().getMonsterAttack(location);
    }

    //IVisibility
    @Override
    public int getVisibility(final String terrain) {
        return getMapTileKey().get(terrain).visibility();
    }

    //ICanCross
    @Override
    public boolean isWalkable(final String terrain) {
        return getMapTileKey().get(terrain).walkable();
    }
    @Override
    public boolean isLadder(final String terrain) {
        return getMapTileKey().get(terrain).name().equals("ladder");
    }
    @Override
    public boolean isCave(final String terrain) {
        return getMapTileKey().get(terrain).name().equals("cave");
    }

    //IImage
    public String getImage(final String terrain) {
        return mapData.getLevel(level).map().getImage(terrain, level);
    }
    public String getPlayerImage(int direction) {
        return mapData.getLevel(level).map().getPlayerImage(direction);
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
}

