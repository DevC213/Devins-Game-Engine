package com.Monsters;

public class MonsterFactory {

    public MonsterFactory(){

    }
    public Monster MonsterFac(int damage, int health, String name, int number){
        switch (name.toLowerCase()) {
            case "goblin" -> {
                return new Goblin(damage, health,number);
            }
            case "skeleton" -> {
                return new Skeleton(damage, health,number);
            }
            case "zombie" -> {
                return new Zombie(damage, health,number);
            }
        }
        return null;
    }
}
