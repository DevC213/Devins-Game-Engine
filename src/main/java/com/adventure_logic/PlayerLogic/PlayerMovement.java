package com.adventure_logic.PlayerLogic;
import com.adventure_logic.MapLogic.ICanCross;
import com.adventure_logic.MapLogic.IDoesDamage;
import com.adventure_logic.MapLogic.IVisibility;
import com.adventure_logic.MapLogic.MapController;

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
    public int move(int movement, String currTile, String newTile, int command) {
        Boolean withinBoundaries = switch(command) {
            case 1 -> (column < maxC - 1 || column > 0);
            case 2 -> (row < maxR - 1 || row > 0);
            default -> false;
        };
        if (withinBoundaries && playerController.getHealth() > 0) {
            double tileDamageNew = doesDamage.effect(newTile);
            if (canCross.getMovement(newTile, 2)) {
                if (command == 1){
                    row += movement;
                }
                else{
                    column += movement;
                }
            } else if (tileDamageNew != 0){
                playerController.damage(tileDamageNew);
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
        if(visibility.getVisibility(newTile) < visibility.getVisibility(currTile)) {
            playerController.sendMessage("The air is so thick here...");
        }
        return visibility.getVisibility(newTile);
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
