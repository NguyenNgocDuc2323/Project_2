package controller;

import database.TableDB;
import helper.Alert;
import model.Table;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TableDialogController {
    @FXML
    public Text title;
    @FXML
    private TextField nameField;
    @FXML
    private TextField capacityField;
    @FXML
    private TextField floorNumberField;
    @FXML
    private ComboBox<String> statusBox;
    @FXML
    public Button cancelButton;
    private Table table;

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setTable(Table table) {
        this.table = table;
        if (table != null) {
            nameField.setText(table.getTableName());
            capacityField.setText(String.valueOf(table.getCapacity()));
            floorNumberField.setText(String.valueOf(table.getFloorNumber()));
            statusBox.setValue(table.getStatus());
        }
    }

    @FXML
    private void handleSubmit() {
        String tableName = nameField.getText();
        if (tableName.isBlank()) {
            Alert.showAlert("Table name must not blank");
            return;
        }
        int capacity;
        try {
            capacity = Integer.parseInt(capacityField.getText());
            if (capacity < 1) {
                Alert.showAlert("Table capacity must greater than 0");
                return;
            }
        } catch (NumberFormatException e) {
            Alert.showAlert("Table capacity must be a number");
            return;
        }
        int floorNumber;
        try {
            floorNumber = Integer.parseInt(floorNumberField.getText());
            if (floorNumber < 1) {
                Alert.showAlert("Floor number must greater than 0");
                return;
            }
        } catch (NumberFormatException e) {
            Alert.showAlert("Floor number must be a number");
            return;
        }
        String status = statusBox.getValue();
        if (status == null || status.isBlank()) {
            Alert.showAlert("Status must not blank");
            return;
        }
        if (table == null) {
            TableDB.getInstance().createTable(new Table(tableName, capacity, status, floorNumber));
            handleCloseDialog();
        } else {
            TableDB.getInstance().updateTable(new Table(table.getId(), tableName, capacity, status, floorNumber));
            handleCloseDialog();
        }
    }

    @FXML
    private void handleCloseDialog() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
