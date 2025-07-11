package com.gamelogic.map.mapLogic;

import java.util.List;
import java.util.Map;

public abstract class MapData {
    public MapData() {}
    public abstract void processMap(int level, Map<String, String> levelMap, String theme, String voice, String sound);
    public abstract void processMap(String filePath);
    public abstract void defaultLevel();
    public abstract LevelData getLevel(int level);
    public abstract int getTotalLevels();
    public abstract List<List<List<String>>> getMap();

    public abstract void loadMap(List<List<List<String>>> map);
}
