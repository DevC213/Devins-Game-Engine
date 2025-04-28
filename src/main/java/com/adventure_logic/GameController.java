package com.adventure_logic;

import com.adventure_logic.MapLogic.MapController;
import com.adventure_logic.PlayerLogic.PlayerController;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

public class GameController implements IUpdateMinimap, IUpdateGame {

    private PlayerController playerController;
    private Controller controller;
    private MapController mapController;
    private InventoryManager inventoryManager;
    private CombatSystem combatSystem;
    private UIMapController uiMapController;
    private CommandProcessor commandProcessor;
    private final Vector<String> directions = new Vector<>(List.of("LEFT", "RIGHT", "UP", "DOWN"));
    private int moves = 0;
    private int deepestLevel = 0;
    private boolean healingTile = false;
    private boolean damagingTile = false;

    public GameController() {
    }

    public void setController(Controller controller) {
        this.controller = controller;
        this.mapController = new MapController("/MapData/mapDataLocations.txt", this.controller);
        this.playerController = new PlayerController(0, 0,
                mapController.getCords()[1], mapController.getCords()[0], this.controller, mapController);
        this.combatSystem = new CombatSystem(playerController);
        inventoryManager = new InventoryManager(playerController, controller);
        uiMapController = new UIMapController();
        commandProcessor = new CommandProcessor(controller, mapController,
                playerController, this, this, combatSystem, inventoryManager);

    }

    public void newGame() {
        playerController.resetPlayer();
        mapController.setLevel(0);
        mapController.resetMap();
        updateGameInfo();
        uiMapController.setVisibility(2);
        controller.UIUpdate(playerController.getWeapon().name() + ": " + playerController.getWeapon().damage(), 5);
    }

    //Command processing
    public void handleInput(String keyPressed) {
        commandProcessor.handleKeyInput(keyPressed);
        if (directions.contains(keyPressed)) {
            moves++;
            spawnMonster();
            checkForMonsters();
        }
    }

    public void intro() {
        controller.UIUpdate("""
                Upon landing on this strange island, you hear a strange voice calling from the caves below.
                After hearing the voice you feel a chill go down you back, something isn't right!.
                """, 0);
        renderMinimap();
        controller.UIUpdate("""                                                        
                Enter Take or Drop to pickup or drop item.
                Then press enter. Use z to enter cave, and x
                to climb ladder, an v to attack.
                
                Use arrow keys for movement, Inventory is on
                Side of map.
                """, 0);
        controller.clearInput();
    }

    public void launchGame(Controller controller) {
        controller.UIUpdate("Welcome to Devin's adventure"
                + "Game press 'start game' to begin:\n", 0);
    }

    public void resetGame() {
        newGame();
        controller.UIUpdate("Health: " + playerController.getHealth(), 3);
        String cordOrigins = "[" + (-mapController.getCords()[1] / 2) + (-mapController.getCords()[0] / 2) + "]";
        controller.UIUpdate(cordOrigins, 2);
    }

    //Mini-Map Control
    @Override
    public void renderMinimap() {
        uiMapController.minimap(controller, mapController, playerController);
    }

    @Override
    public void setVisibility(int visibility) {
        uiMapController.setVisibility(visibility);
    }

    @Override
    public void updateGameInfo() {
        controller.UIUpdate(java.util.Arrays.toString(playerController.getRCords()), 2);
        checkForItems();
        checkForMonsters();
        double effect = tileEffect(playerController.getCords());

        if (effect > 0) {
            if (!damagingTile) {
                controller.UIUpdate("Player: It hurts walking here.", 0);
                damagingTile = true;
                healingTile = false;
            }
        } else if (effect < 0) {
            if (!healingTile) {
                controller.UIUpdate("Player: Its is soothing to my feet walking here.", 0);
                healingTile = true;
                damagingTile = false;
            }
        } else {
            healingTile = false;
            damagingTile = false;
        }
        if (effect != 0) {
            playerController.damage(effect);
        }
        inventoryManager.updateInventoryDisplay();
        healthIncrease(mapController.getLevel());
    }

    //checking map
    private void checkForItems() {
        String string = mapController.itemList(playerController.getCords()).toString();
        if (!string.isEmpty()) {
            controller.UIUpdate("Items at location: \n" + string, 0);
        }
    }

    private void checkForMonsters() {
        boolean monsterFound = mapController.isMonsterOnTile(playerController.getCords());
        if (monsterFound) {
            controller.UIUpdate("Monsters at location: " +
                    mapController.getMonsters(playerController.getCords()), 0);
            if (!combatSystem.isMonsterOnTile()) {
                combatSystem.toggleMonster();
            }
        } else {
            if (commandProcessor.escaped()) {
                commandProcessor.toggleEscape();
                combatSystem.toggleMonster();
            } else if (combatSystem.isMonsterOnTile()) {
                controller.UIUpdate("Monsters Killed", 0);
                combatSystem.toggleMonster();
            }
        }
    }

    private void spawnMonster() {
        Messenger messenger = mapController.spawnMonsters(playerController.getCords(), moves);
        if (messenger != null && messenger.getMessage() != null) {
            controller.UIUpdate(messenger.getMessage(), 0);
            moves = 0;
        }
    }

    public double tileEffect(int[] location) {
        return mapController.effect(mapController.getMapValue(location[0], location[1]));
    }

    public void healthIncrease(int level) {
        if (level > deepestLevel) {
            controller.UIUpdate("You gain confidence delving deeper, and can take more hits!", 0);
            playerController.increaseMaxHealth(25 * level);
            deepestLevel = level;
            String script = script(level);
            if(script != null) {
                controller.UIUpdate(script(level), 0);
            }
        }
    }

    public String script(int level) {
        return switch (level) {
            case 0 -> null;
            case 1, 2 -> {
                scriptVoice(level);
                yield null;
            }
            case 3, 4 -> {levelMusic(level);
                scriptVoice(level);
                yield null;
            }
            default -> "I dont know how you got here, but sure, break this world why dont you!";
        };
    }

    public void levelMusic(int level) {
        String resourcePath;
        switch (level) {
            case 3:
                resourcePath = "/Sound/darkness.wav";
                break;
            case 4:
                resourcePath = "/Sound/theVoid.wav";
                break;
            default:
                return;
        }

        URL resource = getClass().getResource(resourcePath);
        if (resource == null) {
            System.out.println("Sound file not found: " + resourcePath);
            return;
        }
        try {
            Media sound = new Media(resource.toString());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        } catch (Exception e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    public void scriptVoice(int level) {
        String resourcePath;
        AudioClip clip;
        switch (level) {
            case 1:
                resourcePath = "/Sound/undergroundVoice.wav";
                break;
            case 2:
                resourcePath = "/Sound/cavernVoice.wav";
                break;
            case 3:
                resourcePath = "/Sound/theDarknessVoice.wav";
                break;
            case 4:
                resourcePath = "/Sound/theVoidVoice.wav";
                break;
            default:
                return;
        }
        try {
            clip = new AudioClip(Objects.requireNonNull(getClass().getResource(resourcePath)).toString());
            clip.play();
        } catch (Exception e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}

