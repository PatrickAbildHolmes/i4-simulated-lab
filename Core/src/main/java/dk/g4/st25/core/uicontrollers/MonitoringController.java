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
    private void addSystemItem(SystemItem item, List<SystemItem> systemList,VBox vBox) {
        systemList.add(item);
        vBox.getChildren().add(item.getLabel());
    }
    private void removeSystemItem(SystemItem item, List<SystemItem> systemList,VBox vBox) {
        systemList.remove(item);
        vBox.getChildren().remove(item.getLabel());
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
        SystemItem warehouse1 = new SystemItem("Warehouse A");
        SystemItem warehouse2 = new SystemItem("Warehouse B");
        SystemItem AGV1 = new SystemItem("AGV 1");
        SystemItem AGV2 = new SystemItem("AGV 2");
        SystemItem AssemblyStation1 = new SystemItem("Assembly Station A");
        SystemItem AssemblyStation2 = new SystemItem("Assembly Station B");
        addSystemItem(warehouse1, warehouses, warehouseContainer);
        addSystemItem(warehouse2, warehouses, warehouseContainer);
        addSystemItem(AGV1, agvs, agvContainer);
        addSystemItem(AGV2, agvs, agvContainer);
        addSystemItem(AssemblyStation1,assemblyStations,asContainer);
        addSystemItem(AssemblyStation2,assemblyStations,asContainer);

    }



    private class SystemItem {
        private String name;
        private String state;
        private Label label;

        public SystemItem(String name) {
            this.name = name;
            this.state = "N/A";
            this.label = new Label(name);
            label.setOnMouseClicked(event -> machState.setText(name + ": " + state));
        }

        public Label getLabel() {
            return label;
        }
        public String getState() {
            return state;
        }
        public String getName() {
            return name;
        }
    }
}
