package com.monsters;

public interface Monster {


    double getBaseAttack();
    int getHealth();
    String getName();
    String getFullName();
    void setHealth(int damage);
    void onKill();
    void attack(int damage);
}
