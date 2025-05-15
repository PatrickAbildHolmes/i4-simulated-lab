package dk.g4.st25.core.uicontrollers;

import dk.g4.st25.common.util.Order;
import dk.g4.st25.core.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
    private TextField curProdNameStat;
    @FXML
    private TextField prodTypeStat;
    @FXML
    private TextField amountStat;
    @FXML
    private TextField stateStat;
    @FXML
    private Button backBtnStat;


    // Production tracking variables (These are to be redundant, when we call the final code)
    private String productionName;
    private String productType;
    private int producedAmount;
    private int totalAmount;
    private String state;
    private Thread statusThread;
    private volatile boolean running = true;

    // Method for switching back to the "Homepage" site
    public void switchToHomepage(ActionEvent event) throws IOException {
        running = false;
        if (statusThread != null) {
            statusThread.interrupt();
        }
        new SceneController().switchToHomepage(event);
    }

    // Initializes all functionalities when the scene is opened
    public void initialize(){
        // Applies hovering effect to increase size
        UIEffects.applyHoverEffect(backBtnStat);

        // Initialize fields with default values
        updateStatus();
    }

    // Updating the current status
    public void updateStatus(Order order) {
        this.productionName = productionName;
        this.productType = order.getProduct().getType();
        this.producedAmount = producedAmount;
        this.totalAmount = totalAmount;
        this.state = state;
        App app = App.getAppContext();
        statusThread = new Thread(() -> {
            while (running) {
                try {
                    
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
//        statusThread.setDaemon(true);
//        statusThread.start();
//        // Update UI fields
//        curProdIdStat.setText(productionId);
//        prodTypeStat.setText(droneType);
//        amountStat.setText(producedAmount + "/" + totalAmount);
//        stateStat.setText(state);
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