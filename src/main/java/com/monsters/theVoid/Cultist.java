package com.monsters.theVoid;

import com.monsters.Monster;

public class Cultist implements Monster {

    int health;
    int damage;
    String name;
    int number;

    public Cultist(int health, int damage, int number) {
        this.health = health;
        this.damage = damage;
        this.number = number;
        name = "Cultist";
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
    public void setHealth(int damage) {
        health = damage;
    }

    @Override
    public void onKill() {

    }

    @Override
    public void attack(int damage) {
        health -= damage;
    }
}
