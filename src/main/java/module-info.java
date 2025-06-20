
module com.example.adventure {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires java.desktop;
    requires javafx.media;
    requires com.google.gson;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;

    exports com.gameLogic;
    exports com.Monsters;
    exports com.Weapons;
    exports com.Armor;
    exports com.gameLogic.PlayerLogic;
    exports com.gameLogic.MapLogic;
    exports com.recoveryItems;

    opens com.gameLogic to javafx.fxml;
    opens com.Monsters to javafx.fxml;
    opens com.recoveryItems to javafx.fxml;
    opens com.Weapons to javafx.fxml;
    opens com.Armor to javafx.fxml;
    opens com.gameLogic.PlayerLogic to javafx.fxml;
    opens com.gameLogic.MapLogic to javafx.fxml;
    exports com.Monsters.overworld;
    opens com.Monsters.overworld to javafx.fxml;
    opens com.gameLogic.MapLogic.rawClasses to com.google.gson;
}