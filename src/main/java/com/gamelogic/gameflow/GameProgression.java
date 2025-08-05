package com.gamelogic.gameflow;

import com.gamelogic.map.mapLogic.MapController;
import com.gamelogic.messaging.Messenger;

public class GameProgression {
    ClassController classController;
    int deepestLevel = 0;
    public GameProgression(ClassController classController) {
        this.classController = classController;
    }
    public void levelProgression(int level, MapController currentMapController) {
        if (level > this.deepestLevel) {
            this.deepestLevel = level;
            String sound = currentMapController.getSound();
            String voice = currentMapController.getVoice();
            ClassController.mainGameController.UIUpdate("You gain confidence delving deeper, and can take more hits!", 0);
            classController.playerController.increaseMaxHealth(25 * level);
            classController.playerController.increaseLevel();

            String script = classController.scriptController.script(level);
            if(script != null) {
                ClassController.mainGameController.UIUpdate(script, 0);
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
                    ClassController.mainGameController.UIUpdate(village, 0);
                    rtnString = messenger.getPayloadString();
                }
            }
        }
        return rtnString;
    }
    public void setDeepestLevel(int deepestLevel) {
        this.deepestLevel = deepestLevel;
    }
}
