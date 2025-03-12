package com.Monsters;

public class MonsterFactory {

    public MonsterFactory(){

    }
    public Monster MonsterFac(int damage, int health, String name){
        switch (name.toLowerCase()) {
            case "goblin" -> {
                return new Goblin(damage, health);
            }
            case "skeleton" -> {
                return new Skeleton(damage, health);
            }
            case "zombie" -> {
                return new Zombie(damage, health);
            }
        }
        return null;
    }
}
