package com.monsters;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.List;

public class Boss {

    final int initialHealth;
    int health;
    int damage;
    String name;
    int number;
    final int percentages;
    int currentStage = 0;
    List<String> quotes;
    List<String> voices;
    StringProperty nameProperty = new SimpleStringProperty();
    IntegerProperty healthProperty = new SimpleIntegerProperty();



    Boss(String name, int damage, int health, int number) {
        this.initialHealth = this.health = health;
        this.damage = damage;
        this.name = name;
        this.number = number;
        nameProperty.set(name);
        healthProperty.set(health);

        int quotes = this.quotes.size();
        if(quotes > 0) {
            percentages = 100/quotes/100;
        } else{
            percentages = 0;
        }
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

        if(percentages != 0) {
            if (health < initialHealth * currentStage){
                System.out.println(quotes.get(currentStage/percentages));
                currentStage += percentages;
            }
        }
        if(health < 0){
            System.out.println(name + " is defeated");
        }
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
