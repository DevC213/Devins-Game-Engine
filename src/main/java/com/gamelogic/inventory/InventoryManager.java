package com.gamelogic.inventory;

import com.gamelogic.commands.IGuiEventListener;
import com.gamelogic.playerlogic.PlayerController;

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
        if (playerController.viewInventory() != null) {
            for (String i : playerController.viewInventory()) {
                sendString.append(i).append("\n");
            }
        }
        if (!(playerController.getHealingItems() == null)) {
            sendString.append(playerController.getHealingItems());
        }
        guiEventListener.UIUpdate(sendString.toString(), 1);
    }

}
