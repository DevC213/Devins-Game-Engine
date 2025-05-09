package com.gameLogic.PlayerLogic;
import com.gameLogic.MapLogic.ICanCross;
import com.gameLogic.MapLogic.IDoesDamage;
import com.gameLogic.MapLogic.IVisibility;
import com.gameLogic.MapLogic.MapController;

class PlayerMovement {
    private int column;
    private int row;
    private final int maxC;
    private final int maxR;
    PlayerController playerController;
    IDoesDamage doesDamage;
    ICanCross canCross;
    IVisibility visibility;


    PlayerMovement(final int c, final int r, final int maxC, final int maxR,
                   PlayerController playerController, MapController mapController) {
        column = c;
        row = r;
        this.maxC = maxC;
        this.maxR = maxR;
        this.playerController = playerController;
        doesDamage = mapController;
        canCross = mapController;
        visibility = mapController;
    }
    public int move(int deltaX, int deltaY, String currTile, String newTile) {
        boolean withinBoundaries;
        if(deltaX != 0){
            withinBoundaries = (column < maxC  || column > 0);
        }else if(deltaY != 0){
            withinBoundaries = (row < maxR || row > 0);
        } else{
            withinBoundaries = false;
        }
        if (withinBoundaries && playerController.getHealth() > 0) {
            double tileDamageNew = doesDamage.effect(newTile);
            if (canCross.getMovement(newTile, 2)) {
                row += deltaY;
                column += deltaX;
            } else if (tileDamageNew != 0){
                playerController.damage(tileDamageNew);
                playerController.sendMessage("PLayer: Ouch!");
            }
            if (playerController.getHealth() <= 0) {
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
    public int[] getCords() {
        return new int[]{column , row};
    }
    public int[] getRCords() {
        return new int[]{column - maxC/2, -(row - maxR/2)};
    }
    public void resetLocation(int[] coords) {
        column = coords[0];
        row = coords[1];
    }

    public int checkVisability(String tile){
        return visibility.getVisibility(tile);
    }
}
