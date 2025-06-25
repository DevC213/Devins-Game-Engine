package com.gamelogic.gameflow;

import com.gamelogic.commands.Keybindings;
import com.gamelogic.core.Controller;
import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Objects;

public class Adventure {

    private static Adventure adventure = getAdventure();
    public GameController gameController;

    //Constructor/initialization methods
    private Adventure() {
    }
    public static synchronized Adventure getAdventure(){
        if (adventure == null){
            adventure = new Adventure();
        }
        return adventure;
    }

    public void startGame(final Controller control) {
        gameController = new GameController(control, readInKeyBindings());
    }
    public void setCharacterID(String characterID) {
        gameController.setCharacter(characterID);
    }
    //Game progression controls
    public void intro() {
        gameController.intro();
    }
    public void resetGame(){
        gameController.resetGame();
    }

    //Facade functions
    public void commandProcessor(final String direction) {
        gameController.handleInput(direction);
   }

    public void setHealth() {
        gameController.setHealth();
    }
    private Keybindings readInKeyBindings(){

        try {
            Gson gson = new Gson();
            InputStream input = Objects.requireNonNull(getClass().getResourceAsStream("/keyBindings.json"));
            InputStreamReader reader = new InputStreamReader(input);
            return gson.fromJson(reader, Keybindings.class);
        } catch (Exception e) {
            return new Keybindings("v","c","x","b");
        }
    }
}
