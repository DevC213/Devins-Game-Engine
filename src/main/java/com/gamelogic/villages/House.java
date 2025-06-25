package com.gamelogic.villages;

import com.armor.Armor;
import com.gamelogic.map.Coordinates;
import com.gamelogic.map.mapLogic.MapController;
import com.gamelogic.map.mapLogic.MapType;

import java.util.*;

public class House extends MapController {
    int houseNumber;
    final Coordinates mainCoordinates;
    final Coordinates exitCoordinates;

    House(int houseNumber, Coordinates mainCoordinates, Coordinates exitCoordinates, String fileMap) {
        super(fileMap, MapType.HOUSE);
        this.houseNumber = houseNumber;
        this.mainCoordinates = mainCoordinates;
        this.exitCoordinates = exitCoordinates;
    }

    public Coordinates getExitCoordinates() {
            return exitCoordinates;
    }
    public Coordinates getMainCoordinates() {
        return mainCoordinates;
    }
    public Coordinates getMaxCoordinates() {
        return super.getCoordinates();
    }
}
