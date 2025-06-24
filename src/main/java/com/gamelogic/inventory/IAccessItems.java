package com.gamelogic.inventory;

import com.armor.Armor;
import com.weapons.Weapon;
import com.gamelogic.map.Coordinates;
import com.gamelogic.messaging.Messenger;
import com.recoveryitems.RecoveryItem;

public interface IAccessItems {
    boolean itemsOnTile(Coordinates location);
    StringBuilder itemList(Coordinates location);
    Weapon getWeapons(Coordinates location);
    Coordinates getCoordinates();
    Messenger grabItem(Coordinates location, final String item);
    Armor getArmor(Coordinates location);
    RecoveryItem getHealing(Coordinates location);
}
