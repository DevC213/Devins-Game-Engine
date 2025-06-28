package com.gamelogic.villages;

import com.armor.Armor;
import com.gamelogic.map.Coordinates;
import com.gamelogic.map.IExitCoordinates;
import com.gamelogic.map.mapLogic.LevelData;
import com.gamelogic.map.mapLogic.MapController;
import com.gamelogic.map.mapLogic.MapType;
import com.gamelogic.messaging.Messenger;
import com.recoveryitems.RecoveryItem;
import com.savesystem.MapState;
import com.weapons.Weapon;

import java.util.*;

public class House extends MapController implements IExitCoordinates {
    int houseNumber;
    final Coordinates mainCoordinates;
    final Coordinates exitCoordinates;

    House(int houseNumber, Coordinates mainCoordinates, Coordinates exitCoordinates, String fileMap, int ID) {
        super(fileMap, MapType.HOUSE, ID);
        this.houseNumber = houseNumber;
        this.mainCoordinates = mainCoordinates;
        this.exitCoordinates = exitCoordinates;
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
    public boolean isMonsterOnTile(Coordinates location) {
        return false;
    }
    @Override
    public Weapon getWeapons(Coordinates location) {
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
    @Override
    public Messenger checkForVillages(Coordinates location) {
        return null;
    }
    @Override
    public boolean usesFog() {
        return false;
    }
    @Override
    public MapState getMapState(){
        MapState mapState = new MapState();
        mapState.ID = super.ID;
        mapState.type = super.mapType.toString();
        return mapState;
    }
    @Override
    public int getLevel() {
        return super.getLevel();
    }
    @Override
    public boolean progressesGame(){
        return false;
    }

    @Override
    public void loadData(MapState mapState) {
    }

    public boolean areItemsOnTile(Coordinates location) {
        return false;
    }
}
