package com.gamelogic.gameflow;

import com.gamelogic.core.Controller;

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
        gameController = new GameController(control);
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
}
