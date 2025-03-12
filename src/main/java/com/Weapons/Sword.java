package com.Weapons;

public class Sword implements Weapon{

    String name;
    int damage;

    public Sword(String name, int damage){
        this.name = name;
        this.damage = damage;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getDamage() {
        return damage;
    }
}
