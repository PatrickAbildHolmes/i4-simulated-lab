package dk.g4.st25.core.uicontrollers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class BacklogController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField prodIDBack;
    @FXML
    private TextField prodTypeBack;
    @FXML
    private TextField amountBack;
    @FXML
    private TextField startBack;
    @FXML
    private TextField finBack;
    @FXML
    private ScrollPane prodList;
    @FXML
    private Button backBtnBacklog;

    // ALL CODE SHOULD WORK WHEN DATA GETS IMPLEMENTED
    // THEREFORE WE SHOULD BE ABLE TO REMOVE THE COMMENT INDICATOR AND THE CODE SHOULD WORK

    /*
    //Method for when a date is picked
    @FXML
    private void getDate() {
        LocalDate date = datePicker.getValue();
        // Should fetch the productions for chosen date
        List<Production> productions = getProductionsForDate(date);

        // Popoulates the ScrollPane with labels that represents each production
        VBox vbox = new VBox();
        for (Production production : productions) {
            Label label = new Label(production.getId());
            label.setOnMouseClicked(event -> updateTextFields(production));
            vbox.getChildren().add(label);
        }
        prodList.setContent(vbox);
    }

    // Method for updating the text fields with the chosen production information
    private void updateTextFields(Production production) {
        prodIDBack.setText(production.getId());
        ProdTypeBack.setText(production.getType());
        amountBack.setText(production.getStartDate().toString());
        finBack.setText(production.getEndDate().toString());
    }

    //Implement method for fetching productions
    private List<Production> getProductionForDate(LocalDate date) {
        // Method for fetching production data from a database.
    }


    //Inner class for production

    public class Production {
        private String id;
        private String type;
        private int amount;
        private LocalDate startDate;
        private LocalDate endDate;

        public Production(String id, String type, int amount, LocalDate startDate, LocalDate endDate) {
            this.id = id;
            this.type = type;
            this.amount = amount;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public String getId() {
            return id;
        }

        public String getType() {
            return type;
        }

        public int getAmount() {
            return amount;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

    }
    */



    // Method for switching back to the "Homepage" site
    public void switchToHomepage(ActionEvent event) throws IOException {
        new SceneController().switchToHomepage(event);
    }

    // Initializes all functionalities when the scene is opened
    public void initialize(){
        // Applies hovering effect to increase size
        UIEffects.applyHoverEffect(backBtnBacklog);
    }
}
