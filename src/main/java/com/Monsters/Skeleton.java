package com.Monsters;

public class Skeleton implements Monster{
    int damage;
    int health;
    String name;
    int number;

    public Skeleton(int damage, int health, int number) {
        this.health = health;
        this.damage = damage;
        this.name = "Skeleton";
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
    public void setHealth(int damage) {

    }

    @Override
    public void onKill() {

    }

    @Override
    public void attack(int damage) {
        health -= damage;
    }
}
