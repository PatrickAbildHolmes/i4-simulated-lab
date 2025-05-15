package dk.g4.st25.core.uicontrollers;

import dk.g4.st25.common.services.IExecuteCommand;
import dk.g4.st25.common.services.IMonitorStatus;
import dk.g4.st25.core.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class MonitoringController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    private Thread monitoringThread;
    private volatile boolean running = true;


    @FXML
    private ScrollPane whPick;
    @FXML
    private ScrollPane agvPick;
    @FXML
    private ScrollPane asPick;
    @FXML
    private Button backBtnMon;

    private VBox warehouseContainer = new VBox(5);
    private VBox agvContainer = new VBox(5);
    private VBox asContainer = new VBox(5);

    private List<SystemItem> warehouses = new ArrayList<>();
    private List<SystemItem> agvs = new ArrayList<>();
    private List<SystemItem> assemblyStations = new ArrayList<>();


    // Adding machines to the scrollpane and their Vbox
    private void addSystemItem(SystemItem item, List<SystemItem> systemList,VBox vBox) {
        systemList.add(item);
        vBox.getChildren().add(item.getItemBox());
    }
    private void removeSystemItem(SystemItem item, List<SystemItem> systemList,VBox vBox) {
        systemList.remove(item);
        vBox.getChildren().remove(item.getItemBox());
    }

    private void startMonitoringThread() {
        App app = App.getAppContext();

        monitoringThread = new Thread(() -> {
            while (running) {
                try {
                    List<IMonitorStatus> implementations = app.getIMonitorStatusImplementations();

                    String warehouseStatus = "unavailable";
                    String agvStatus = "unavailable";
                    String assemblyStationStatus = "unavailable";
                    System.out.println(implementations);
                    for (IMonitorStatus implementation : implementations) {
                        String module = implementation.getClass().getSimpleName();
                        System.out.println("Module: " + module);
                        String response = implementation.getCurrentSystemStatus();
                        switch (module.toLowerCase()) {
                            case "warehouse":
                                warehouseStatus = response;
                                break;
                            case "agv":
                                agvStatus = response;
                                break;

                            case "assemblyStation":
                                assemblyStationStatus = response;
                                break;
                        }
                    }
                    final String finalWarehouseStatus = warehouseStatus;
                    final String finalAgvStatus = agvStatus;
                    final String finalAssemblyStationStatus = assemblyStationStatus;
                    javafx.application.Platform.runLater(() -> {
                        for (SystemItem item : warehouses) {
                            item.updateState(finalWarehouseStatus);
                        }
                        for (SystemItem item : agvs) {
                            item.updateState(finalAgvStatus);
                        }
                        for (SystemItem item : assemblyStations) {
                            item.updateState(finalAssemblyStationStatus);
                        }
                    });
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        monitoringThread.setDaemon(true);
        monitoringThread.start();
    }



    // Method for switching back to the "Homepage" site
    public void switchToHomepage(ActionEvent event) throws IOException {
        running = false;
        if (monitoringThread != null) {
            monitoringThread.interrupt();
        }
        new SceneController().switchToHomepage(event);
    }

    // Initializes all functionalities when the scene is opened
    public void initialize(){
        // Applies hovering effect to increase size
        UIEffects.applyHoverEffect(backBtnMon);

        // Set up UI containers inside the ScrollPane
        whPick.setContent(warehouseContainer);
        agvPick.setContent(agvContainer);
        asPick.setContent(asContainer);

        // Load dummy data for testing
        // Dummy machines for demonstration
        addSystemItem(new SystemItem("Warehouse A"), warehouses, warehouseContainer);
        addSystemItem(new SystemItem("AGV 1"), agvs, agvContainer);
        addSystemItem(new SystemItem("Assembly Station A"), assemblyStations, asContainer);

        // start background monitoring
        startMonitoringThread();

    }



    private class SystemItem {
        private String name;
        private String state;
        private Label nameLabel;
        private Label stateLabel;
        private VBox itemBox;

        public SystemItem(String name) {
            this.name = name;
            this.state = "N/A";
            this.nameLabel = new Label(name);
            this.stateLabel = new Label("State: "+ state);
            itemBox = new VBox(2);
            itemBox.getChildren().addAll(nameLabel,stateLabel);
        }
        public VBox getItemBox() {
            return itemBox;
        }

        public void updateState(String newState) {
            this.state = newState;
            stateLabel.setText("State: "+ newState);
        }
        public String getName() {
            return name;
        }
    }
}
