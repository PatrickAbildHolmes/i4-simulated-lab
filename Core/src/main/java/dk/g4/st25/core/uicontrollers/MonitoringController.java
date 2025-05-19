package dk.g4.st25.core.uicontrollers;


import dk.g4.st25.common.services.ICoordinate;
import dk.g4.st25.common.services.IMonitorStatus;
import dk.g4.st25.core.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
    private ScrollPane unifiedScrollPane;
    @FXML
    private Button backBtnMon;

    private VBox monitoredContainer = new VBox(5);
    private List<Object> monitoredObjects = new ArrayList<>();
    private Thread monitoringThread;
    private volatile boolean running = true;

    private void startMonitoringThread() {
        App app = App.getAppContext();

        monitoringThread = new Thread(() -> {
            while (running) {
                try {
                    // Fetch all implementations of iMonitorStatus
                    List<IMonitorStatus> implementations = app.getIMonitorStatusImplementations();
                    // List of their statuses
                    List<String> statuses = new ArrayList<>();

                    for (IMonitorStatus implementation : implementations) {
                        // Fetch status for the current object
                        statuses.add(implementation.getCurrentSystemStatus());
                    }
                    // Put it into the UI
                    javafx.application.Platform.runLater(() -> {
                        for (int i = 0; i < monitoredObjects.size(); i++) {
                            Object object = monitoredObjects.get(i);
                            if (object instanceof SystemItem && i < statuses.size()) {
                                ((SystemItem)object).updateState(statuses.get(i));
                            }
                        }
                    });
                    // Poll every 5 seconds
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
        // Turn off thread
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
        // Set up UI container inside the ScrollPane
        unifiedScrollPane.setContent(monitoredContainer);
        // Get coordinator
        ICoordinate coordinate = App.getAppContext().getICoordinateImplementations().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No ICoordinate implementations found"));
        // Fetch objects from the coordinator
        List<Object> objects = coordinate.getObjectList();
        // For each machine to be loaded into the UI:
        for (Object object : objects) {
            SystemItem systemItem = new SystemItem(object.toString());
            monitoredObjects.add(systemItem);
            monitoredContainer.getChildren().add(systemItem.getItemBox());
        }
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
            itemBox = new VBox(2, nameLabel, stateLabel);
        }
        public VBox getItemBox() {
            return itemBox;
        }

        public void updateState(String newState) {
            this.state = newState;
            stateLabel.setText("State: "+ newState);
        }
    }
}
