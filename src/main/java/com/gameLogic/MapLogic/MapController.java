package com.gameLogic.MapLogic;

import com.Armor.Armor;
import com.Weapons.Weapon;
import com.gameLogic.IGuiEventListener;
import com.gameLogic.Messenger;
import com.gameLogic.TileKey;
import com.recoveryItems.HealingItem;

import java.io.InputStream;
import java.util.*;

public class MapController implements ICanCross, IDoesDamage, IVisibility {

    private final Vector<MapGeneration> maps = new Vector<>();
    private final Vector<MapItemController> items = new Vector<>();
    private final Vector<MapMonsterController> monsters = new Vector<>();
    private final IGuiEventListener guiEventListener;
    private int level = 0;
    private final Random random = new Random();

    //Constructors/Map Generation:
    public MapController(final String filePath, final IGuiEventListener guiEventListener) {

        InputStream input;
        int fileLine = 0;
        this.guiEventListener = guiEventListener;
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
    public int[] generateValidStartPosition() {
        Map<String, TileKey> tileKey = getMapTileKey();
        int[] startingCords = {(int) Math.floor(Math.random() * getCoordinates()[1]), (int) Math.floor(Math.random() * getCoordinates()[0])};
        String tile = getMapValue(startingCords[0], startingCords[1]);
        TileKey key = tileKey.get(tile);
        int attempts = 0;
        while (isCave(tile) || isLadder(tile) || isMonsterOnTile(startingCords) || key.healthDelta() != 0 || !key.walkable()) {
            if(attempts > 8000){
                throw new RuntimeException("Error finding valid starting position, check overworld map");
            }
            startingCords = new int[]{(int) Math.floor(Math.random() * getCoordinates()[1]), (int) Math.floor(Math.random() * getCoordinates()[0])};
            tile = getMapValue(startingCords[0], startingCords[1]);
            key = tileKey.get(tile);
            attempts++;
        }
        if (getVisibility(getMapValue(startingCords[0], startingCords[1])) != 2) {
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
                    monsters.add(new MapMonsterController(maps.get(level).getColumnsAndRows(), file));
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
        if ((levelDelta == 1 && level > 0) || (levelDelta == -1 && level < maps.size() - 1)) {
            level -= levelDelta; /* this logic is intentional */
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
    public String getMapValue(final int c, final int r) {
        return maps.get(level).getMapValue(c, r);
    }

    //IDoesDamage
    @Override
    public int effect(final String terrain) {
        return getMapTileKey().get(terrain).healthDelta();
    }
    public Messenger attackMonsters(String monster, int attack, final int[] location) {
        return monsters.get(level).attackMonsters(monster, attack, location);
    }
    public Messenger getMonstersAttack(final int[] location) {
        return monsters.get(level).getMonsterAttack(location);
    }

    //IVisibility
    @Override
    public int getVisibility(final String terrain) {
        return getMapTileKey().get(terrain).visibility();
    }

    //ICanCross
    @Override
    public boolean getMovement(final String terrain) {
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
    public boolean getItems(final int[] location) {
        return items.get(level).itemsOnTile(location);
    }
    public StringBuilder itemList(final int[] location) {
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
    public Weapon getWeapons(final int[] location) {
        return items.get(level).weaponsOnTile(location);
    }
    public int[] getCoordinates() {
        return maps.get(level).getColumnsAndRows();
    }
    public Messenger grabItem(final int[] location, final String item) {
        return items.get(level).grabItems(location, item);
    }
    public Armor getArmor(final int[] location) {
        return items.get(level).armorOnTile(location);
    }
    public HealingItem getHealing(int[] location) {
        return items.get(level).healingItemsOnTile(location);
    }

    //IMonsters
    public Messenger spawnMonsters(int[] location, int moves) {
        Messenger messenger = new Messenger();
        int RANDOM_RANGE = 20;
        int SPAWN_THRESHOLD = Integer.MAX_VALUE - 1;
        if (random.nextInt(RANDOM_RANGE) > 15 && moves >= SPAWN_THRESHOLD) {
            monsters.get(level).spawnMonster(location);
            messenger.setMessage("Monster Spawned");
        }
        return messenger;
    }
    public Vector<String> getMonsters(final int[] location) {
        return monsters.get(level).getMonsters(location);
    }
    public boolean isMonsterOnTile(int[] location) {
        return (monsters.get(level).getMonsters(location) != null);
    }
}

