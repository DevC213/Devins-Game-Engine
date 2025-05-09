package com.gameLogic;

/* Todo:
       Medium priority:
            Create more items
            Create bigger maps
        Low Priority:
            Cleanup warnings
*/

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
}
