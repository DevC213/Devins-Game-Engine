package com.Armor;

public class Greaves implements Armor{

    private final int defence;
    private final String name;

    Greaves(int defence, String name) {
        this.defence = defence;
        this.name = name;
    }

    @Override
    public int getDefence() {
        return defence;
    }
    @Override
    public String getName() {return name;}
}
