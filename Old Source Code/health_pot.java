package com.recovery_items;

public class health_pot implements RecoveryItem {

    final String name = "health pot";
    final int healing = 20;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getHealValue() {
        return healing;
    }

    @Override
    public int use() {
        return healing;
    }
}
