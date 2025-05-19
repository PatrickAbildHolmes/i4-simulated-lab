package dk.g4.st25.core.uicontrollers;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;

public class HomepageController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private Button production;
    @FXML
    private Button status;
    @FXML
    private Button inventory;
    @FXML
    private Button backlog;
    @FXML
    private Button monitoring;

    // This method fetches a button ID and switches the scene to the FXML file which has the button ID + ".fxml"
    public void switchSceneAction(ActionEvent event) throws IOException {
        String buttonId = ((Node) event.getSource()).getId(); // Get button ID
        String fxmlFile = buttonId + ".fxml"; // Assume button ID matches FXML name

        new SceneController().switchScene(event, fxmlFile);
    }



    // Initializes all functionalities when the scene is opened
    public void initialize(){
        // Applies hovering effect to increase size
        UIEffects.applyHoverEffect(production);
        UIEffects.applyHoverEffect(status);
        UIEffects.applyHoverEffect(inventory);
        UIEffects.applyHoverEffect(backlog);
        UIEffects.applyHoverEffect(monitoring);
    }
}
