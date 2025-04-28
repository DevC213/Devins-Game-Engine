package com.adventure_logic;

public interface IGuiEventListener {
    void UIUpdate(String message, int box);
    void UIUpdate(Messenger messenger, int box);
    void GameOver();
    void clearInput();

}
