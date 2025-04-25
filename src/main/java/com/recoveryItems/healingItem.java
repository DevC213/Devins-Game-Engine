package com.recoveryItems;

public class healingItem {
    final String name;
    final int healing;
    final String bonus;

    public healingItem(String name, int healing) {
        this.name = name;
        this.healing = healing;
        this.bonus = null;
    }
    public healingItem(String name, int healing, String bonus) {
        this.name = name;
        this.healing = healing;
        this.bonus = bonus;
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
    public String getBonus() {return bonus;}
}
