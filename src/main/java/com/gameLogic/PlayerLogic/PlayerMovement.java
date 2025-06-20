package com.gameLogic.PlayerLogic;
import com.gameLogic.Coordinates;
import com.gameLogic.MapLogic.ICanCross;
import com.gameLogic.MapLogic.IDoesDamage;
import com.gameLogic.MapLogic.IVisibility;

import java.awt.geom.Point2D;

class PlayerMovement {

    Coordinates maxCoords;
    PlayerController playerController;
    IDoesDamage doesDamage;
    ICanCross canCross;
    IVisibility visibility;
    Point2D player;


    PlayerMovement(Coordinates playerStart, Coordinates maxCoords,
                   PlayerController playerController, IVisibility visibility, IDoesDamage doesDamage, ICanCross canCross) {
        player = new Point2D.Double(playerStart.x(), playerStart.y());
        this.maxCoords = new Coordinates(maxCoords.x(), maxCoords.y());
        this.playerController = playerController;
        this.doesDamage = doesDamage;
        this.canCross = canCross;
        this.visibility = visibility;
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
        if (withinBoundaries && playerController.getHealth() > 0) {
            double tileDamageNew = doesDamage.getHealthDelta(newTile);
            if (canCross.isWalkable(newTile)) {
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
        if(visibility.getVisibility(newTile) < visibility.getVisibility(currTile)) {
            if (visibility.getVisibility(newTile) == 0) {
                playerController.sendMessage("Player: I cant see!!");
            } else {
                playerController.sendMessage("PLayer: The air is so thick here...");
            }
        }
        return visibility.getVisibility(newTile);
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
}
