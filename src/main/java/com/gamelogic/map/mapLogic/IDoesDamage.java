package com.gamelogic.map.mapLogic;

import com.gamelogic.map.Coordinates;
import com.gamelogic.messaging.Messenger;

public interface IDoesDamage {
    Messenger attackMonsters(String monster, int attack, Coordinates location);
    Messenger getMonstersAttack(Coordinates location);
}
