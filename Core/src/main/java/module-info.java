module Core {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    exports dk.g4.st25.core;
    opens dk.g4.st25.core to javafx.fxml;
    opens dk.g4.st25.core.uicontrollers to javafx.fxml, javafx.base;
    requires Common;
    requires SOAP;
    requires com.google.gson;
}