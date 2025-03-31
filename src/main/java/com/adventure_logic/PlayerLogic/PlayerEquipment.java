package com.adventure_logic.PlayerLogic;

import com.Armor.Armor;
import com.Weapons.Sword;
import com.Weapons.Weapon;

class PlayerEquipment {
    private Armor armor;
    private int defence;
    private Weapon weapon;
    private int extraDamage;

    PlayerEquipment(){
        defence = 0;
        extraDamage = 0;
        weapon = new Sword("Cooper Sword", 5);
        armor = null;
    }
    public int getDefence() {
        return defence;
    }
    public void setArmor(Armor newArmor){
        armor = newArmor;
        defence = newArmor.getDefence();
    }
    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }
    public void setExtraDamage(int extraDamage) {
        this.extraDamage = extraDamage;
    }
    public int attack(){return extraDamage + weapon.getDamage();
    }
    /**
     * Sets defence
     * @param defence item defence
     */
    public void setDefence(int defence){
        this.defence = Math.max(this.defence + (this.defence - defence), 0);
    }
    public void Reset(){
        defence = 0;
        weapon = new Sword("Cooper Sword", 5);
        armor = null;
    }
}
