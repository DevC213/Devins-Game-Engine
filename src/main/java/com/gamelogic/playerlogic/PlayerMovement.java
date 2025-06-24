package com.gamelogic.playerlogic;
import com.gamelogic.core.TileKeyRegistry;
import com.gamelogic.map.Coordinates;
import com.gamelogic.map.TileKey;
import com.gamelogic.map.mapLogic.ICanCross;
import com.gamelogic.map.mapLogic.IDoesDamage;
import com.gamelogic.map.mapLogic.IVisibility;
import com.gamelogic.map.mapLogic.MapController;

import java.awt.geom.Point2D;
import java.util.Map;

class PlayerMovement {

    Coordinates maxCoords;
    PlayerController playerController;
    Point2D player;

    Map<String, TileKey> tileKeyMap;


    PlayerMovement(Coordinates playerStart, Coordinates maxCoords,
                   PlayerController playerController) {
        player = new Point2D.Double(playerStart.x(), playerStart.y());
        this.maxCoords = new Coordinates(maxCoords.x(), maxCoords.y());
        this.playerController = playerController;
        tileKeyMap = TileKeyRegistry.getTileKeyList();
    }
    public int move(Coordinates delta, String currTile, String newTile) {
        boolean withinBoundaries;
        if(delta.x() != 0){
            withinBoundaries = (player.getX() < maxCoords.x()  || player.getX() > 0);
        }else if(delta.y() != 0){
            withinBoundaries = (player.getY() < maxCoords.y() || player.getY() > 0);
        } else{
            withinBoundaries = false;
        }
        int visibilityNew = tileKeyMap.get(newTile).visibility();
        int currentVisibility = tileKeyMap.get(currTile).visibility();
        if (withinBoundaries && playerController.getHealth() > 0) {
            double tileDamageNew = tileKeyMap.get(newTile).healthDelta();
            if (tileKeyMap.get(newTile).walkable()) {
                player.setLocation(player.getX() + delta.x(), player.getY() + delta.y());
            } else if (tileDamageNew != 0){
                playerController.changeHealth(tileDamageNew);
                playerController.sendMessage("PLayer: Ouch!");
            }
            while(playerController.getHealth() <= 0) {
                if (playerController.getHealingItems() == null) {
                    playerController.gameOver();
                    return -1;
                } else {
                    playerController.EmergencyUse();
                }
            }
        }
        while(playerController.getHealth() <= 0) {
            if (playerController.getHealingItems() == null) {
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
        return new Coordinates((int)player.getX() , (int)player.getY());
    }
    public Coordinates getDisplayCoordinates() {
        return new Coordinates((int)player.getX() - maxCoords.x()/2, -((int)player.getY()- maxCoords.y()/2));
    }
    public void resetLocation(Coordinates coords) {
        player.setLocation(coords.x(), coords.y());
    }

    public void setLocation(int x, int y) {
        player.setLocation(x, y);
    }
}
