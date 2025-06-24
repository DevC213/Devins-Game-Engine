package com.monsters.theDarkness;

import com.monsters.Monster;

public class OrbOfDarkness implements Monster {

    int health;
    int damage;
    String name;
    int number;


    public OrbOfDarkness(int damage, int health, int number) {
        this.health = health;
        this.damage = damage;
        this.name = "Orb of Darkness";
        this.number = number;
    }

    @Override
    public double getBaseAttack() {
        return damage;
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFullName() {
        return name + " #" + number;
    }
    @Override
    public void setHealth(int health) {
        this.health -= health;
    }

    @Override
    public void onKill() {

    }

    @Override
    public void attack(int damage) {
        health -= damage;
    }
}