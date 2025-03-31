package com.recovery_items;

public class Soup implements RecoveryItem {

    private static final String NAME = "Soup";
    private static final int HEALING = 10;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int getHealValue() {
        return HEALING;
    }

    @Override
    public int use() {
        return HEALING;
    }
}
