package com.gamelogic.combat;

import com.gamelogic.messaging.Messenger;
import com.gamelogic.playerlogic.PlayerDamageListener;

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
        for (Double damage : messenger.getPayloadD()) {
            monsterAttack(damage);
        }
    }
    private void monsterAttack(Double damage) {
        playerDamageListener.damage(damage);
    }
    public boolean isMonsterOnTile() {
        return monsterOnTile;
    }
    public void toggleMonster() {
        this.monsterOnTile = !this.monsterOnTile;
    }
}
