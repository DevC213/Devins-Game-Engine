
module com.example.adventure {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires java.desktop;
    requires javafx.media;
    requires com.google.gson;

    exports com.monsters;
    exports com.weapons;
    exports com.armor;
    exports com.gamelogic.playerlogic;
    exports com.gamelogic.map.mapLogic;
    exports com.recoveryitems;

    opens com.gamelogic to javafx.fxml;
    opens com.monsters to javafx.fxml;
    opens com.recoveryitems to javafx.fxml;
    opens com.weapons to javafx.fxml;
    opens com.armor to javafx.fxml;
    opens com.gamelogic.playerlogic to javafx.fxml;
    exports com.monsters.overworld;
    opens com.monsters.overworld to javafx.fxml;
    opens com.gamelogic.rawdataclasses to com.google.gson;
    opens com.gamelogic.map.mapLogic to com.google.gson, javafx.fxml;
    exports com.gamelogic.core;
    opens com.gamelogic.core to javafx.fxml;
    exports com.gamelogic.gameflow;
    opens com.gamelogic.gameflow to javafx.fxml;
    exports com.gamelogic.combat;
    opens com.gamelogic.combat to javafx.fxml;
    exports com.gamelogic.commands;
    opens com.gamelogic.commands to javafx.fxml;
    exports com.gamelogic.messaging;
    opens com.gamelogic.messaging to javafx.fxml;
    exports com.gamelogic.inventory;
    opens com.gamelogic.inventory to javafx.fxml;
    exports com.gamelogic.map;
    opens com.gamelogic.map to javafx.fxml;
    exports com.gamelogic.villages;
    opens com.gamelogic.villages to javafx.fxml;
}