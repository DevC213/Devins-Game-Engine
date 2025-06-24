package com.gamelogic.map.mapLogic;

import com.gamelogic.map.Coordinates;
import com.gamelogic.messaging.Messenger;

public interface IDoesDamage {
    int getHealthDelta(final String terrain);
    Messenger attackMonsters(String monster, int attack, Coordinates location);
    Messenger getMonstersAttack(Coordinates location);
}
