package com.gamelogic.playerlogic;
import com.gamelogic.core.TileKeyRegistry;
import com.gamelogic.map.Coordinates;
import com.gamelogic.map.TileKey;
import java.util.Map;

class PlayerMovement {

    Coordinates maxCoords;
    Coordinates coordinates;
    PlayerController playerController;
    Map<String, TileKey> tileKeyMap;


    PlayerMovement(Coordinates playerStart, Coordinates maxCoords,
                   PlayerController playerController) {
        this.maxCoords = maxCoords;
        coordinates = playerStart;
        this.playerController = playerController;
        tileKeyMap = TileKeyRegistry.getTileKeyList();
    }
    public int move(Coordinates delta, String currTile, String newTile) {
        boolean withinBoundaries;
        if(delta.x() != 0){
            withinBoundaries = (coordinates.x() < maxCoords.x()  || coordinates.x() > 0);
        }else if(delta.y() != 0){
            withinBoundaries = (coordinates.y() < maxCoords.y() || coordinates.y() > 0);
        } else{
            withinBoundaries = false;
        }
        int visibilityNew = tileKeyMap.get(newTile).visibility();
        int currentVisibility = tileKeyMap.get(currTile).visibility();
        if (withinBoundaries && playerController.getHealth() > 0) {
            double tileDamageNew = tileKeyMap.get(newTile).healthDelta();
            if (tileKeyMap.get(newTile).walkable()) {
                coordinates = new Coordinates(coordinates.x() + delta.x(), coordinates.y() + delta.y());
            } else if (tileDamageNew != 0){
                playerController.changeHealth(tileDamageNew);
                playerController.sendMessage("PLayer: Ouch!");
            }
            while(playerController.getHealth() <= 0) {
                if (playerController.getHealingItems().isEmpty()) {
                    playerController.gameOver();
                    return -1;
                } else {
                    playerController.EmergencyUse();
                }
            }
        }
        while(playerController.getHealth() <= 0) {
            if (playerController.getHealingItems().isEmpty()) {
                playerController.gameOver();
                return -1;
            } else {
                playerController.EmergencyUse();
            }
        }
        if(visibilityNew < currentVisibility && !newTile.equals("-")) {
            if (visibilityNew == 0) {
                playerController.sendMessage("Player: I cant see!!");
            } else {
                playerController.sendMessage("PLayer: The air is so thick here...");
            }
        }
        if(newTile.equals("-")) {
            return currentVisibility;
        }
        return visibilityNew;
    }
    public Coordinates getMapCoordinates() {
        return coordinates;
    }
    public Coordinates getDisplayCoordinates() {
        return new Coordinates(coordinates.x() - maxCoords.x()/2, -(coordinates.y() - maxCoords.y()/2));
    }
    public void resetLocation(Coordinates coords) {
        coordinates = coords;
    }

    public void setLocation(int x, int y) {
        coordinates = new Coordinates(x, y);
    }
    public void setMaxCoords(Coordinates maxCoords) {
        this.maxCoords = maxCoords;
    }
}
