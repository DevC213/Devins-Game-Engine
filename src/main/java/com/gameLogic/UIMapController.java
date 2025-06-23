package com.gameLogic;

import com.gameLogic.MapLogic.MapController;
import com.gameLogic.PlayerLogic.PlayerController;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class UIMapController {

    private int visibility = 2;
    private int direction = 0;
    public UIMapController() {}

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }
    public void minimap(Controller controller, MapController mapController, PlayerController playerController) {
        int mapSize = 5;
        int mapMiddle = mapSize / 2;
        Coordinates playerCoordinates = playerController.getMapCoordinates();
        Coordinates maxCoordinates = mapController.getCoordinates();

        int startColumn = Math.max(0, Math.min(playerCoordinates.x() - mapMiddle, maxCoordinates.x() - mapSize));
        int startRow = Math.max(0, Math.min(playerCoordinates.y() - mapMiddle, maxCoordinates.y() - mapSize));

        for (int column = 0; column < maxCoordinates.y(); column++) {
            for (int row = 0; row < maxCoordinates.x(); row++) {

                int mapColumn = startColumn + column;
                int mapRow = startRow + row;

                int deltaX = mapColumn - playerCoordinates.x();
                int deltaY = mapRow - playerCoordinates.y();

                if (Math.abs(deltaX) > visibility || Math.abs(deltaY) > visibility) {
                    controller.modifyImage(row, column, mapController.getImage("?"));
                } else if (deltaX == 0 && deltaY == 0) {
                    try {
                        controller.modifyImage(row, column, overlayPlayer(playerController, mapController));
                    } catch (IOException e) {
                        controller.UIUpdate(e.getMessage(), 0);
                    }
                } else {
                    String tileID = mapController.getMapValue(new Coordinates(mapColumn, mapRow));
                    controller.modifyImage(row, column, mapController.getImage(tileID));
                }
            }
        }
    }
    private Image overlayPlayer(final PlayerController plays, MapController mapController) throws IOException {
        BufferedImage player;
        BufferedImage tile;
        BufferedImage blend;
        int imageWidth;
        int imageHeight;
        Graphics merger;

        String valAtPlayer = mapController.getMapValue(plays.getMapCoordinates());

        player = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(mapController.getPlayerImage(direction))));
        tile = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(mapController.getImage(valAtPlayer))));

        imageWidth = Math.max(player.getWidth(), tile.getWidth());
        imageHeight = Math.max(player.getHeight(), tile.getHeight());

        blend = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

        merger = blend.getGraphics();
        merger.drawImage(tile, 0, 0, null);
        merger.drawImage(player, 0, 0, null);
        merger.dispose();

        return SwingFXUtils.toFXImage(blend, null);
    }
    public void setDirection(int direction) {
        this.direction = direction;
    }
}
