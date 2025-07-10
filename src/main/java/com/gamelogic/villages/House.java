package com.gamelogic.villages;

import com.gamelogic.core.MapRegistry;
import com.gamelogic.map.Coordinates;
import com.gamelogic.map.IExitCoordinates;
import com.gamelogic.map.mapLogic.HouseData;
import com.gamelogic.map.mapLogic.MapController;
import com.gamelogic.map.mapLogic.MapData;
import com.gamelogic.map.mapLogic.MapType;
import com.savesystem.MapState;


public class House extends MapController implements IExitCoordinates {
    int ID;
    int houseNumber;
    int level;
    final Coordinates mainCoordinates;
    final Coordinates exitCoordinates;
    MapData mapData;
    MapType mapType;

    House(Coordinates mainCoordinates, Coordinates exitCoordinates, String fileMap, int ID) {
        this.mainCoordinates = mainCoordinates;
        this.exitCoordinates = exitCoordinates;
        this.ID = ID;
        this.level = super.level;
        mapType = MapType.HOUSE;
        super.mapType = this.mapType;
        mapData = new HouseData(mapType);
        mapData.processMap(fileMap);
        MapRegistry.addMap(this, ID);
    }
    @Override
    public Coordinates getExitCoordinates() {
            return exitCoordinates;
    }

    @Override
    public Coordinates getMapCoordinates() {
        return mainCoordinates;
    }
    @Override
    public Coordinates generateValidStartPosition() {
        return null;
    }
    @Override
    public boolean usesFog() {
        return false;
    }
    @Override
    public MapState getMapState(){
        MapState mapState = new MapState();
        mapState.ID = this.ID;
        mapState.type = mapType.toString();
        return mapState;
    }
    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void resetMap() {

    }
    @Override
    public void loadData(MapState mapState) {
    }
    @Override
    public boolean areItemsOnTile(Coordinates location) {
        return false;
    }
    @Override
    public int getID() {
        return ID;
    }
    @Override
    public Coordinates getCoordinates() {
        return this.mapData.getLevel(level).map().getColumnsAndRows();
    }

    @Override
    public String getMapValue(Coordinates coordinates) {
        return mapData.getLevel(level).map().getMapValue(coordinates);
    }
    @Override
    public String getTheme() {
        return mapData.getLevel(level).theme();
    }
    @Override
    public void changeLevel(int levelDelta) {
        if ((levelDelta == -1 && level > 0) || (levelDelta == 1 && level < mapData.getTotalLevels())) {
            level += levelDelta;
        }
    }
    public NPC getNPC(Coordinates location, String villageName) {
        return null;
    }

}
