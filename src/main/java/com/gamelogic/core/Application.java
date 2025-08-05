package com.gamelogic.core;

import com.gamelogic.gameflow.Adventure;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Objects;

public class Application extends javafx.application.Application {

    Adventure adventure;
    @Override
    public void start(final Stage stage) {
        final float defaultV = 1000f;
        final float defaultV1 = 700f;
        final String folderPath = "/com/gameData/";
        adventure = Adventure.getAdventure();
        FXMLLoader mainGame = new FXMLLoader(Application.class.getResource(folderPath + "mainGame.fxml"));
        FXMLLoader pauseMenu = new FXMLLoader(Application.class.getResource(folderPath + "pauseMenu.fxml"));
        PauseMenuController pauseMenuController;
        MainGameController mainGameController;

        Parent game = null;
        Parent pause = null;
        Scene scene = null;
        try {
            game = mainGame.load();
            pause = pauseMenu.load();
            mainGameController = mainGame.getController();
            pauseMenuController = pauseMenu.getController();
            pauseMenuController.setAdventure();
            pauseMenuController.linkViews(game);
            mainGameController.giveClass();
            StackPane root = new StackPane();
            pause.setVisible(false);
            root.getChildren().addAll(game, pause);
            scene = new Scene(root, defaultV, defaultV1);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(folderPath + "game.css")).toExternalForm());
            stage.setTitle("Island Adventure");
            stage.setScene(scene);
            stage.show();
            showInstructions();
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        assert scene != null;
        Parent finalPause = pause;
        Parent finalGame = game;

        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.F1) {
                showInstructions();
            } else if (event.getCode() == KeyCode.F2) {
                finalPause.setVisible(true);
                finalGame.setVisible(false);
            } else {
                String keyPressed = String.valueOf(event.getCode());
                adventure.commandProcessor(keyPressed);
            }
        });

    }
    private void showInstructions(){

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Instructions");
        alert.setHeaderText("How to Play: ");
        alert.setContentText(
                """
                Press B to grab item.
                Press C then enter name of healing item to use.
                Press V to attack, and then enter monster name.
                Will auto attack when only one monster.
               
                Press enter after item or monster name.
                
                If multiple monsters of the same name are on tile:
                Enter name + number, ex: Zombie #2, or Goblin #4.
                
                Use X to explore area: whether it is a cave, house or dungeon
                
                Shown keys are default values, change values in keyBindings.json
                
                Use arrow keys for movement.
                
                Player information is on the side of map.
                Max health increases each level you descend,
                and as you kill monsters.
                
                Normal: Respawn on death
                Daredevil: Permadeath
                Both settings will allow you to save your progress.
                However, daredevil will delete your save upon death.
                
                Press F1 to view again.
                """
        );
        alert.showAndWait();
    }
    public static void main(final String[] args) {launch();}
}
