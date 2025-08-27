package com.gamelogic.core;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;

public class MiniMapCanvas extends Region {

    private static final int SIZE = 5;
    private final Canvas canvas = new Canvas();
    private final Image[][] tiles = new Image[SIZE][SIZE];

    public MiniMapCanvas() {
        getChildren().add(canvas);
        setSnapToPixel(true);

        widthProperty().addListener((_, _, _) -> resizeCanvas());
        heightProperty().addListener((_, _, _) -> resizeCanvas());
    }

    private void resizeCanvas() {
        double width = Math.floor(getWidth());
        double height = Math.floor(getHeight());
        canvas.setWidth(width);
        canvas.setHeight(height);
        redrawCanvas();
    }

    private void redrawCanvas() {
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        if (width <= 0 || height <= 0) return;
        double dWidth = Math.floor(width / SIZE);
        double dHeight = Math.floor(height / SIZE);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, dWidth, dHeight);
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Image image = tiles[i][j];
                if (image != null){
                    gc.drawImage(image, i*dWidth, j*dHeight, dWidth, dHeight);
                }
            }
        }
    }
    public void setImage(int col, int row, Image image) {
        tiles[col][row] = image;
        redrawCanvas();
    }
    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        canvas.setLayoutX(0);
        canvas.setLayoutY(0);
    }
    @Override
    public boolean isResizable(){
        return true;
    }
}
