package com.monsters.caverns;

import com.monsters.Monster;

public class WitheredSkeleton implements Monster {

    int health;
    int damage;
    String name;
    int number;


    public WitheredSkeleton(int damage, int health, int number) {
        this.health = health;
        this.damage = damage;
        this.name = "Withered Skeleton";
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