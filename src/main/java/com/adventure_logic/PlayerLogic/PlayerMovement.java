package com.adventure_logic.PlayerLogic;

import com.adventure_logic.Adventure;
import com.adventure_logic.GuiEventListener;

class PlayerMovement {
    private int column;
    private int row;
    private final int maxC;
    private final int maxR;
    PlayerController playerController;
    Adventure adventure = Adventure.getAdventure();

    PlayerMovement(final int c, final int r, final int maxC, final int maxR,
                   PlayerController playerController) {
        column = c;
        row = r;
        this.maxC = maxC;
        this.maxR = maxR;
        this.playerController = playerController;

    }
    public void changeRow(int movement){
        if ((row < maxR - 1 || row > 0) && playerController.getHealth() > 0) {
            if(adventure.getCanCross(new int[]{column, row+movement})) {
                row+= movement;
            } else if (adventure.getDoesDamage(new int[]{column, row + movement})) {
                playerController.damage(adventure.tileDamage(new int[]{column, row + movement}));
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

    }
    public void changeColumn(int movement){
        if ((column < maxC - 1 || column > 0)&& playerController.getHealth() > 0) {
            if (adventure.getCanCross(new int[]{column + movement, row})) {
                column+= movement;
            }else if (adventure.getDoesDamage(new int[]{column + movement, row})){
                playerController.damage(adventure.tileDamage(new int[]{column + movement, row}));
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
    }
    public int[] getCords() {
        return new int[]{column , row};
    }
    public int[] getRCords() {return new int[]{column - maxC/2, -(row - maxR/2)};}
    public void resetLocation(){
        column = 0;
        row = 0;
    }
}
