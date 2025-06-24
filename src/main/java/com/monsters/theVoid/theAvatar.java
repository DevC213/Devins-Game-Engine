package com.monsters.theVoid;

import com.monsters.Monster;

public class theAvatar implements Monster {

    int health;
    int damage;
    String name;

    public theAvatar(int health, int damage) {
        this.health = health;
        this.damage = damage;
        name = "Avatar of the Void";
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
        return name;
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
        health-=damage;
    }
}
