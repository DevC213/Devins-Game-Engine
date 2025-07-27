package com.gamelogic.gameflow;

import com.gamelogic.commands.Keybindings;
import com.gamelogic.core.MainGameController;
import com.gamelogic.core.MapRegistry;
import com.google.gson.Gson;
import com.savesystem.GameState;

import java.io.InputStream;
import java.io.InputStreamReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class Adventure {

    private static Adventure adventure = getAdventure();
    public GameController gameController;

    //Constructor/initialization methods
    private Adventure() {
    }
    public static synchronized Adventure getAdventure(){
        if (adventure == null){
            adventure = new Adventure();
        }
        return adventure;
    }

    public void startGame(final MainGameController control) {
        gameController = new GameController(control, readCustomBindings());
    }
    public void setCharacterID(String characterID) {
        gameController.setCharacter(characterID);
    }
    //Game progression controls
    public void intro() {
        gameController.intro();
    }
    public void resetGame(){
        gameController.resetGame();
    }

    //Facade functions
    public void commandProcessor(final String direction) {
        gameController.handleInput(direction);
   }

    public void setHealth() {
        gameController.setHealth();
    }
    private Keybindings readInKeyBindings(){

        try {
            Gson gson = new Gson();
            InputStream input = Objects.requireNonNull(getClass().getResourceAsStream("/keyBindings.json"));
            InputStreamReader reader = new InputStreamReader(input);
            return gson.fromJson(reader, Keybindings.class);
        } catch (Exception e) {
            return new Keybindings("v","c","x","b");
        }
    }
    private Keybindings readCustomBindings() {
        String saveDir = getSystemPath();
        Keybindings kb = readInKeyBindings();
        if (!Files.exists(Paths.get(saveDir + "/keyBindings.json"))) {
            try {
                Gson gson = new Gson();
                String json = gson.toJson(kb);
                Path savePath = Paths.get(saveDir, "keyBindings.json");
                Files.createDirectories(savePath.getParent());
                Files.write(savePath, json.getBytes());
                Files.write(Paths.get("keyBindings.json"), json.getBytes());
            } catch (Exception e) {
                System.out.println("Error saving keybindings");
                kb = new Keybindings("v","c","x","b");
            }
        } else {
            try {
                Gson gson = new Gson();
                String json = Files.readString(Paths.get(getSystemPath() + "/keyBindings.json"));
                kb = gson.fromJson(json, Keybindings.class);
            } catch (Exception e) {
                System.out.println("Error loading keybindings");
                kb = new Keybindings("v","c","x","b");
            }
        }
        return kb;
    }
    public void respawn() {
        gameController.respawn();
    }

    public void saveGame() {
        GameState gameState = new GameState();
        gameState.playerState =  gameController.getPlayerState();
        gameState.mapStates = MapRegistry.getMapStates();
        gameState.currentMapID = gameController.getID();
        gameState.level = gameController.getLevel();
        gameState.deepestLevel = gameController.getDeepestLevel();
        gameState.mainMapLevel = MapRegistry.getMapController(0).getLevel();
        String saveDir = getSystemPath();
        try {
            Gson gson = new Gson();
            String json = gson.toJson(gameState);
            Path savePath = Paths.get(saveDir, "savegame.json");
            Files.createDirectories(savePath.getParent());
            Files.write(savePath, json.getBytes());
            Files.write(Paths.get("savegame.json"), json.getBytes());
        } catch (Exception e){
            System.out.println("Error saving game");
        }
    }
    public void loadGame() {

        try{
            Gson gson = new Gson();
            String json = Files.readString(Paths.get(getSystemPath() + "/savegame.json"));
            GameState gameState = gson.fromJson(json, GameState.class);
            MapRegistry.loadData(gameState.mapStates);
            gameController.loadMapFromID(gameState.currentMapID);
            gameController.setLevel(gameState.level);
            gameController.setDeepestLevel(gameState.deepestLevel);
            gameController.loadData(gameState.playerState);
            MapRegistry.getMapController(0).setLevel(gameState.mainMapLevel);
        } catch(Exception e){
            System.out.println("Error loading Game Data");
        }
    }
    private String getSystemPath(){
        String saveDir;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            saveDir = System.getenv("APPDATA") + "\\IslandAdventure";
        } else if (os.contains("mac")) {
            saveDir = System.getProperty("user.home") + "/Library/Application Support/IslandAdventure";
        } else {
            saveDir = System.getProperty("user.home") + "/.islandadventure";
        }
        return saveDir;
    }
}
