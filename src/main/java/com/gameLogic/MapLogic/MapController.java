package com.gameLogic.MapLogic;

import com.Armor.Armor;
import com.Weapons.Weapon;
import com.gameLogic.*;
import com.recoveryItems.HealingItem;

import java.io.InputStream;
import java.util.*;

public class MapController implements ICanCross, IDoesDamage, IVisibility, IMapState, IImage, IAccessItems, IMonsters {

    private final  List<MapGeneration> maps = new ArrayList<>();
    private final  List<MapItemController> items = new ArrayList<>();
    private final  List<MapMonsterController> monsters = new ArrayList<>();
    private final IGuiEventListener guiEventListener;
    private final ValidStart validStart;
    private int level = 0;
    private final Random random = new Random();

    //Constructors/Map Generation:
    public MapController(final String filePath, final IGuiEventListener guiEventListener) {

        InputStream input;
        int fileLine = 0;
        this.guiEventListener = guiEventListener;
        validStart = new ValidStart(this,this,this,this);
        try {
            input = Objects.requireNonNull(getClass().getResourceAsStream(filePath));
            Scanner reader = new Scanner(input);
            while (reader.hasNext()) {
                String line = reader.nextLine();
                if (fileLine == 0) {
                    MapGeneration.processKey(Objects.requireNonNull(getClass().getResourceAsStream(line.trim())));
                } else {
                    processMaps(line);
                }
                fileLine++;
            }
            reader.close();
        } catch (Exception e) {
            this.guiEventListener.UIUpdate("Error Reading Map info, loading default map", 0);
            maps.add(new MapGeneration());
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
    private void processMaps(String filePath) {
        int fileLine = 0;
        InputStream input = Objects.requireNonNull(getClass().getResourceAsStream(filePath));
        Scanner reader = new Scanner(input);
        String file;
        while (reader.hasNext()) {
            file = reader.nextLine();
            switch (fileLine) {
                case 0 -> maps.add(new MapGeneration(file));
                case 1 -> items.add(new MapItemController(file, maps.get(level).getColumnsAndRows()));
                case 2 -> {
                    monsters.add(new MapMonsterController(file));
                    Messenger messenger = monsters.get(level).processFiles(file);
                    if (messenger.getMessage() != null) {
                        guiEventListener.UIUpdate(messenger.getMessage(), 0);
                    }
                }
                case 3 -> {
                    Messenger messenger = monsters.get(level).processSpawnChances(file);
                    if (messenger.getMessage() != null) {
                        guiEventListener.UIUpdate(messenger.getMessage(), 0);
                    }
                }
            }

            fileLine++;

        }
        reader.close();
    }

    //IMapState
    public void changeLevel(int levelDelta) {
        if ((levelDelta == -1 && level > 0) || (levelDelta == 1 && level < maps.size() - 1)) {
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
        for (int i = 0; i < maps.size(); i++) {
            items.get(i).resetMap();
            monsters.get(i).resetMonsters();
        }
    }
    public String getMapValue(Coordinates coordinates) {
        return maps.get(level).getMapValue(coordinates.x(), coordinates.y());
    }

    //IDoesDamage
    @Override
    public int getHealthDelta(final String terrain) {
        return getMapTileKey().get(terrain).healthDelta();
    }
    public Messenger attackMonsters(String monster, int attack, Coordinates location) {
        return monsters.get(level).attackMonsters(monster, attack, location);
    }
    public Messenger getMonstersAttack(Coordinates location) {
        return monsters.get(level).getMonsterAttack(location);
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
        return maps.get(level).getImage(terrain, level);
    }
    public String getPlayerImage(int direction) {
        return maps.get(level).getPlayerImage(direction);
    }

    //IAccessItems
    public boolean itemsOnTile(Coordinates location) {
        return items.get(level).itemsOnTile(location);
    }
    public StringBuilder itemList(Coordinates location) {
        Weapon weapons = getWeapons(location);
        Armor armor = getArmor(location);
        HealingItem healingItems = getHealing(location);
        StringBuilder str = new StringBuilder();
        if (weapons != null) {
            str.append(weapons.name());
        }
        if (armor != null) {
            str.append(armor.name());
        }
        if (healingItems != null) {
            str.append(healingItems.getName());
        }
        return str;
    }
    public Weapon getWeapons(Coordinates location) {
        return items.get(level).weaponsOnTile(location);
    }
    public Coordinates getCoordinates() {
        return maps.get(level).getColumnsAndRows();
    }
    public Messenger grabItem(Coordinates location, final String item) {
        return items.get(level).grabItems(location, item);
    }
    public Armor getArmor(Coordinates location) {
        return items.get(level).armorOnTile(location);
    }
    public HealingItem getHealing(Coordinates location) {
        return items.get(level).healingItemsOnTile(location);
    }

    //IMonsters
    public Messenger spawnMonsters(Coordinates location, int moves) {
        Messenger messenger = new Messenger();
        int RANDOM_RANGE = 20;
        int SPAWN_THRESHOLD = Integer.MAX_VALUE - 1; /* <-- This huge value is so I can test everything else without monsters spawning. Will be more resonable once I want to test with spawning */
        if (random.nextInt(RANDOM_RANGE) > 15 && moves >= SPAWN_THRESHOLD) {
            monsters.get(level).spawnMonster(location);
            messenger.setMessage("Monster Spawned");
        }
        return messenger;
    }
    public  List<String> getMonsters(Coordinates location) {
        return monsters.get(level).getMonsters(location);
    }
    public boolean isMonsterOnTile(Coordinates location) {
        return (monsters.get(level).getMonsters(location) != null);
    }
}

