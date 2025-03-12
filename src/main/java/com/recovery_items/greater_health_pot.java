package com.recovery_items;

public class greater_health_pot implements RecoveryItem {

    final String name = "greater healing pot";
    final int healing = 30;

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
