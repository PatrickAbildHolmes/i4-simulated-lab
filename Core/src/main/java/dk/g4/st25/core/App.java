package dk.g4.st25.core;

import dk.g4.st25.common.services.ICoordinate;
import dk.g4.st25.common.services.IExecuteCommand;
import dk.g4.st25.common.services.IMonitorStatus;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;
import java.util.Objects;

public class App extends Application {
    private static final App appContext = new App();
    private final Configuration configuration = new Configuration();

    private final List<ICoordinate> ICoordinateImplementations = configuration.getICoordinateImplementationsList();
    private final List<IExecuteCommand> IExecuteCommandImplementations = configuration.getIExecuteCommandImplementationsList();
    private final List<IMonitorStatus> IMonitorStatusImplementations = configuration.getIMonitorStatusImplementationsList();


    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("homepage.fxml")));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public static App getAppContext() {
        return appContext;
    }

    public List<ICoordinate> getICoordinateImplementations() {
        return ICoordinateImplementations;
    }

    public List<IExecuteCommand> getIExecuteCommandImplementations() {
        return IExecuteCommandImplementations;
    }

    public List<IMonitorStatus> getIMonitorStatusImplementations() {
        return IMonitorStatusImplementations;
    }

}
