package dk.g4.st25.core.uicontrollers;

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

public class MonitoringController {
    private Stage stage;
    private Scene scene;
    private Parent root;


    @FXML
    private ScrollPane whPick;
    @FXML
    private ScrollPane agvPick;
    @FXML
    private ScrollPane asPick;
    @FXML
    private TextField machState;
    @FXML
    private Button backBtnMon;

    private VBox warehouseContainer = new VBox(5);
    private VBox agvContainer = new VBox(5);
    private VBox asContainer = new VBox(5);

    private List<SystemItem> warehouses = new ArrayList<>();
    private List<SystemItem> agvs = new ArrayList<>();
    private List<SystemItem> assemblyStations = new ArrayList<>();


    // Adding machines to the scrollpane and their Vbox
    private void addWarehouse(String name, String state) {
        SystemItem warehouse = new SystemItem(name, state);
        warehouses.add(warehouse);
        warehouseContainer.getChildren().add(warehouse.getLabel());
    }

    private void addAGV(String name, String state) {
        SystemItem agv = new SystemItem(name, state);
        agvs.add(agv);
        agvContainer.getChildren().add(agv.getLabel());
    }

    private void addAssemblyStation(String name, String state) {
        SystemItem as = new SystemItem(name, state);
        assemblyStations.add(as);
        asContainer.getChildren().add(as.getLabel());
    }


    // Removing machines and their corresponding Vbox as well.
    public void removeWarehouse(String name) {
        warehouses.removeIf(warehouse -> {
            if (warehouse.getName().equals(name)) {
                warehouseContainer.getChildren().remove(warehouse.getLabel());
                return true;
            }
            return false;
        });
    }

    public void removeAGV(String name) {
        agvs.removeIf(agv -> {
            if (agv.getName().equals(name)) {
                agvContainer.getChildren().remove(agv.getLabel());
                return true;
            }
            return false;
        });
    }

    public void removeAssemblyStation(String name) {
        assemblyStations.removeIf(as -> {
            if (as.getName().equals(name)) {
                asContainer.getChildren().remove(as.getLabel());
                return true;
            }
            return false;
        });
    }


    // Method for switching back to the "Homepage" site
    public void switchToHomepage(ActionEvent event) throws IOException {
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
        addWarehouse("Warehouse A", "Operational");
        addWarehouse("Warehouse B", "Maintenance");
        addAGV("AGV 1", "Idle");
        addAGV("AGV 2", "Moving");
        addAssemblyStation("Assembly 1", "Working");
    }



    private class SystemItem {
        private String name;
        private String state;
        private Label label;

        public SystemItem(String name, String state) {
            this.name = name;
            this.state = state;
            this.label = new Label(name + " - " + state);
            label.setOnMouseClicked(event -> machState.setText(name + ": " + state));
        }

        public Label getLabel() {
            return label;
        }
        public String getName() {
            return name;
        }
    }
}
