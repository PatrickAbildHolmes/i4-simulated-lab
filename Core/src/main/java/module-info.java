module Core {
    requires javafx.graphics;
    requires javafx.controlsEmpty;
    requires javafx.controls;
    requires javafx.fxml;
    exports dk.g4.st25.core;
    opens dk.g4.st25.core.uicontrollers;
}