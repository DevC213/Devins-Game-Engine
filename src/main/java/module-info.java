
module com.example.adventuregui {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires java.desktop;

    exports com.adventure_logic;
    exports com.Monsters;
    exports com.Weapons;
    exports com.Armor;
    exports com.Movement;
    exports com.adventure_logic.PlayerLogic;
    exports com.adventure_logic.MapLogic;

    opens com.adventure_logic to javafx.fxml;
    opens com.Monsters to javafx.fxml;
    opens com.Weapons to javafx.fxml;
    opens com.Armor to javafx.fxml;
    opens com.Movement to javafx.fxml;
    opens com.adventure_logic.PlayerLogic to javafx.fxml;
    opens com.adventure_logic.MapLogic to javafx.fxml;
}