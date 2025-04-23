package com.adventure_logic;

/* Todo:
        High priority:
            Full working attack w/ finding weapons
            Full working defence w/ finding armor
       Medium priority:
            More monsters across levels
            Lower levels
            Win condition
            Create bigger maps
            clean up last classes
        Low Priority:
            Lantern + changing visibility
            Cleanup warnings,
            Cleanup GUI
*/

public class Adventure {


    /**
     * values for command location.
     * 0 - > Continue on,
     * 1 -> Pickup
     * 2 -> Drop Item
     * 3 -> Use Health Item
     * 4 -> attack
     * "1" & "2" are used internally
     */
    /*
        Adventure should start and restart the game
     */
    private static Adventure adventure = getAdventure();
    public GuiEventListener controller;
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
        this.controller = control;
        gameController = new GameController(control);

    }

    //Game progression controls
    public void intro() {
         controller.UIUpdate("""
                 You find yourself lost on island, explore around to find
                 what this Island has to offer but you know you need to hurry
                 you hear noises coming from the caves that you can see.
                 """,0);
        gameController.minimap();
        controller.UIUpdate("""                                                        
                                Enter Take or Drop to pickup or drop item.
                                Then press enter. Use z to enter cave, and x
                                to climb ladder, an v to attack.
                         
                                Use arrow keys for movement, Inventory is on
                                Side of map.
                                """,0);
        controller.clearInput();
    }
    public void launchGame() {
        controller = new Controller();
        controller.UIUpdate("Welcome to Devin's Text adventure"
                + "Game press 'start game' to begin:\n",0);
    }
    public void resetGame(){
        gameController.newGame();
        controller.UIUpdate("Health: " + gameController.getHealth(),3);
        String cordOrigins = "[" + (-gameController.getCords()[1] / 2) + (-gameController.getCords()[0]/2) + "]";
        controller.UIUpdate(cordOrigins,2);
    }

    //Facade functions
    public void commandProcessor(final String direction) {
        gameController.handleCommands(direction);
   }
    public void movePlayer(int movement, char dir){
        gameController.move(movement, dir);
    }

}
