package com.monsters;

public class MonsterFactory {

    public MonsterFactory(){

    }
    public Monster MonsterFac(int damage, int health, String name, int number){
        return new Monster(name, damage, health, number);
    }
}
/*

 */

