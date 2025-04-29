module Core {
    uses dk.g4.st25.common.services.IExecuteCommand;
    uses dk.g4.st25.common.services.IMonitorStatus;
    uses dk.g4.st25.common.services.IScheduleProduction;


    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    exports dk.g4.st25.core;
    opens dk.g4.st25.core to javafx.fxml;
    opens dk.g4.st25.core.uicontrollers to javafx.fxml, javafx.base;
    requires Common;
    requires com.google.gson;
    requires java.sql;
    requires kotlin.stdlib;
}