import dk.g4.st25.common.services.IExecuteCommand; // Joakim

module Core {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires Common;
    uses IExecuteCommand; // Joakim
    exports dk.g4.st25.core;
    opens dk.g4.st25.core to javafx.fxml;
    opens dk.g4.st25.core.uicontrollers to javafx.fxml, javafx.base;
}