package com.gamelogic.commands;

import com.gamelogic.messaging.Messenger;

public interface IGuiEventListener {
    void UIUpdate(String message, int box);
    void GameOver(boolean victory);
}
