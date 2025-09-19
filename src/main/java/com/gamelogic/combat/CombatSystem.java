package com.gamelogic.combat;

import com.gamelogic.messaging.Messenger;
import com.gamelogic.playerlogic.PlayerDamageListener;
import com.monsters.Monster;

public class CombatSystem {
    private final PlayerDamageListener playerDamageListener;
    private boolean monsterOnTile = false;


    public CombatSystem(PlayerDamageListener playerDamageListener) {
        this.playerDamageListener = playerDamageListener;
    }

    public Messenger attack(Messenger monsterAttack) {
        String message;
        Messenger messenger = new Messenger();
        message = monsterAttack.getMessage();

        if(message != null) {
            messenger.setMessage(message);
        }
        return messenger;
    }
    public void monstersAttack(Messenger messenger){
        for (Monster i : messenger.getMonsters()) {
            monsterAttack(i.getBaseAttack(),i.getFullName());
        }
    }
    private void monsterAttack(Double damage, String monsterName) {
        playerDamageListener.damage(damage, monsterName);
    }
    public boolean isMonsterOnTile() {
        return monsterOnTile;
    }
    public void toggleMonster() {
        this.monsterOnTile = !this.monsterOnTile;
    }
}
