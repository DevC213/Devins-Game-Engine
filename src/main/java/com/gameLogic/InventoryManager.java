package com.gameLogic;

import com.gameLogic.PlayerLogic.PlayerController;

public class InventoryManager {
    private final PlayerController playerController;
    private final IGuiEventListener guiEventListener;

    public InventoryManager(PlayerController playerController, IGuiEventListener guiEventListener) {
        this.playerController = playerController;
        this.guiEventListener = guiEventListener;
    }
    public void useHealthItem(String item) {
        if (item == null || item.isEmpty()) {
            guiEventListener.UIUpdate("No healing item entered.", 0);
            return;
        }
        playerController.useHealing(item);
        updateInventoryDisplay();
    }
    public void updateInventoryDisplay() {
        StringBuilder sendString = new StringBuilder();
        if (playerController.InventoryCommands(null, 3) != null) {
            for (String i : playerController.InventoryCommands(null, 3)) {
                sendString.append(i).append("\n");
            }
        }
        if (!(playerController.getHealing_items() == null)) {
            sendString.append(playerController.getHealing_items());
        }
        guiEventListener.UIUpdate(sendString.toString(), 1);
    }

}
