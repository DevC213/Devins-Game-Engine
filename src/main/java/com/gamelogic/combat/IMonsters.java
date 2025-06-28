package com.gamelogic.combat;

import com.gamelogic.map.Coordinates;
import com.gamelogic.messaging.Messenger;

import java.util.List;

public interface IMonsters {
    Messenger spawnMonsters(Coordinates location, int moves);
    List<String> getMonsters(Coordinates location);
    boolean isMonsterOnTile(Coordinates location);

    List<String> getMonsterNames(Coordinates mapCoordinates);
}
