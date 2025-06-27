package com.gamelogic.map;

public record TileKey(String tileId, String name, String fileLocation, boolean walkable, int visibility, int healthDelta, boolean themed, int levelDelta) {
    public String resolvePath(String theme) {
        if (themed) {
            return "/MapPics/" + theme + "Tiles/" + fileLocation;
        }
        return fileLocation;
    }
}
