package com.gameLogic.MapLogic;

import com.gameLogic.Coordinates;
import com.gameLogic.Messenger;

public interface IDoesDamage {
    int getHealthDelta(final String terrain);
    public Messenger attackMonsters(String monster, int attack, Coordinates location);
    public Messenger getMonstersAttack(Coordinates location);
}
