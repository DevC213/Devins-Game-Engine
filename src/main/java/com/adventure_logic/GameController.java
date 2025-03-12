package com.adventure_logic;

import com.adventure_logic.MapLogic.MapController;
import com.adventure_logic.PlayerLogic.PlayerController;

public class GameController {

    PlayerController playerController;
    GuiEventListener guiEventListener;
    MapController mapController;
    GameController(GuiEventListener controller) {
        guiEventListener = controller;
        mapController = new MapController("MapData/Maps/Map1.txt");
        playerController = new PlayerController(0, 0,
                mapController.getCords()[1], mapController.getCords()[0], controller);
    }
}
