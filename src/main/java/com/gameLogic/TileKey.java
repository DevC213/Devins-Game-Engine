package com.gameLogic;

public record TileKey(String tileId, String name, String fileLocation, boolean walkable, int visibility, int healthDelta, boolean multiFile) {}
