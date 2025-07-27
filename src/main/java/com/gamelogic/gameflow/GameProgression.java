package com.gamelogic.gameflow;

import com.gamelogic.map.mapLogic.MapController;
import com.gamelogic.messaging.Messenger;

public class GameProgression {
    ClassController classController;
    int deepestLevel = 0;
    MapController mapController;
    public GameProgression(ClassController classController) {
        this.classController = classController;
    }
    public void levelProgression(int level, int deepestLevel, MapController currentMapController) {
        if (level > deepestLevel) {
            String sound = currentMapController.getSound();
            String voice = currentMapController.getVoice();
            classController.mainGameController.UIUpdate("You gain confidence delving deeper, and can take more hits!", 0);
            classController.playerController.increaseMaxHealth(25 * level);
            classController.playerController.increaseLevel();
            deepestLevel = level;
            String script = classController.scriptController.script(level);
            if(script != null) {
                classController.mainGameController.UIUpdate(script, 0);
            } else {
                if(sound != null) {
                    classController.scriptController.playSound(sound);
                }
                if(voice != null) {
                    classController.scriptController.playSound(voice);
                }
            }
        }
    }
    public String checkProgression(MapController currentMapController) {
        String rtnString = null;
        if(currentMapController.getLevel() == 0) {
            Messenger messenger = currentMapController.checkForVillages(classController.playerController.getMapCoordinates());
            if (messenger != null) {
                String village = messenger.getMessage();
                if (village != null) {
                    classController.mainGameController.UIUpdate(village, 0);
                    rtnString = messenger.getPayloadString();
                }
            }
        }
        return rtnString;
    }
}
