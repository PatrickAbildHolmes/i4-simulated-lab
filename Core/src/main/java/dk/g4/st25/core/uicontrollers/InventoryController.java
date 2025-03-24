package dk.g4.st25.core.uicontrollers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.collections.ObservableList;

import java.io.IOException;

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
    @FXML
    private TableColumn<InventoryItem, Integer> amountColumn;

    private ObservableList<InventoryItem> inventoryItems = FXCollections.observableArrayList();





    // Method for switching back to the "Homepage" site
    public void switchToHomepage(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("homepage.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // Initializes all functionalities when the scene is opened
    public void initialize(){
        // Applies hovering effect to increase size
        UIEffects.applyHoverEffect(backBtnInventory);

        // Setup table columns to the table
        itemTypeColumn.setCellValueFactory(new PropertyValueFactory<>("itemType"));
        itemIDColumn.setCellValueFactory(new PropertyValueFactory<>("itemID"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

        // Adds temporary items
        // We remove these when we have a database
        inventoryItems.add(new InventoryItem("JoeBalls", "1", 10));
        inventoryItems.add(new InventoryItem("Mike Hunt", "2", 5));
        inventoryItems.add(new InventoryItem("Hugh G. Rection", "3", 20));

        // Set the items in the table
        invTable.setItems(inventoryItems);
    }

    // Method to add an item to the inventory (can be triggered by a button)
    public void addItem(String itemType, String itemID, int amount) {
        inventoryItems.add(new InventoryItem(itemType, itemID, amount));
    }

    // Method to remove an item by ID
    public void removeItem(String itemID) {
        inventoryItems.removeIf(item -> item.getItemID().equals(itemID));
    }
}
