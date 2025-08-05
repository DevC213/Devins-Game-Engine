package com.monsters;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Monster {

    int health;
    int damage;
    String name;
    int number;
    StringProperty nameProperty = new SimpleStringProperty();
    IntegerProperty healthProperty = new SimpleIntegerProperty();


    Monster(String name, int damage, int health, int number) {
        this.health = health;
        this.damage = damage;
        this.name = name;
        this.number = number;
        nameProperty.set(name);
        healthProperty.set(health);
    }
    public double getBaseAttack(){
        return damage;
    }


    public String getFullName(){
        return name + " #" + number;
    }
    public void setHealth(int damage){
        this.health = damage;
    }
    public void attack(int damage) {
        health -= damage;
        healthProperty.set(health);
    }

    @Override
    public String toString(){
        return getFullName() + ": health - " + getHealth();
    }

    public String getName(){
        return nameProperty.get();
    }
    public StringProperty nameProperty(){
        return nameProperty;
    }
    public int getHealth(){
        return healthProperty.get();
    }
    public IntegerProperty healthProperty(){
        return healthProperty;
    }


}
