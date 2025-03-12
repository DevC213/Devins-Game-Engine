package com.Monsters;

public interface Monster {

    double getBaseAttack();
    int getHealth();
    String getName();
    void setHealth(int damage);
    void onKill();
    void attack(int damage);
}
