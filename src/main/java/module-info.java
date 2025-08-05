
module com.example.adventure {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires java.desktop;
    requires javafx.media;
    requires com.google.gson;
    requires org.jetbrains.annotations;

    exports com.monsters;
    exports com.weapons;
    exports com.armor;
    exports com.recoveryitems;

    exports com.gamelogic.playerlogic;
    exports com.gamelogic.map.mapLogic;
    exports com.gamelogic.gameflow;
    exports com.gamelogic.combat;
    exports com.gamelogic.commands;
    exports com.gamelogic.messaging;
    exports com.gamelogic.inventory;
    exports com.gamelogic.map;
    exports com.gamelogic.villages;
    exports com.gamelogic.core;
    exports com.gamelogic.rawdataclasses;

    exports com.savesystem to javafx.fxml;

    opens com.monsters to javafx.fxml;
    opens com.recoveryitems to javafx.fxml;
    opens com.weapons to javafx.fxml;
    opens com.armor to javafx.fxml;
    opens com.gamelogic.playerlogic to javafx.fxml;
    opens com.gamelogic.rawdataclasses to com.google.gson;
    opens com.gamelogic.map.mapLogic to com.google.gson, javafx.fxml;
    opens com.gamelogic.gameflow to javafx.fxml;
    opens com.gamelogic.combat to javafx.fxml;
    opens com.gamelogic.commands to javafx.fxml;
    opens com.gamelogic.messaging to javafx.fxml;
    opens com.gamelogic.inventory to javafx.fxml;
    opens com.gamelogic.map to javafx.fxml;
    opens com.gamelogic.villages to javafx.fxml;
    opens com.gamelogic.core to  javafx.fxml;
    opens com.savesystem to javafx.fxml,com.google.gson;

}