package com.Monsters.theDarkness;

import com.Monsters.Monster;

public class DarkenedWisp implements Monster {

    int health;
    int damage;
    String name;
    int number;


    public DarkenedWisp(int damage, int health, int number) {
        this.health = health;
        this.damage = damage;
        this.name = "Darkened Wisps";
        this.number = number;
    }

    @Override
    public double getBaseAttack() {
        return damage * 1.2;
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