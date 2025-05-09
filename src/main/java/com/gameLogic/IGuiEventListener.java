package com.gameLogic;

public interface IGuiEventListener {
    void UIUpdate(String message, int box);
    void UIUpdate(Messenger messenger, int box);
    void GameOver(boolean victory);
    void clearInput();
}
