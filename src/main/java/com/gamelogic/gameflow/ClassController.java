package com.gamelogic.gameflow;

import com.gamelogic.combat.CombatSystem;
import com.gamelogic.commands.CommandProcessor;
import com.gamelogic.commands.Keybindings;
import com.gamelogic.core.*;
import com.gamelogic.inventory.InventoryManager;
import com.gamelogic.map.Coordinates;
import com.gamelogic.map.TileKey;
import com.gamelogic.map.UIMapController;
import com.gamelogic.map.mapLogic.MapController;
import com.gamelogic.map.mapLogic.MapType;
import com.gamelogic.map.mapLogic.Overworld;
import com.gamelogic.playerlogic.PlayerController;

import java.util.Map;

public class ClassController {
    public final PlayerController playerController;
    public final MainGameController mainGameController;
    public MapController currentMapController;
    public final InventoryManager inventoryManager;
    public final CombatSystem combatSystem;
    public final UIMapController uiMapController;
    public final CommandProcessor commandProcessor;
    public final ScriptController scriptController;
    public final EnvironmentChecker environmentChecker;
    public final GameController gameController;
    public final Map<String, TileKey> tileKeyMap;
    public ClassController(MainGameController mainGameController, Keybindings keybindings, GameController gameController, Difficulty difficulty) {
        this.gameController = gameController;
        tileKeyMap = TileKeyRegistry.getTileKeyList();
        scriptController = new ScriptController();
        this.mainGameController = mainGameController;
        TileKeyRegistry.initialize("/key.json");
        uiMapController = new UIMapController();
        this.currentMapController = new Overworld("/levelData.json", MapType.OVERWORLD,0);
        MapRegistry.addMap(currentMapController,0);
        uiMapController.processCharacters("/characters.json");
        Coordinates startingCords = currentMapController.generateValidStartPosition();
        this.playerController = new PlayerController(startingCords, currentMapController.getCoordinates(), this.mainGameController, difficulty);
        this.combatSystem = new CombatSystem(playerController);
        inventoryManager = new InventoryManager(playerController, mainGameController);
        commandProcessor = new CommandProcessor(mainGameController, playerController, this.gameController, this.gameController, combatSystem, inventoryManager,
                currentMapController, currentMapController, currentMapController, currentMapController, keybindings);
        int startingVisibility = tileKeyMap.get(currentMapController.getMapValue(playerController.getMapCoordinates())).visibility();
        uiMapController.setVisibility(startingVisibility);
        if (startingVisibility != 2) {
            mainGameController.UIUpdate("Player: The air is thick here", 0);
        }
        environmentChecker = new EnvironmentChecker(this.mainGameController,playerController,currentMapController);
    }
    public MapController getMapController(int mapId) {
        return MapRegistry.getMapController(mapId);
    }
    public TileKey getTileKey(String tile) {
        return tileKeyMap.get(tile);
    }
}
