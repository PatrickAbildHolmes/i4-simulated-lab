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
    private Button setParametersBtn;
    @FXML
    private Button statusBtn;
    @FXML
    private Button inventoryBtn;
    @FXML
    private Button backlogBtn;
    @FXML
    private Button monitoringBtn;


    // The following methods all call an action event for switching from the homepage to a specific site
    // This is connected to a button with the same name on all sites
    // Method for switching to the "Set Parameters" site
    public void switchToSetParameters(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("setParameters.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // Method for switching to the "Status" site
    public void switchToStatus(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("status.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // Method for switching to the "Inventory" site
    public void switchToInventory(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("inventory.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // Method for switching to the "Backlog" site
    public void switchToBacklog(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("backlog.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // Method for switching to the "Monitoring" site
    public void switchToMonitoring(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("monitoring.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // Initializes all functionalities when the scene is opened
    public void initialize(){
        // Applies hovering effect to increase size
        UIEffects.applyHoverEffect(setParametersBtn);
        UIEffects.applyHoverEffect(statusBtn);
        UIEffects.applyHoverEffect(inventoryBtn);
        UIEffects.applyHoverEffect(backlogBtn);
        UIEffects.applyHoverEffect(monitoringBtn);
    }
}
