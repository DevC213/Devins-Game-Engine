package com.gamelogic.core;

import com.gamelogic.gameflow.Adventure;
import com.gamelogic.commands.IGuiCommandGetter;
import com.gamelogic.commands.IGuiEventListener;
import com.gamelogic.gameflow.ClassController;
import com.gamelogic.messaging.Messenger;
import com.monsters.Monster;
import com.recoveryitems.RecoveryItem;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.util.Objects;

public class MainGameController implements IGuiEventListener, IGuiCommandGetter {

    public TextField health;
    public TextField defence;
    public TextField weapon;
    public GridPane mainGrid;
    public GridPane miniMap;
    private static final int SIZE = 5;
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
    private TextField commandInput;
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
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    ImageView img = new ImageView();
                    img.fitWidthProperty().bind(miniMap.widthProperty().divide(SIZE));
                    img.fitHeightProperty().bind(miniMap.heightProperty().divide(SIZE));
                    img.setPreserveRatio(false);
                    miniMap.add(img, i, j);
                }
            }

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
        commandInput.setVisible(true);
        commandInput.setEditable(true);
        weapon.setVisible(true);
        miniMap.setVisible(true);
        money.setVisible(true);
        difficulty.setVisible(false);
        enemySelection.setVisible(true);
        healthItems.setVisible(true);
        Platform.runLater(() -> script.setScrollTop(0));
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
        commandInput.setVisible(false);
        commandInput.setEditable(false);
        weapon.setVisible(false);
        miniMap.setVisible(false);
        money.setVisible(false);
        difficulty.setVisible(true);
        enemySelection.setVisible(false);
        healthItems.setVisible(false);
        gameOver = true;
    }
    @Override
    public void clearInput() {
        commandInput.clear();
    }

    @Override
    public void UIUpdate(String message, int box) {
        //box: 0 -> script, 1 -> inventory, 2->cords, 3-> health, 4-> defence
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
    @Override
    public void UIUpdate(Messenger message, int box) {
        //box: 0 -> script, 1 -> inventory, 2->cords, 3-> health, 4-> defence
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
            case 4 -> defence.setText(message.getArmor().name() + ": " + message.getArmor().defence());
            case 5 -> weapon.setText(message.getWeapon().name() + ": " + message.getWeapon().damage());
            default -> {
            }
        }
    }

    public void modifyImage(final int row, final int col, final String imagePath) {
        try {
            for (javafx.scene.Node node : miniMap.getChildren()) {
                Integer r = GridPane.getRowIndex(node);
                Integer c = GridPane.getColumnIndex(node);
                r = (r == null ? 0 : r);
                c = (c == null ? 0 : c);
                if (r == row && c == col && node instanceof ImageView) {
                    ((ImageView) node).setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath))));
                    break;
                }
            }

        } catch (Exception e) {
            UIUpdate(e.getMessage(), 0);
        }
    }

    public void modifyImage(final int row, final int col, final Image image) {
        try {
            for (javafx.scene.Node node : miniMap.getChildren()) {
                Integer r = GridPane.getRowIndex(node);
                Integer c = GridPane.getColumnIndex(node);
                r = (r == null ? 0 : r);
                c = (c == null ? 0 : c);

                if (r == row && c == col && node instanceof ImageView) {
                    ((ImageView) node).setImage(image);
                    break;
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public String getCommand() {
        return commandInput.getText();
    }

    public void scroll() {
        script.positionCaret(0);
    }

    public void commandFocus() {
        commandInput.requestFocus();
    }

    public void textAreaFocus() {
        script.requestFocus();
    }

    public void enableEnemy(ObservableList<Monster> monsters) {
        enemyList.setItems(monsters);

        enemyList.setCellFactory(monsterListView -> new ListCell<>() {
            private final ChangeListener<Number> healthListener = (obs, oldVal, newVal) -> updateItem(getItem(), false);

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
            script.appendText("no Monster selected.");
        }
        adventure.Attack(monster);
        if(enemyList.getItems().isEmpty()){
            disableEnemy();
        }
        enemyList.getSelectionModel().clearSelection();
    }
    public void AOE(){
        adventure.AOE();
        if(enemyList.getItems().isEmpty()){
            disableEnemy();
        }
        enemyList.getSelectionModel().clearSelection();
    }
    public void disableEnemy(){
        enemyList.setDisable(true);
        singleAttack.setDisable(true);
        AOEAttack.setDisable(true);

    }
}