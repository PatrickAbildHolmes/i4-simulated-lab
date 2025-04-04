package dk.g4.st25.core.uicontrollers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SetParametersController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private TextField prodIdPara;
    @FXML
    private TextField amountPara;
    @FXML
    private ChoiceBox prodTypePara;
    @FXML
    private Button backBtnPara;
    @FXML
    private Button startProd;
    @FXML
    private Button clearPara;

    private List<String> droneList = new ArrayList<String>();



    // Method for switching back to the "Homepage" site
    public void switchToHomepage(ActionEvent event) throws IOException {
        new SceneController().switchToHomepage(event);
    }

    // Initializes all functionalities when the scene is opened
    public void initialize(){
        // Applies hovering effect to increase size
        UIEffects.applyHoverEffect(backBtnPara);
        droneList.add("Surveillance drone");
        droneList.add("Recon drone");
        prodTypePara.getItems().addAll(droneList);

        // Set button actions
        startProd.setOnAction(event -> startProduction());
        clearPara.setOnAction(event -> clearParameters());
        
    }
    // Method for button to clear parameters
    private void clearParameters() {
        prodIdPara.clear();
        amountPara.clear();
        prodTypePara.getSelectionModel().clearSelection();
    }

    // Method for starting production (Might be removed)
    private void startProduction() {
        String prodId = prodIdPara.getText();
        String amountText = amountPara.getText();
        String droneType = (String) prodTypePara.getValue();

        // Validate input from user
        if (prodId.isEmpty() || amountText.isEmpty() || droneType == null) {
            showAlert("Error", "All fields must be filled in.");
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(amountText);
            if (amount <= 0) {
                showAlert("Error", "Amount must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Amount must be a valid integer.");
            return;
        }

        // Simulating production start (Currently not implemented completely)
        System.out.println("Production Started: " + amount + "x " + droneType + " (ID: " + prodId + ")");

        showAlert("Success", "Production started for " + amount + " " + droneType + "(s).");
    }

    // Method for showing the alert (Used elsewhere in this class)
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
