package com.adventure_logic;

import com.adventure_logic.MapLogic.MapController;
import com.adventure_logic.PlayerLogic.PlayerController;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class UIMapController {

    private int visibility = 2;
    private final int[] SQUARE_CHANGE = {-2, -1, 0, 1, 2};
    public UIMapController() {}

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }
    public void minimap(Controller controller, MapController mapController, PlayerController playerController) {
        for (int j = 0; j < SQUARE_CHANGE.length; j++) {
            for (int k = 0; k < SQUARE_CHANGE.length; k++) {
                if(Math.abs(SQUARE_CHANGE[j]) > visibility || Math.abs(SQUARE_CHANGE[k]) > visibility ){
                    controller.modifyImage(k,j,mapController.getImage("?"));
                } else if (SQUARE_CHANGE[j] == 0 && SQUARE_CHANGE[k] == 0) {
                    try {
                        controller.modifyImage(k, j, createBlend(playerController, mapController));
                    } catch (IOException e) {
                        controller.UIUpdate(e.getMessage(), 0);
                        throw new RuntimeException(e);
                    }
                } else {

                    controller.modifyImage(k, j, mapController.getImage(mapController.getMapValue(playerController.getCords()[0]
                            + SQUARE_CHANGE[j],playerController.getCords()[1] + SQUARE_CHANGE[k])));
                }
            }
        }
    }
    private Image createBlend(final PlayerController plays, MapController mapController) throws IOException {
        BufferedImage player;
        BufferedImage tile;
        BufferedImage blend;
        int imageWidth;
        int imageHeight;
        Graphics merger;

        String valAtPlayer = mapController.getMapValue(plays.getCords()[0], plays.getCords()[1]);
        // Correct way to load from resources
        player = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(mapController.getImage("1"))));
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
}
