package com.gameLogic;

/* Todo:
       Medium priority:
            Create more items, and enemies
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

    public void setController(final Controller control) {
        gameController.setController(control);
    }

    //Game progression controls
    public void intro() {
        gameController.intro();
    }
    public void launchGame() {
        gameController = new GameController();
        gameController.launchGame(new Controller());
    }
    public void resetGame(){
        gameController.resetGame();
    }

    //Facade functions
    public void commandProcessor(final String direction) {
        gameController.handleInput(direction);
   }
}
