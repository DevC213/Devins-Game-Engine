package com.gamelogic.core;

import javafx.scene.media.AudioClip;

import java.util.Objects;

public class ScriptController {

    public ScriptController() {

    }
    public String script(int level) {
        return switch (level) {
            case 0, 1, 2, 3, 4 -> null;
            default -> "I dont know how you got here, but sure, break this world why dont you!";
        };
    }
    public void playSound(String resourcePath) {
        AudioClip clip;
        try {
            clip = new AudioClip(Objects.requireNonNull(getClass().getResource(resourcePath)).toString());
            clip.play();
        } catch (Exception e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
