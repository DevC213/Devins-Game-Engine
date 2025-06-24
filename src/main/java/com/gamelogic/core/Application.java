package com.gamelogic.core;

import com.gamelogic.gameflow.Adventure;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class Application extends javafx.application.Application {

    Adventure adventure;
    @Override
    public void start(final Stage stage) {
        final int defaultV = 700;
        final int defaultV1 = 500;
        adventure = Adventure.getAdventure();
        FXMLLoader fxmlLoader = new FXMLLoader(Application
                .class.getResource("/com/gamelogic/mainGame.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), defaultV, defaultV1);
            stage.setResizable(false);
            stage.setTitle("Island Adventure");
            stage.setScene(scene);
            stage.show();
            showInstructions();
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        assert scene != null;
        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.F1) {
                showInstructions();
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
                Press B then enter item name to pick up item.
                Press C then enter name of healing item to use.
                Press V to attack, and then enter monster name.
               
                Press enter after item or monster name.
                
                If multiple monsters of the same name are on tile:
                Enter name + number, ex: Zombie #2, or Goblin #4.
                
                Use Z to enter cave, and X to climb ladder.
                Use arrow keys for movement.
                
                Player information is on the side of map.
                Max health increases each level you descend.
     
                Press F1 to view again.
                """
        );
        alert.showAndWait();
    }
    public static void main(final String[] args) {launch();}
}
