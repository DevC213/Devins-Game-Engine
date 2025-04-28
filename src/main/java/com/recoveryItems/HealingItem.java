package com.recoveryItems;

public class HealingItem {
    final String name;
    final int healing;
    final String bonus;

    public HealingItem(String name, int healing) {
        this.name = name;
        this.healing = healing;
        this.bonus = null;
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
