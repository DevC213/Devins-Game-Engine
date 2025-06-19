package com.recoveryItems;

public class HealingItem {
    final String name;
    final int healing;

    public HealingItem(String name, int healing) {
        this.name = name;
        this.healing = healing;
    }

    public String getName() {
        return name;
    }
    public int getHealValue() {
        return healing;
    }

    public int use() {
        return healing;
    }

}
