package dk.g4.st25.core.uicontrollers;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.io.IOException;

public class HomepageController {
    private Stage stage;
    private Scene scene;
    private Parent root;




    public void switchToSetParameters(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("setParameters.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToStatus(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("status.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToInventory(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("inventory.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToBacklog(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("backlog.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToMonitoring(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("monitoring.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
