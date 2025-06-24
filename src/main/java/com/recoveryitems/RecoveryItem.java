package com.recoveryitems;

public class RecoveryItem {
    final String name;
    final int healing;

    public RecoveryItem(String name, int healing) {
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
