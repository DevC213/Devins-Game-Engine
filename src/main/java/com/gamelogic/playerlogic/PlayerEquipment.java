package com.gamelogic.playerlogic;

import com.armor.Armor;
import com.weapons.Weapon;

class PlayerEquipment {
    private Armor armor;
    private int defence;
    private Weapon weapon;

    PlayerEquipment(){
        defence = 0;
        weapon = new Weapon("Cooper Sword", 5);
        armor = null;
    }
    public int getDefence() {
        return defence;
    }
    public void setArmor(Armor newArmor){
        armor = newArmor;
        defence = newArmor.defence();
    }
    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }
    public int attack(){return weapon.damage();
    }
    public void Reset(){
        defence = 0;
        weapon = new Weapon("Cooper Sword", 5);
        armor = null;
    }

    public Weapon getWeapon() {
        return weapon;
    }
}
