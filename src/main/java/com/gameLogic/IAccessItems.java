package com.gameLogic;

import com.Armor.Armor;
import com.Weapons.Weapon;
import com.recoveryItems.RecoveryItem;

public interface IAccessItems {
    public boolean itemsOnTile(Coordinates location);
    public StringBuilder itemList(Coordinates location);
    public Weapon getWeapons(Coordinates location);
    public Coordinates getCoordinates();
    public Messenger grabItem(Coordinates location, final String item);
    public Armor getArmor(Coordinates location);
    public RecoveryItem getHealing(Coordinates location);
}
