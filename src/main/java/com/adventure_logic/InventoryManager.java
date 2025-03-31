package com.adventure_logic;

import com.adventure_logic.PlayerLogic.PlayerController;

public class InventoryManager {
    private final PlayerController playerController;
    private final GuiEventListener guiEventListener;

    public InventoryManager(PlayerController playerController, GuiEventListener guiEventListener) {
        this.playerController = playerController;
        this.guiEventListener = guiEventListener;
    }
    public int useHealthPot() {
        guiEventListener.clearInput();
        guiEventListener.UIUpdate("Enter Item to use:", 0);
        return 3;
    }
    public int useHealthPot(String item) {
        if(item == null || item.isEmpty()){
            guiEventListener.UIUpdate("No healing item entered.",0);
            return 0;
        }
        playerController.useHealing(item);
        updateInventoryDisplay();
        return 3;
    }
    public void dropItem(String item) {

        if (item == null) {
            guiEventListener.UIUpdate("Nothing dropped", 0);
            return;
        }
        guiEventListener.UIUpdate("Dropping Item...", 0);
        playerController.InventoryCommands(new String[]{item}, 4);
        updateInventoryDisplay();
    }
    public void takeItem(String item) {

        if (item == null) {
            guiEventListener.UIUpdate("Items left", 0);
        } else if (item.split(",").length > 1) {
            playerController.InventoryCommands(item.split(","), 1);
            guiEventListener.UIUpdate("Items Added", 0);
        } else if (item.split(",").length == 1) {
            playerController.InventoryCommands(new String[]{item}, 1);
            guiEventListener.UIUpdate("Item Added", 0);
        } else {
            guiEventListener.UIUpdate("Items left", 0);
        }
        updateInventoryDisplay();
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
    public int getInventoryTotal() {
        return playerController.InventoryCommands(null, 3).size();
    }
}
