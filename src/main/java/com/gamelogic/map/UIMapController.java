package com.gamelogic.map;

import com.gamelogic.core.Controller;
import com.gamelogic.core.TileKeyRegistry;
import com.gamelogic.map.mapLogic.MapController;
import com.gamelogic.playerlogic.PlayerController;
import com.gamelogic.playerlogic.Character;
import com.gamelogic.villages.House;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.List;

public class UIMapController {

    private int visibility = 2;
    private String direction = "down";
    private String characterID;
    private final Map<String, Character> characterMap;
    public UIMapController() {
        characterMap = new HashMap<>();
    }
    public void setCharacterID(String gender) {
        characterID = gender + "One";
    }
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

                if ((Math.abs(deltaX) > visibility || Math.abs(deltaY) > visibility) && mapController.usesFog()) {
                    controller.modifyImage(row, column, getFilePath("?", mapController.getTheme()));
                } else if (deltaX == 0 && deltaY == 0) {
                    try {
                        controller.modifyImage(row, column, overlayPlayer(playerController, mapController));
                    } catch (IOException e) {
                        controller.UIUpdate(e.getMessage(), 0);
                    }
                } else {
                    String tileID = mapController.getMapValue(new Coordinates(mapColumn, mapRow));
                    controller.modifyImage(row, column, getFilePath(tileID, mapController.getTheme()));
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
        String currTheme = mapController.getTheme();

        player = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(characterMap.get(characterID).directions().get(direction))));
        tile = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(getFilePath(valAtPlayer, currTheme))));

        imageWidth = Math.max(player.getWidth(), tile.getWidth());
        imageHeight = Math.max(player.getHeight(), tile.getHeight());

        blend = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

        merger = blend.getGraphics();
        merger.drawImage(tile, 0, 0, null);
        merger.drawImage(player, 0, 0, null);
        merger.dispose();

        return SwingFXUtils.toFXImage(blend, null);
    }
    public void setDirection(String direction) {
        this.direction = direction;
    }
    public void processCharacters(String filePath){
        try {
            Gson gson = new Gson();
            InputStream input = Objects.requireNonNull(getClass().getResourceAsStream(filePath));
            InputStreamReader reader = new InputStreamReader(input);
            Type listType = new TypeToken<java.util.List<Character>>() {}.getType();
            List<Character> tempSkinList = gson.fromJson(reader, listType);
            for(Character character : tempSkinList) {
                characterMap.put(character.name(), character);
            }
        } catch (Exception e) {
            throw  new RuntimeException("Error processing player's skins file");
        }
    }
    private String getFilePath(String tile, String theme){
        TileKey tileKey = TileKeyRegistry.getTileKey(tile);
        if(tileKey == null){
            throw new IllegalArgumentException("Tile Key not found: " + tile);
        }
       return tileKey.resolvePath(theme);
    }

    public int getPlayerHealth() {
        return characterMap.get(characterID).startingHP();
    }

}
