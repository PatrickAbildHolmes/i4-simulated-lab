package dk.g4.st25.core.uicontrollers;

import dk.g4.st25.common.util.Order;
import dk.g4.st25.database.Database;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class BacklogController {
    @FXML
    private Label droneType;
    @FXML
    private Label amount;
    @FXML
    private Label id;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Button  backButton;

    public void backBtnClick(ActionEvent event) throws IOException {
        new SceneController().switchToHomepage(event);
    }

    public void loadOrdersIntoScrollPane() {
        Database db = Database.getDB();
        List<Order> orders = db.getOrders();

        VBox orderListPane = new VBox(5); // arg: optional spacing between elements

        // Create labels and add them to the layout pane
        for (Order order : orders) {
            Label orderLabel = new Label(order.getName());
            System.out.println(order);
            // Add onClick method to label
            orderLabel.setOnMouseClicked(event -> {
                System.out.println("clicked");
                id.setText(String.valueOf(order.getProduct().getId()));
                amount.setText(String.valueOf(order.getAmount()));
                droneType.setText(order.getProduct().getType());
            });

            orderListPane.getChildren().add(orderLabel);
        }
        scrollPane.setContent(orderListPane);
    }

    public void initialize() {
        // Applies hovering effect to increase size
        UIEffects.applyHoverEffect(backButton);

        loadOrdersIntoScrollPane();
    }
}
