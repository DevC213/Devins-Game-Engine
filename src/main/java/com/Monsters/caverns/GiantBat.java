package com.Monsters.caverns;

import com.Monsters.Monster;

public class GiantBat implements Monster {

    int health;
    int damage;
    String name;
    int number;


    public GiantBat(int damage, int health, int number) {
        this.health = health;
        this.damage = damage;
        this.name = "Giant Bat";
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