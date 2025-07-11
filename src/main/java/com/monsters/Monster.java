package com.monsters;

public class Monster {

    int health;
    int damage;
    String name;
    int number;

    Monster(String name, int damage, int health, int number) {
        this.health = health;
        this.damage = damage;
        this.name = name;
        this.number = number;
    }


    public double getBaseAttack(){
        return damage;
    }
    public int getHealth(){
        return health;
    }
    public String getName(){
        return name;
    }
    public String getFullName(){
        return name + " #" + number;
    }
    public void setHealth(int damage){
        this.health = damage;
    }
    public void attack(int damage) {
        health -= damage;
    }
}
