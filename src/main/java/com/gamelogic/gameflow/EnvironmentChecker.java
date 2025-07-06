package com.gamelogic.gameflow;

import com.gamelogic.combat.CombatSystem;
import com.gamelogic.commands.CommandProcessor;
import com.gamelogic.core.MainGameController;
import com.gamelogic.map.mapLogic.MapController;
import com.gamelogic.playerlogic.PlayerController;

public class EnvironmentChecker {
    enum TileStatus {NEUTRAL, HEALING, DAMAGING}

    TileStatus tileStatus = TileStatus.NEUTRAL;
    MainGameController mainGameController;
    PlayerController playerController;
    MapController mapController;

    EnvironmentChecker(MainGameController mainGameController, PlayerController playerController, MapController mapController) {
        this.mainGameController = mainGameController;
        this.playerController = playerController;
        this.mapController = mapController;
    }

    public void changeMap(MapController mapController) {
        this.mapController = mapController;
    }

    private void checkTileEffect(double effect) {
        if (effect < 0) {
            if (tileStatus != TileStatus.DAMAGING) {
                mainGameController.UIUpdate("Player: It hurts walking here.", 0);
                tileStatus = TileStatus.DAMAGING;
            }
        } else if (effect > 0) {
            if (tileStatus != TileStatus.HEALING) {
                mainGameController.UIUpdate("Player: Its is soothing to my feet walking here.", 0);
                tileStatus = TileStatus.HEALING;
            }
        } else {
            tileStatus = TileStatus.NEUTRAL;
        }
        if (effect != 0) {
            playerController.changeHealth(effect);
        }
    }

    public void checkTile(CombatSystem combatSystem, CommandProcessor commandProcessor, double effect) {
        checkForItems();
        checkForMonsters(combatSystem, commandProcessor);
        checkTileEffect(effect);
    }

    private void checkForItems() {
        String string = mapController.itemList(playerController.getMapCoordinates()).toString();
        if (!string.isEmpty()) {
            mainGameController.UIUpdate("Items at location: \n" + string, 0);
        }
    }

    private void checkForMonsters(CombatSystem combatSystem, CommandProcessor commandProcessor) {
        boolean monsterFound = mapController.isMonsterOnTile(playerController.getMapCoordinates());
        if (monsterFound) {
            mainGameController.UIUpdate("Monsters at location: " +
                    mapController.getMonsters(playerController.getMapCoordinates()), 0);
            if (!combatSystem.isMonsterOnTile()) {
                combatSystem.toggleMonster();
            }
        } else {
            if (commandProcessor.escaped()) {
                commandProcessor.toggleEscape();
                combatSystem.toggleMonster();
            } else if (combatSystem.isMonsterOnTile()) {
                mainGameController.UIUpdate("Monsters Killed", 0);
                combatSystem.toggleMonster();
                commandProcessor.clearCommandState();
            }
        }
    }
}
