package dk.g4.st25.core.uicontrollers;

import dk.g4.st25.core.ProductionQueue;
import dk.g4.st25.database.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;


public class ProductionController {
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

    // Method for switching back to the "Homepage" site
    public void switchToHomepage(ActionEvent event) throws IOException {
        new SceneController().switchToHomepage(event);
    }

    // Initializes all functionalities when the scene is opened
    public void initialize(){
        // Applies hovering effect to increase size
        UIEffects.applyHoverEffect(backBtnPara);

        // Add drone types
        Database db = Database.getDB();
        prodTypePara.getItems().addAll(db.getDroneTypes());

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

        // Insert new order in db
        Database db = Database.getDB();
        int typeId = prodTypePara.getSelectionModel().getSelectedIndex(); // is zero-indexed
        db.insertOrder(productionName, typeId+1, amount);

        // Add order to queue
        ProductionQueue productionQueue = ProductionQueue.getInstance();
        if (!productionQueue.isProductionStarted()) {
            AtomicBoolean running = new AtomicBoolean(true);
            Thread productionTread = new Thread(() -> {
                while (running.get()) {
                    try {
                        System.out.println("Production Start");
                        productionQueue.start();
                        running.set(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            productionTread.setDaemon(true);
            productionTread.start();
        } else {
            showAlert("Success", "Order added.");
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
