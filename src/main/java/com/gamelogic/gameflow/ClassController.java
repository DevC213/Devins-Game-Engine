package com.gamelogic.gameflow;

import com.gamelogic.combat.CombatSystem;
import com.gamelogic.commands.CommandProcessor;
import com.gamelogic.commands.Keybindings;
import com.gamelogic.core.*;
import com.gamelogic.map.Coordinates;
import com.gamelogic.map.TileKey;
import com.gamelogic.map.UIMapController;
import com.gamelogic.map.mapLogic.MapController;
import com.gamelogic.map.mapLogic.MapType;
import com.gamelogic.map.mapLogic.Overworld;
import com.gamelogic.playerlogic.PlayerController;

import java.util.Map;

public final class ClassController {


    public final PlayerController playerController;
    public static MainGameController mainGameController;
    public final MapController currentMapController;
    public final CombatSystem combatSystem;
    public final UIMapController uiMapController;
    public final CommandProcessor commandProcessor;
    public final ScriptController scriptController;
    public final EnvironmentChecker environmentChecker;
    public final GameController gameController;
    public final Map<String, TileKey> tileKeyMap;
    public ClassController(Keybindings keybindings, GameController gameController) {

        int startingVisibility;
        final String TILE_PATH = "/key.json";
        final String CHARACTERS_PATH = "/characters.json";
        final String LEVEL_DATA_PATH = "/levelData.json";

        //Initiate Tile Key
        TileKeyRegistry.initialize(TILE_PATH);
        tileKeyMap = TileKeyRegistry.getTileKeyList();


        //Initiate game controllers
        this.gameController = gameController;
        this.scriptController = new ScriptController();
        //this.mainGameController = mainGameController;

        //Initiate UI + add characters
        this.uiMapController = new UIMapController();
        this.uiMapController.processCharacters(CHARACTERS_PATH);

        //Generate maps
        this.currentMapController = new Overworld(LEVEL_DATA_PATH, MapType.OVERWORLD,0);
        MapRegistry.addMap(currentMapController,0);
        Coordinates startingCords = currentMapController.generateValidStartPosition();

        //Initiate player
        this.playerController = new PlayerController(startingCords, currentMapController.getCoordinates(), mainGameController);
        this.combatSystem = new CombatSystem(playerController);
        this.commandProcessor = new CommandProcessor(keybindings, this);
        this.environmentChecker = new EnvironmentChecker(this);

        //Set initial visibility
        startingVisibility = tileKeyMap.get(currentMapController.getMapValue(playerController.getMapCoordinates())).visibility();
        uiMapController.setVisibility(startingVisibility);

    }
    public MapController getMapController(int mapId) {
        return MapRegistry.getMapController(mapId);
    }
}
