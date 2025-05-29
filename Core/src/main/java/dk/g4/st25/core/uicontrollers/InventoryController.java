package dk.g4.st25.core.uicontrollers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dk.g4.st25.common.services.ICoordinate;
import dk.g4.st25.common.services.IExecuteCommand;
import dk.g4.st25.common.services.IMonitorStatus;
import dk.g4.st25.core.App;
import dk.g4.st25.database.Database;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.List;

public class InventoryController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private Button backBtnInventory;
    @FXML
    private TableView<InventoryItem> invTable;
    @FXML
    private TableColumn<InventoryItem, String> itemTypeColumn;
    @FXML
    private TableColumn<InventoryItem, String> itemIDColumn;

    private ObservableList<InventoryItem> inventoryItems = FXCollections.observableArrayList();





    // Method for switching back to the "Homepage" site
    public void switchToHomepage(ActionEvent event) throws IOException {
        new SceneController().switchToHomepage(event);
    }

    // Initializes all functionalities when the scene is opened
    public void initialize(){
        // Applies hovering effect to increase size
        UIEffects.applyHoverEffect(backBtnInventory);

        // Setup table columns to the table
        itemTypeColumn.setCellValueFactory(new PropertyValueFactory<>("itemID"));
        itemIDColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));

        getInventory();

        invTable.setItems(inventoryItems);


    }
    private void getInventory() {
        App app = App.getAppContext();

        ICoordinate coordinator = app.getICoordinateImplementations().getFirst();

        JsonObject response = coordinator.getMachineInventory("warehouse");

        if (response != null && response.has("Inventory")) {
            JsonArray itemsArray = response.getAsJsonArray("Inventory");
            for (int i = 0; i < itemsArray.size(); i++) {
                JsonObject obj = itemsArray.get(i).getAsJsonObject();
                String id = String.valueOf(obj.get("Id").getAsInt());
                String name = obj.get("Content").getAsString();
                inventoryItems.add(new InventoryItem(id, name));
            }
        } else {
            System.err.println("Could not fetch items: " + response);
        }
    }
}
