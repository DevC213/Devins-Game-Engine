package com.adventure_logic;

public interface GuiEventListener {
    void UIUpdate(String message, int box);
    void GameOver();
    void clearInput();
}
