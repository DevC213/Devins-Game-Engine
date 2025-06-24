package com.monsters.overworld;

import com.monsters.Monster;

public class Goblin implements Monster {

    int health;
    int damage;
    String name;
    int number;


    public Goblin(int damage, int health, int number) {
        this.health = health;
        this.damage = damage;
        this.name = "Goblin";
        this.number = number;
    }

    @Override
    public double getBaseAttack() {
        return damage * 1.5;
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
