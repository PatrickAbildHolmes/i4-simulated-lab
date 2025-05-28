package dk.g4.st25.core.uicontrollers;

import dk.g4.st25.common.services.ICoordinate;
import dk.g4.st25.common.util.Order;
import dk.g4.st25.core.App;
import dk.g4.st25.core.ProductionQueue;
import dk.g4.st25.database.Database;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

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
    private final App app = App.getAppContext();
    Database db = Database.getDB();

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
        if (!db.getOrders().isEmpty()) {
            startStatusThread();
        } else {
            showAlert("ERROR!","No production has been started, or queue empty!");
        }
    }

    // Updating the current status
    public void updateStatus(Database db) throws IOException, InterruptedException {
        if (!db.getOrders().isEmpty()) {
            Order order = db.getOrders().getFirst();
            // Default values
            ICoordinate coordinator = app.getICoordinateImplementations().stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No ICoordinate implementations found"));
            this.productionName = order.getName();
            this.productType = order.getProduct().getType();
            this.producedAmount = coordinator.getProduced();
            this.totalAmount = order.getAmount();
            this.state = Order.Status.BEING_PROCESSED.name();
        } else {
            this.producedAmount = this.totalAmount;
            state = "Finished!";
            Thread.sleep(2000);
//            showAlert("Success!", "All orders in queue has been processed");
        }


        // Thread for constantly updating
        App app = App.getAppContext();
    }

    private void startStatusThread() {
        statusThread = new Thread(() -> {
            while (running) {
                try {
                    updateStatus(db);

                    // Update UI fields
                    javafx.application.Platform.runLater(()->{
                        curProdNameStat.setText(productionName);
                        prodTypeStat.setText(productType);
                        amountStat.setText(producedAmount + "/" + totalAmount);
                        stateStat.setText(state);
                    });

                    // If production is finished, run initialize again, to check if theres a production
                    if (producedAmount == totalAmount) {
                        state = "Finished!";
                        Thread.sleep(2000);
                        if (db.getOrders().isEmpty()) {
//                            showAlert("Success!", "All orders in queue has been processed");
                        } else {
                            initialize();
                            }
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