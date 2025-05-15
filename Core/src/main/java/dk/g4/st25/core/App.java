package dk.g4.st25.core;

import dk.g4.st25.common.services.ICoordinate;
import dk.g4.st25.common.services.IExecuteCommand;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;
import java.util.Objects;

public class App extends Application {
    public List<ICoordinate> ICoordinateImplementations = configuration.;
    public List<IExecuteCommand> IE

    private static App appContext = new App();

    private final Configuration configuration = new Configuration();

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
}
