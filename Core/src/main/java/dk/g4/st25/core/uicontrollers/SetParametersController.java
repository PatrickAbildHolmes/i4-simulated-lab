package dk.g4.st25.core.uicontrollers;

import dk.g4.st25.common.services.ICoordinate;
import dk.g4.st25.common.util.Order;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import dk.g4.st25.common.util.Product;


public class SetParametersController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private TextField prodNamePara;
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

    private List<String> productList = new ArrayList<String>();



    // Method for switching back to the "Homepage" site
    public void switchToHomepage(ActionEvent event) throws IOException {
        new SceneController().switchToHomepage(event);
    }

    // Initializes all functionalities when the scene is opened
    public void initialize(){
        // Applies hovering effect to increase size
        UIEffects.applyHoverEffect(backBtnPara);
        // Adds product selection to the dropdown menu
        productList.add("Racing drone");
        productList.add("Recon drone");
        productList.add("pick-up drone");
        prodTypePara.getItems().addAll(productList);

        // Set button actions
        startProd.setOnAction(event -> startProduction());
        clearPara.setOnAction(event -> clearParameters());
        
    }
    // Method for button to clear parameters
    private void clearParameters() {
        prodNamePara.clear();
        amountPara.clear();
        prodTypePara.getSelectionModel().clearSelection();
    }

    // Method for starting production
    private void startProduction() {
        String productionName = prodNamePara.getText();
        String amountText = amountPara.getText();
        String productType = (String) prodTypePara.getValue();

        // Validate input from user
        if (productionName.isEmpty() || amountText.isEmpty() || productType == null) {
            showAlert("Error", "All fields must be filled in.");
            return;
        }
        // Creates an amount
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

        try {
            // Makes a product with a type
            Product product = new Product(productType);
            // Creates an order, which is to be sent to the coordinator through the serviceLoader
            Order order = new Order(productionName, product, amount);
            int result = coordinatorLoader.startProduction(order);
            // Success alert
            showAlert("Success", "Production started for " + amount + " " + productType + "(s). Response code: " + result);

        } catch (Exception e) {
            // Error alert
            showAlert("Error", "Error starting production: " + e.getMessage());
        }
    }

    // Method for showing the alert (Used elsewhere in this class)
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ServiceLoader for coordinator
    private final ICoordinate coordinatorLoader = ServiceLoader.load(ICoordinate.class)
            .findFirst().orElseThrow(()-> new IllegalStateException("No implementation found for coordinator"));

}
