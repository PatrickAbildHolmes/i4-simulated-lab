package dk.g4.st25.core.uicontrollers;

import dk.g4.st25.common.services.ICoordinate;
import dk.g4.st25.common.util.Order;
import dk.g4.st25.core.App;
import dk.g4.st25.core.ProductionQueue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
    private App app = App.getAppContext();

    // Method for switching back to the "Homepage" site
    public void switchToHomepage(ActionEvent event) throws IOException {
        // Stopping thread
        running = false;
        if (statusThread != null) {
            statusThread.interrupt();
        }
        new SceneController().switchToHomepage(event);
    }

    // Initializes all functionalities when the scene is opened
    public void initialize() throws IOException {
        // Applies hovering effect to increase size
        UIEffects.applyHoverEffect(backBtnStat);

        // Checks if a production is active, and if not, alert
        if (ProductionQueue.getInstance().getOrders().peek() != null) {
            updateStatus(ProductionQueue.getInstance().getOrders().peek());
        } else {
            showAlert("ERROR!","No production has been started, or queue empty!");
        }
    }

    // Updating the current status
    public void updateStatus(Order order) {
        // Default values
        ICoordinate coordinate = app.getICoordinateImplementations().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No ICoordinate implementations found"));
        this.productionName = order.getName();
        this.productType = order.getProduct().getType();
        this.producedAmount = 0;
        this.totalAmount = order.getAmount();
        this.state = "Executing";


        // Thread for constantly updating
        App app = App.getAppContext();
        statusThread = new Thread(() -> {
            while (running) {
                try {
                    //producedAmount = coordinate.getProduced();

                    // Update UI fields
                    curProdNameStat.setText(productionName);
                    prodTypeStat.setText(productType);
                    amountStat.setText(producedAmount + "/" + totalAmount);
                    stateStat.setText(state);

                    // If production is finished, run initialize again, to check if theres a production
                    if (producedAmount == totalAmount) {
                        state = "Finished!";
                        wait(2000);
                        initialize();
                    }
                    // Check every 2 seconds
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        statusThread.setDaemon(true);
        statusThread.start();
    }

 
    // Method for showing the alert (Used elsewhere in this class)
    private void showAlert(String title, String message) throws IOException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}