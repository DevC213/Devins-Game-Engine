package com.gameLogic.MapLogic;

public interface ICanCross {
    boolean isWalkable(final String terrain);
    boolean isLadder(final String terrain);
    boolean isCave(final String terrain);
}
