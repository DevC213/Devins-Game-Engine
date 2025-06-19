package com.gameLogic;

import java.util.List;
import java.util.Vector;

public interface IMonsters {
    public Messenger spawnMonsters(Coordinates location, int moves);
    public List<String> getMonsters(Coordinates location);
    public boolean isMonsterOnTile(Coordinates location);
}
