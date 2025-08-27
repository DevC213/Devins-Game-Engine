package com.gamelogic.core;

import com.gamelogic.gameflow.Adventure;
import com.gamelogic.commands.IGuiEventListener;
import com.gamelogic.gameflow.ClassController;
import com.monsters.Monster;
import com.recoveryitems.RecoveryItem;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.util.Objects;

public class MainGameController implements IGuiEventListener{

    public TextField health;
    public TextField defence;
    public TextField weapon;
    public GridPane mainGrid;
    public StackPane miniMap;
    private MiniMapCanvas miniMapCanvas;
    public TextField money;
    public ComboBox<String> characterSelection;
    public ComboBox<String> difficulty;
    public GridPane enemySelection;
    public GridPane healthItems;
    public Button AOEAttack;
    public Button singleAttack;
    public ComboBox<Monster> enemyList;
    public ComboBox<RecoveryItem> healthItemList;
    @FXML
    private TextField cords;
    @FXML
    private TextArea inventory;
    @FXML
    private TextArea script;
    @FXML
    private Button start;
    private Adventure adventure;
    private boolean activated = false;
    private boolean gameOver = false;
    private Difficulty difficultySelected;

    public void giveClass(){
        ClassController.mainGameController = this;
    }

    @FXML
    public void gameStart() {

        if (adventure == null) {
            adventure = Adventure.getAdventure();
        }

        if (activated) {
            adventure.resetGame();
            gameOver = false;
        } else {
            initializeMinimap();
            adventure.startGame();
            activated = true;
        }
        String character = characterSelection.getValue();
        String difficulty = this.difficulty.getValue();
        if (character == null) {
            character = "Male";
        }
        if (difficulty == null) {
            difficulty = "Normal";
        }
        switch (difficulty.toLowerCase()) {
            case "normal" -> this.difficultySelected = Difficulty.NORMAL;
            case "daredevil" -> this.difficultySelected = Difficulty.HARDCORE;
            default -> throw new IllegalArgumentException("Invalid difficulty");
        }
        activateFields();
        bindHealingItems(adventure.getRecoveryItems());
        adventure.setCharacterID(character);
        adventure.setHealth();
        script.clear();
        adventure.intro();
        start.setVisible(false);
        Platform.runLater(() -> script.setScrollTop(0));
    }
    @FXML
    private void activateFields() {
        characterSelection.setVisible(false);
        inventory.setVisible(true);
        health.setVisible(true);
        defence.setVisible(true);
        cords.setVisible(true);
        weapon.setVisible(true);
        miniMap.setVisible(true);
        money.setVisible(true);
        difficulty.setVisible(false);
        enemySelection.setVisible(true);
        healthItems.setVisible(true);
        Platform.runLater(() -> script.setScrollTop(0));
    }
    private void initializeMinimap() {
        miniMapCanvas = new MiniMapCanvas();
        miniMap.getChildren().clear();
        miniMap.getChildren().add(miniMapCanvas);

        StackPane.setMargin(miniMapCanvas, Insets.EMPTY);

        miniMapCanvas.prefWidthProperty().bind(miniMap.widthProperty());
        miniMapCanvas.prefHeightProperty().bind(miniMap.heightProperty());
    }

    @FXML
    @Override
    public void GameOver(boolean victory) {
        script.clear();
        if (victory) {
            script.appendText("""
                    You have managed to free this island and its inhabitants from the Void's Avatar.
                    Congratulations!
                    
                    Thank you for playing my game. Press start to begin again.""");
            toggleItems();
        } else if (difficultySelected.endGame()) {
            script.appendText("Game Over, press start to begin again.");
            if (!gameOver) {
                toggleItems();
            }
        } else {
            adventure.respawn();
        }
    }
    private void toggleItems(){
        start.setVisible(true);
        characterSelection.setVisible(true);
        inventory.setVisible(false);
        health.setVisible(false);
        defence.setVisible(false);
        cords.setVisible(false);
        weapon.setVisible(false);
        miniMap.setVisible(false);
        money.setVisible(false);
        difficulty.setVisible(true);
        enemySelection.setVisible(false);
        healthItems.setVisible(false);
        gameOver = true;
    }
    @Override
    public void UIUpdate(String message, int box) {
        //box: 0 -> script, 1 -> inventory, 2->cords, 3-> health, 4-> defence, 5-> weapon, 6-> money
        String rtn = message + "\n";
        switch (box) {
            case 0 -> {
                script.appendText(rtn);
                script.positionCaret(script.getText().length());
            }
            case 1 -> {
                inventory.clear();
                inventory.appendText(rtn);
            }
            case 2 -> cords.setText(rtn);
            case 3 -> health.setText(rtn);
            case 4 -> defence.setText(rtn);
            case 5 -> weapon.setText(rtn);
            case 6 -> money.setText(rtn);
            default -> {
            }
        }
    }

    public void modifyImage(final int row, final int col, final String imagePath) {
        try {
            miniMapCanvas.setImage(col, row, new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath))));
        } catch (Exception e) {
            UIUpdate(e.getMessage(), 0);
        }
    }

    public void modifyImage(final int row, final int col, final Image image) {
        try {
            miniMapCanvas.setImage(col, row, image);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void scroll() {
        script.positionCaret(0);
    }

    public void enableEnemy(ObservableList<Monster> monsters) {
        enemyList.setItems(monsters);

        enemyList.setCellFactory(_ -> new ListCell<>() {
            private final ChangeListener<Number> healthListener = (_, _, _) -> updateItem(getItem(), false);

            @Override
            protected void updateItem(Monster monster, boolean isEmpty) {
                if (getItem() != null) {
                    getItem().healthProperty().removeListener(healthListener);
                }
                super.updateItem(monster, isEmpty);
                if (isEmpty || monster == null) {
                    setText(null);
                } else {
                    setText(monster.toString());
                }
            }
        });

        enemyList.setDisable(false);
        singleAttack.setDisable(false);
        AOEAttack.setDisable(false);
    }
    public void bindHealingItems(ObservableList<RecoveryItem> recoveryItems) {
        healthItemList.setItems(recoveryItems);
    }

    public void useHealingItem(){
        RecoveryItem item = healthItemList.getValue();
        if(item == null){
            script.appendText("no Item selected.");
            script.positionCaret(script.getText().length());
        } else {
            adventure.useHealingItem(item);
        }
    }
    public void attackMonster(){
        Monster monster = enemyList.getValue();
        if(monster == null){
            script.appendText("No monster was selected.\n");
        }
        enemyList.getSelectionModel().clearSelection();
        adventure.Attack(monster);
        if(enemyList.getItems().isEmpty()){
            disableEnemy();
        }
    }
    public void AOE(){
        enemyList.getSelectionModel().clearSelection();
        adventure.AOE();
        if(enemyList.getItems().isEmpty()){
            disableEnemy();
        }

    }
    public void disableEnemy(){
        enemyList.setDisable(true);
        singleAttack.setDisable(true);
        AOEAttack.setDisable(true);

    }
}