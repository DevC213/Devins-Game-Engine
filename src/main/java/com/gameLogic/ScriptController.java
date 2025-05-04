package com.gameLogic;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.Objects;

public class ScriptController {

    public ScriptController() {

    }
    public String script(int level) {
        return switch (level) {
            case 0 -> null;
            case 1, 2 -> {
                scriptVoice(level);
                yield null;
            }
            case 3, 4 -> {levelMusic(level);
                scriptVoice(level);
                yield null;
            }
            default -> "I dont know how you got here, but sure, break this world why dont you!";
        };
    }
    public void levelMusic(int level) {
        String resourcePath;
        switch (level) {
            case 3:
                resourcePath = "/Sound/darkness.wav";
                break;
            case 4:
                resourcePath = "/Sound/theVoid.wav";
                break;
            default:
                return;
        }

        URL resource = getClass().getResource(resourcePath);
        if (resource == null) {
            System.out.println("Sound file not found: " + resourcePath);
            return;
        }
        try {
            Media sound = new Media(resource.toString());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        } catch (Exception e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
    public void scriptVoice(int level) {
        String resourcePath;
        AudioClip clip;
        switch (level) {
            case 1:
                resourcePath = "/Sound/undergroundVoice.wav";
                break;
            case 2:
                resourcePath = "/Sound/cavernVoice.wav";
                break;
            case 3:
                resourcePath = "/Sound/theDarknessVoice.wav";
                break;
            case 4:
                resourcePath = "/Sound/theVoidVoice.wav";
                break;
            default:
                return;
        }
        try {
            clip = new AudioClip(Objects.requireNonNull(getClass().getResource(resourcePath)).toString());
            clip.play();
        } catch (Exception e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
