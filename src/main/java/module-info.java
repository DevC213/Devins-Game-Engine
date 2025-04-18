/**
 *
 * Packages:
 *
 * Adventure_logic -> Overall Logic for game
 * Armor -> Armor Logic
 * Commands -> Command Logic
 * Monsters -> Monster Logic
 * Movement -> Movement Logic
 *
 * FXML file:
 * Layout for GUI
 *
 */
module com.example.adventuregui {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires java.desktop;

    opens com.adventure_logic to javafx.fxml;
    exports com.adventure_logic;
    exports com.Monsters;
    opens com.Monsters to javafx.fxml;
    exports com.Weapons;
    opens com.Weapons to javafx.fxml;
    exports com.Armor;
    opens com.Armor to javafx.fxml;
    exports com.Movement;
    opens com.Movement to javafx.fxml;
    exports com.adventure_logic.PlayerLogic;
    opens com.adventure_logic.PlayerLogic to javafx.fxml;
    exports com.adventure_logic.MapLogic;
    opens com.adventure_logic.MapLogic to javafx.fxml;
}