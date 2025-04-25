package com.adventure_logic;

import com.adventure_logic.MapLogic.MapController;
import com.adventure_logic.PlayerLogic.PlayerController;

public class CombatSystem {
    private final MapController mapController;
    private final PlayerController playerController;
    private final GuiEventListener guiEventListener;
    private boolean monsterOnTile = false;

    public CombatSystem(MapController mapController, PlayerController playerController, GuiEventListener guiEventListener) {
        this.mapController = mapController;
        this.playerController = playerController;
        this.guiEventListener = guiEventListener;
    }
    public int attack() {
        if (!monsterOnTile) {
            guiEventListener.UIUpdate("No Monster on tile", 0);
            guiEventListener.clearInput();
            return 0;
        }
        guiEventListener.UIUpdate("What monster?", 0);
        guiEventListener.clearInput();
        return 4;
    }
    public void attack(String monster) {
        if (monster == null || monster.isEmpty()) {
            guiEventListener.UIUpdate("Missed.", 0);
            monsterAttack();
            return;
        }

        mapController.attackMonster(monster, playerController.getAttack(), playerController.getCords());

        for (Double damage : mapController.getMonstersAttacks(playerController.getCords())) {
            monsterAttack(damage);
        }
        guiEventListener.clearInput();
    }
    public void monsterAttack(){
        for (Double damage : mapController.getMonstersAttacks(playerController.getCords())) {
            monsterAttack(damage);
        }
    }
    private void monsterAttack(Double damage) {
        guiEventListener.UIUpdate("Monster hits you for: " + damage, 0);
        playerController.damage(damage);
        if (playerController.getHealth() <= 0) {
            if (playerController.getHealing_items() == null) {
                guiEventListener.GameOver();
            } else {
                playerController.EmergencyUse();
            }
        }
    }
    public boolean isMonsterOnTile() {
        return monsterOnTile;
    }
    public void toggleMonster() {
        this.monsterOnTile = !this.monsterOnTile;
    }
}
