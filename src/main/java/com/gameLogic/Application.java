package com.gameLogic;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Application extends javafx.application.Application {


    Adventure adventure;
    @Override
    public void start(final Stage stage){
        final int defaultV = 700;
        final int defaultV1 = 500;
        adventure = Adventure.getAdventure();
        FXMLLoader fxmlLoader = new FXMLLoader(Application
                .class.getResource("layout.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), defaultV, defaultV1);
            stage.setResizable(false);
            stage.setTitle("Adventure Game");
            stage.setScene(scene);
            stage.show();
            adventure.launchGame();

        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        assert scene != null;
        scene.setOnKeyReleased(event -> {
            String keyPressed = String.valueOf(event.getCode());
            adventure.commandProcessor(keyPressed);
        });
    }
    public static void main(final String[] args) {launch();}
}
