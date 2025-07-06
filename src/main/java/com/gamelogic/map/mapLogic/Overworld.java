package com.gamelogic.map.mapLogic;

import com.armor.Armor;
import com.gamelogic.map.IMonsters;
import com.gamelogic.core.MapRegistry;
import com.gamelogic.core.TileKeyRegistry;
import com.gamelogic.gameflow.ValidStart;
import com.gamelogic.inventory.IAccessItems;
import com.gamelogic.map.Coordinates;
import com.gamelogic.map.IMapState;
import com.gamelogic.messaging.Messenger;
import com.gamelogic.rawdataclasses.RMap;
import com.gamelogic.villages.House;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.recoveryitems.RecoveryItem;
import com.savesystem.MapState;
import com.weapons.Weapon;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class Overworld extends MapController implements IDoesDamage, IMapState, IAccessItems, IMonsters {

    protected final MapData mapData;
    private final ValidStart validStart;
    private int level = 0;
    protected int ID;
    protected MapType mapType;
    private final Random random = new Random();

    //Constructors/Map Generation:
    public Overworld(final String filePath, MapType type, int ID) {
        mapType = type;
        this.ID = ID;
        validStart = new ValidStart(this, this, this);
        mapData = new OverworldMapData(type);
        try {
            Gson gson = new Gson();
            InputStream input = Objects.requireNonNull(getClass().getResourceAsStream(filePath));
            InputStreamReader reader = new InputStreamReader(input);
            Type listType = new TypeToken<List<RMap>>() {
            }.getType();
            List<RMap> tempMapList = gson.fromJson(reader, listType);
            for (RMap rMap : tempMapList) {

                mapData.processMap(rMap.level(), getStringMap(rMap.file()), rMap.theme(), rMap.voice(), rMap.sound());
            }
            MapRegistry.addMap(this, 0);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            mapData.defaultLevel();
        }
    }

    public Coordinates generateValidStartPosition() {
        return validStart.validStartingCoordinates(TileKeyRegistry.getTileKeyList());
    }

    public boolean usesFog() {
        return mapType.hasFog();
    }

    protected Map<String, String> getStringMap(String filePath) {
        return super.getStringMap(filePath);
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
    public List<Messenger> attackAllMonsters(int attack, Coordinates location) {
        double damage = attack*0.85;
        return mapData.getLevel(level).monster().attackAllMonsters(damage,location);
    }

    public Messenger getMonstersAttack(Coordinates location) {
        return mapData.getLevel(level).monster().getMonsterAttack(location);
    }

    //IAccessItems
    public boolean areItemsOnTile(Coordinates location) {
        return mapData.getLevel(level).item().itemsOnTile(location);
    }
    @Override
    public String getItemName(Coordinates location) {
        return mapData.getLevel(level).item().itemOnTile(location);
    }
    public StringBuilder itemList(Coordinates location) {
        if (mapData.getLevel(level) == null) {
            return new StringBuilder();
        }
        if(!mapType.hasItems()){
            return new StringBuilder();
        }
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

    public List<String> getMonsters(Coordinates location) {
        return mapData.getLevel(level).monster().getMonsters(location);
    }

    public boolean isMonsterOnTile(Coordinates location) {
        return (mapType.hasMonsters() && !mapData.getLevel(level).monster().getMonsters(location).isEmpty());
    }

    @Override
    public List<String> getMonsterNames(Coordinates mapCoordinates) {
        return mapData.getLevel(level).monster().getMonsterNames(mapCoordinates);
    }

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
        return mapData.getLevel(level).villages().checkVillage(location);
    }

    public int getHouseNumber(Coordinates coordinates, String string) {
        return mapData.getLevel(level).villages().checkHouse(coordinates, string);
    }

    public House getHouse(int houseNum, String village) {
        return mapData.getLevel(level).villages().getHouseMap(houseNum, village);
    }

    public MapState getMapState() {
        MapState mapState = new MapState();
        mapState.ID = this.ID;
        mapState.type = this.mapType.toString();
        for(int i =0; i< mapData.getTotalLevels(); i++){
            LevelData levelData = this.mapData.getLevel(i);
            mapState.recoveryItemsList.put(i,levelData.item().getItems());
            mapState.weaponList.put(i,levelData.item().getWeapons());
            mapState.armorList.put(i,levelData.item().getArmor());
            mapState.monsterList.put(i, levelData.monster().getMonsterState());
        }
        return mapState;
    }

    public int getID() {
        return ID;
    }
    public boolean progressesGame(){
        return mapType.progressesGame();
    }

    public void loadData(MapState mapState) {
        for(int i = 0; i < mapData.getTotalLevels(); i++){
            LevelData levelData = mapData.getLevel(i);
            levelData.item().clearItemList();
            levelData.item().loadItems(mapState.recoveryItemsList.get(i));
            levelData.monster().loadMonsters(mapState.monsterList.get(i));
            levelData.item().loadArmor(mapState.armorList.get(i));
            levelData.item().loadWeapons(mapState.weaponList.get(i));
        }
    }

}
