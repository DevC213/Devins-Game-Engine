package com.gamelogic.gameflow;

import com.gamelogic.core.MapRegistry;
import com.gamelogic.map.Coordinates;
import com.gamelogic.map.mapLogic.MapController;

public class GameProgressController {
    ClassController classController;
    public GameProgressController(ClassController classController) {
        this.classController = classController;
    }
    public void newGame() {
        MapController currentMapController = MapRegistry.getMapController(0);
        Coordinates startingCords = currentMapController.generateValidStartPosition();
        classController.playerController.resetPlayer(startingCords);
        currentMapController.setLevel(0);
        currentMapController.resetMap();
        intro();
        int startingVisibility = classController.tileKeyMap.get(currentMapController.getMapValue(classController.playerController.getMapCoordinates())).visibility();
        classController.uiMapController.setVisibility(startingVisibility);
        classController.uiMapController.setDirection("down");
        ClassController.mainGameController.UIUpdate(classController.playerController.getWeapon().name() + ": " + classController.playerController.getWeapon().damage(), 5);
        if(classController.playerController.isGameOver()){classController.playerController.toggleGameOver();}
        if (startingVisibility != 2) {
            ClassController.mainGameController.UIUpdate("Player: The air is thick here", 0);
        }
    }
    public void intro() {
        ClassController.mainGameController.UIUpdate("""
                Upon awaking on this strange island
                you hear a strange voice calling from the caves below.
                After hearing the voice you feel a chill go down you back,
                something isn't right!
                """, 0);
        ClassController.mainGameController.clearInput();
        ClassController.mainGameController.scroll();
    }
    public void resetGame(MapController currentMapController) {
        newGame();
        ClassController.mainGameController.UIUpdate("Health: " + classController.playerController.getHealth(), 3);
        String cordOrigins = "[" + (-currentMapController.getCoordinates().x() / 2) + (-currentMapController.getCoordinates().y() / 2) + "]";
        ClassController.mainGameController.UIUpdate(cordOrigins, 2);
    }
    public void respawn() {
        MapController currentMapController = MapRegistry.getMapController(0);
        currentMapController.setLevel(0);
        Coordinates coordinates = currentMapController.generateValidStartPosition();
        classController.playerController.respawn(coordinates);
        ClassController.mainGameController.UIUpdate("You passed out for a time, and found yourself if a different location.\n" +
                "You were robbed 50!",0);
        ClassController.mainGameController.UIUpdate("Money: " + classController.playerController.getGold(),6);
        ClassController.mainGameController.UIUpdate("Health: " + classController.playerController.getHealth(),3);
    }
}
