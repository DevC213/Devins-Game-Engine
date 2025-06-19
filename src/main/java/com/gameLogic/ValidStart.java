package com.gameLogic;

import com.gameLogic.MapLogic.ICanCross;
import com.gameLogic.MapLogic.MapController;
import com.gameLogic.MapLogic.*;
import com.gameLogic.*;
import java.util.Map;
import java.util.Vector;

public class ValidStart {

    IAccessItems accessItems;
    ICanCross canCross;
    IMapState mapState;
    IMonsters monsters;

    public ValidStart(IAccessItems accessItems, ICanCross canCross, IMapState mapState, IMonsters monsters) {
        this.accessItems = accessItems;
        this.canCross = canCross;
        this.mapState = mapState;
        this.monsters = monsters;
    }

    public Coordinates validStartingCoordinents(Map<String, TileKey> tileKey){
        Coordinates coordinates = new Coordinates((int) Math.floor(Math.random() * accessItems.getCoordinates().y()), (int) Math.floor(Math.random() * accessItems.getCoordinates().y()));
        String tile = mapState.getMapValue(coordinates);
        TileKey key = tileKey.get(tile);
        int attempts = 0;
        while (canCross.isCave(tile) || canCross.isLadder(tile) || monsters.isMonsterOnTile(coordinates) || key.healthDelta() != 0 || !key.walkable()) {
            if(attempts > 8000){
                throw new RuntimeException("Error finding valid starting position, check overworld map");
            }
            coordinates = new Coordinates((int) Math.floor(Math.random() * accessItems.getCoordinates().y()), (int) Math.floor(Math.random() * accessItems.getCoordinates().y()));
            tile = mapState.getMapValue(coordinates);
            key = tileKey.get(tile);
            attempts++;
        }

        return coordinates;
    }
}
