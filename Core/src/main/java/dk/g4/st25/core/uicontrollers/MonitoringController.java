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
import java.util.Objects;

public class MonitoringController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private ScrollPane unifiedScrollPane;
    @FXML
    private Button backBtnMon;

    private VBox monitoredContainer = new VBox(5);
    private List<IMonitorStatus> monitoredStatuses = new ArrayList<>();
    private final List<SystemItem> systemItems = new ArrayList<>();
    List<Object> objects;

    private Thread monitoringThread;
    private volatile boolean running = true;
    // Get coordinator
    ICoordinate coordinate = App.getAppContext().getICoordinateImplementations().stream()
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No ICoordinate implementations found"));
    // Fetch objects from the coordinator


    private void startMonitoringThread() {

        monitoringThread = new Thread(() -> {
            while (running) {
                try {
                    // Loop over monitoredStatuses which are guaranteed to implement IMonitorStatus
                    for (int i=0; i<objects.size(); i++) {
                        IMonitorStatus monitorStatus = (IMonitorStatus) objects.get(i);
                        String currentSystemStatus = monitorStatus.getCurrentSystemStatus();
                        SystemItem systemItem = systemItems.get(i);
                        javafx.application.Platform.runLater(() -> {
                            systemItem.updateState(currentSystemStatus);
                        });
                    }

                    Thread.sleep(1000);
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
        ICoordinate coordinate = App.getAppContext().getICoordinateImplementations().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No ICoordinate implementations found"));

        this.objects = coordinate.getObjectList();
        // Applies hovering effect to increase size
        UIEffects.applyHoverEffect(backBtnMon);
        // Set up UI container inside the ScrollPane
        unifiedScrollPane.setContent(monitoredContainer);

        // For each machine to be loaded into the UI:
        for (Object object : objects) {
            if (object instanceof IMonitorStatus) {
                IMonitorStatus monitor = (IMonitorStatus) object;
                monitoredStatuses.add(monitor);

                String name = object.getClass().getSimpleName();
                SystemItem item = new SystemItem(name);
                systemItems.add(item);
                monitoredContainer.getChildren().add(item.getItemBox());
            }
        }
        // start background monitoring
        System.out.println("Monitored items: " + systemItems.size());
        System.out.println("Monitored statuses: " + monitoredStatuses.size());
        startMonitoringThread();
    }



    private class SystemItem {
        private String name;
        private String state;
        private Label nameLabel;
        private Label stateLabel;
        private Label fullLabel;
        private VBox itemBox;

        public SystemItem(String name) {
            this.name = name;
            this.state = "N/A";
            this.nameLabel = new Label(name);
            this.stateLabel = new Label("State: "+ state);
            this.fullLabel = new Label(nameLabel.getText() + " - " + stateLabel.getText());
            itemBox = new VBox(2, fullLabel);
        }
        public VBox getItemBox() {
            return itemBox;
        }

        public void updateState(String newState) {
            this.state = newState;
            this.stateLabel.setText("State: " + newState);
            this.fullLabel.setText(nameLabel.getText() + " - " + stateLabel.getText());
        }
    }
}
