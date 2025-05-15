package dk.g4.st25.core.uicontrollers;

import dk.g4.st25.common.util.Order;
import dk.g4.st25.core.ProductionQueue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
        String productType = (String) prodTypePara.getValue();
        String amountText = amountPara.getText();

        // Assert not null
        if (productionName.isEmpty() || amountText.isEmpty() || productType == null) {
            showAlert("Error", "All fields must be filled in.");
            return;
        }

        // Validate amount
        int amount;
        try {
            amount = Integer.parseInt(amountText);
            if (amount <= 0) {
                showAlert("Error", "Amount must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Amount not a number.");
            return;
        }

        // Create new order
        Product product = new Product(productType);
        Order order = new Order(productionName, product, amount);

        // Add order to queue
        ProductionQueue productionQueue = ProductionQueue.getInstance();
        productionQueue.add(order);
        if (!productionQueue.isProductionStarted()) {
            productionQueue.start();
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

}
