package com.recovery_items;

public class bread implements RecoveryItem {

    final String name = "bread";
    final int healing = 15;
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
