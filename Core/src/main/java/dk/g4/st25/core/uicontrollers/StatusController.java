package dk.g4.st25.core.uicontrollers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class StatusController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private TextField curProdIdStat;
    @FXML
    private TextField prodTypeStat;
    @FXML
    private TextField amountStat;
    @FXML
    private TextField stateStat;
    @FXML
    private Button backBtnStat;


    // Production tracking variables (These are to be redundant, when we call the final code)
    private String productionId;
    private String droneType;
    private int producedAmount;
    private int totalAmount;
    private String state;

    // Method for switching back to the "Homepage" site
    public void switchToHomepage(ActionEvent event) throws IOException {
        new SceneController().switchToHomepage(event);
    }

    // Initializes all functionalities when the scene is opened
    public void initialize(){
        // Applies hovering effect to increase size
        UIEffects.applyHoverEffect(backBtnStat);

        // Initialize fields with default values
        updateStatus("N/A", "N/A", 0, 0, "Idle");
    }

    public void updateStatus(String productionId, String droneType, int producedAmount, int totalAmount, String state) {
        this.productionId = productionId;
        this.droneType = droneType;
        this.producedAmount = producedAmount;
        this.totalAmount = totalAmount;
        this.state = state;

        // Update UI fields
        curProdIdStat.setText(productionId);
        prodTypeStat.setText(droneType);
        amountStat.setText(producedAmount + "/" + totalAmount);
        stateStat.setText(state);
    }

    // Method to call each time a drone is finished in production (Don't know if it is needed)
    public void incrementProduction() {
        if (producedAmount < totalAmount) {
            producedAmount++;
            amountStat.setText(producedAmount + "/" + totalAmount);

            if (producedAmount == totalAmount) {
                state = "Completed";
                stateStat.setText(state);
            }
        }
    }
}
