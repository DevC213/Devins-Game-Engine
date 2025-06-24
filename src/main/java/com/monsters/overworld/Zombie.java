package com.monsters.overworld;

import com.monsters.Monster;

public class Zombie implements Monster {

    int damage;
    int health;
    String name;
    int number;

    public Zombie(int damage, int health, int number) {
        this.health = health;
        this.damage = damage;
        this.name = "Zombie";
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
