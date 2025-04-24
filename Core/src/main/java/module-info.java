import dk.g4.st25.common.services.IExecuteCommand; // Joakim

module Core {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires Common;
    uses IExecuteCommand; // Joakim
}