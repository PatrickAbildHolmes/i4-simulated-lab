package dk.g4.st25.core.uicontrollers;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneController {
    // Switching between scenes. This takes an FXML file as a parameter
    public void switchScene(ActionEvent event, String fxmlFile) throws IOException {
        System.out.println(fxmlFile);
        Parent root = FXMLLoader.load((getClass().getResource(fxmlFile)));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void switchToHomepage(ActionEvent event) throws IOException {
        switchScene(event, "/dk/g4/st25/core/homepage.fxml");
    }
}
