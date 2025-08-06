package com.gamelogic.gameflow;

import com.gamelogic.map.IMonsters;
import com.gamelogic.playerlogic.inventory.IAccessItems;
import com.gamelogic.map.Coordinates;
import com.gamelogic.map.IMapState;
import com.gamelogic.map.TileKey;

import java.util.Map;
import java.util.Objects;

public class ValidStart {

    IAccessItems accessItems;
    IMapState mapState;
    IMonsters monsters;

    public ValidStart(IAccessItems accessItems, IMapState mapState, IMonsters monsters) {
        this.accessItems = accessItems;
        this.mapState = mapState;
        this.monsters = monsters;
    }

    public Coordinates validStartingCoordinates(Map<String, TileKey> tileKey){
        Coordinates coordinates = new Coordinates((int) Math.floor(Math.random() * accessItems.getCoordinates().y()), (int) Math.floor(Math.random() * accessItems.getCoordinates().y()));
        String tile = mapState.getMapValue(coordinates);
        TileKey key = tileKey.get(tile);
        int attempts = 0;
        while (Objects.equals(key.name(), "cave") || Objects.equals(key.name(), "ladder") || monsters.isMonsterOnTile(coordinates) || key.healthDelta() != 0 || !key.walkable()) {
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
