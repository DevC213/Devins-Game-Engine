package com.gamelogic.core;

import com.gamelogic.gameflow.Adventure;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;

public class PauseMenuController {
    public Button LoadGame;
    public Button Resume;
    public Button SaveGame;
    public Button Quit;

    @FXML
    public GridPane gridPane;
    public TextArea activeQuests;

    private Node mainGame;
    private Adventure adventure;

    @FXML
    private void QuitGame(){
        Platform.exit();
    }

    @FXML
    private void Resume(){
        gridPane.setVisible(false);
        if(mainGame != null){
            mainGame.setVisible(true);
            mainGame.setDisable(false);
        }
    }
    @FXML
    public void linkViews(Node main){
        mainGame = main;
    }
    @FXML
    public void setAdventure(){
        if(adventure == null){
            adventure = Adventure.getAdventure();
        }
    }
    @FXML
    private void saveGame(){
        adventure.saveGame();
    }
    @FXML
    private void loadGame(){
        adventure.loadGame();
    }

}
