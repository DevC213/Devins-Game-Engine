package com.Monsters;

public class Zombie implements Monster{

    int damage;
    int health;
    String name;

    public Zombie(int damage, int health) {
        this.damage = damage;
        this.health = health;
        name = "Zombie";
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
