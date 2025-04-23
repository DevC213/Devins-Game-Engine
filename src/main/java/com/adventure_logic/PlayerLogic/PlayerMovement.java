package com.adventure_logic.PlayerLogic;
import com.adventure_logic.MapLogic.MapController;

class PlayerMovement {
    private int column;
    private int row;
    private final int maxC;
    private final int maxR;
    PlayerController playerController;
    MapController mapController;

    PlayerMovement(final int c, final int r, final int maxC, final int maxR,
                   PlayerController playerController, MapController mapController) {
        column = c;
        row = r;
        this.maxC = maxC;
        this.maxR = maxR;
        this.playerController = playerController;
        this.mapController = mapController;

    }
    public int changeRow(int movement){
        if ((row < maxR - 1 || row > 0) && playerController.getHealth() > 0) {
            if(mapController.getMovement(mapController.getMapValue(column, row+movement),2)){
                row+= movement;
            } else if (mapController.damage(mapController.getMapValue(column, row+movement)) != 0) {
                playerController.damage(mapController.damage(mapController.getMapValue(column, row+movement)));
                playerController.sendMessage("Ouch!");
            }
            if (mapController.damage(mapController.getMapValue(column, row)) != 0) {
                playerController.damage(mapController.damage(mapController.getMapValue(column, row)));
                playerController.sendMessage("Ouch!");
            }
            if (playerController.getHealth() <= 0) {
                if (playerController.getHealing_items() == null) {
                    playerController.gameOver();
                } else {
                    playerController.EmergencyUse();
                }
            }
        }

        return mapController.getVisibility(mapController.getMapValue(column, row));
    }
    public int changeColumn(int movement){
        if ((column < maxC - 1 || column > 0)&& playerController.getHealth() > 0) {
            if (mapController.getMovement(mapController.getMapValue(column + movement, row),2)) {
                column += movement;

            } else if (mapController.damage(mapController.getMapValue(column+movement, row)) != 0){
                playerController.damage(mapController.damage(mapController.getMapValue(column+movement, row)));
                playerController.sendMessage("Ouch!");
            }
            if (mapController.damage(mapController.getMapValue(column, row)) != 0) {
                playerController.damage(mapController.damage(mapController.getMapValue(column, row)));
                playerController.sendMessage("Ouch!");
            }
            if (playerController.getHealth() <= 0) {
                if (playerController.getHealing_items() == null) {
                    playerController.getHealing_items();
                    playerController.gameOver();
                } else {
                    playerController.EmergencyUse();
                }
            }
        }
        return mapController.getVisibility(mapController.getMapValue(column, row));
    }
    public int[] getCords() {
        return new int[]{column , row};
    }
    public int[] getRCords() {
        return new int[]{column - maxC/2, -(row - maxR/2)};
    }
    public void resetLocation(){
        column = 0;
        row = 0;
    }
}
