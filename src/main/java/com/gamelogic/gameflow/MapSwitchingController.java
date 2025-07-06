package com.gamelogic.gameflow;

import com.gamelogic.map.Coordinates;
import com.gamelogic.map.IExitCoordinates;
import com.gamelogic.map.mapLogic.MapController;

public class MapSwitchingController {

    ClassController classController;
    public MapSwitchingController(ClassController classController) {
        this.classController = classController;
    }

    public Coordinates loadMapFromID(int currentMapID, MapController currentMapController) {
        switchMap(currentMapID);
        Coordinates returnMapLocation = null;
        if(currentMapController instanceof IExitCoordinates exitCoordinates && currentMapController.getLevel() == 0) {
            Coordinates mainMap = exitCoordinates.getMapCoordinates();
            returnMapLocation = new Coordinates(mainMap.y(), mainMap.x());
            classController.playerController.setMaxCoordinates(currentMapController.getCoordinates());
        }
        return returnMapLocation;
    }
    public MapController switchMap(int ID) {
        MapController currentMapController = classController.getMapController(ID);
        classController.commandProcessor.changeMapState(currentMapController);
        classController.environmentChecker.changeMap(currentMapController);
        if(currentMapController instanceof IExitCoordinates exitCoordinates) {
            Coordinates exit = exitCoordinates.getExitCoordinates();
            classController.playerController.setCoordinates(exit.y(), exit.x()-1);
            classController.playerController.setMaxCoordinates(currentMapController.getCoordinates());
        }
        return currentMapController;
    }
    public MapController returnToMainMap(Coordinates mainMapLocation) {
        MapController currentMapController = classController.getMapController(0);
        classController.commandProcessor.changeMapState(currentMapController);
        classController.playerController.setMaxCoordinates(currentMapController.getCoordinates());
        classController.playerController.setCoordinates(mainMapLocation.x(),  mainMapLocation.y()+1);
        classController.environmentChecker.changeMap(currentMapController);
        return currentMapController;
    }
}
