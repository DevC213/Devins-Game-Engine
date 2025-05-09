package com.gameLogic;

public interface IUpdateMinimap {
    void renderMinimap();
    void setVisibility(int visibility);
    void setDirection(int deltaX, int deltaY);
}
