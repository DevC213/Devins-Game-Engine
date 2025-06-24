package com.gamelogic.map;

public interface IUpdateMinimap {
    void renderMinimap();
    void setVisibility(int visibility);
    void setDirection(int deltaX, int deltaY);
    void toggleHouse();
}
