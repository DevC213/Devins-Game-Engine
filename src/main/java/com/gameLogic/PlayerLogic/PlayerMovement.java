package com.gameLogic.PlayerLogic;
import com.gameLogic.Coordinates;
import com.gameLogic.MapLogic.ICanCross;
import com.gameLogic.MapLogic.IDoesDamage;
import com.gameLogic.MapLogic.IVisibility;
import com.gameLogic.MapLogic.MapController;

class PlayerMovement {
    private int column;
    private int row;
    Coordinates maxCoords;
    PlayerController playerController;
    IDoesDamage doesDamage;
    ICanCross canCross;
    IVisibility visibility;


    PlayerMovement(Coordinates playerStart, Coordinates maxCoords,
                   PlayerController playerController, MapController mapController) {
        column = playerStart.x();
        row = playerStart.y();
        this.maxCoords = new Coordinates(maxCoords.x(), maxCoords.y());
        this.playerController = playerController;
        doesDamage = mapController;
        canCross = mapController;
        visibility = mapController;
    }
    public int move(int deltaX, int deltaY, String currTile, String newTile) {
        boolean withinBoundaries;
        if(deltaX != 0){
            withinBoundaries = (column < maxCoords.x()  || column > 0);
        }else if(deltaY != 0){
            withinBoundaries = (row < maxCoords.y() || row > 0);
        } else{
            withinBoundaries = false;
        }
        if (withinBoundaries && playerController.getHealth() > 0) {
            double tileDamageNew = doesDamage.getHealthDelta(newTile);
            if (canCross.isWalkable(newTile)) {
                row += deltaY;
                column += deltaX;
            } else if (tileDamageNew != 0){
                playerController.changeHealth(tileDamageNew);
                playerController.sendMessage("PLayer: Ouch!");
            }
            while(playerController.getHealth() <= 0) {
                if (playerController.getHealing_items() == null) {
                    playerController.gameOver();
                    return -1;
                } else {
                    playerController.EmergencyUse();
                }
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
    public Coordinates getCords() {
        return new Coordinates(column , row);
    }
    public Coordinates getRCords() {
        return new Coordinates(column - maxCoords.x()/2, -(row - maxCoords.y()/2));
    }
    public void resetLocation(Coordinates coords) {
        column = coords.x();
        row = coords.y();
    }
}
