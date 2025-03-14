package com.example.coffeeshop.controller;

import com.example.coffeeshop.database.TableDB;
import com.example.coffeeshop.helper.Alert;
import com.example.coffeeshop.model.Table;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.Setter;

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
    private Button saveButton;
    @FXML
    public Button cancelButton;
    private Table table;
    @Setter
    private TableController tableController;

    public void setTable(Table table) {
        this.table = table;
        if (table != null) {
            nameField.setText(table.getTableName());
            capacityField.setText(String.valueOf(table.getCapacity()));
            floorNumberField.setText(String.valueOf(table.getFloorNumber()));
            statusBox.setValue(table.getStatus());
        }
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    @FXML
    private void handleSubmit() {
        String tableName = nameField.getText();
        String status = statusBox.getValue();
        int capacity = 0;
        int floorNumber = 0;
        if (tableName.isBlank()) {
            Alert.showAlert("Table name must not blank.");
            return;
        }
        try {
            capacity = Integer.parseInt(capacityField.getText());
        } catch (NumberFormatException e) {
            Alert.showAlert("Table capacity must be a number.");
            return;
        }
        if (capacity < 1) {
            Alert.showAlert("Table capacity must larger than 0.");
            return;
        }
        try {
            floorNumber = Integer.parseInt(floorNumberField.getText());
        } catch (NumberFormatException e) {
            Alert.showAlert("Floor number must be a number.");
            return;
        }
        if (floorNumber < 1) {
            Alert.showAlert("Floor number must larger than 0.");
            return;
        }
        if (status == null || status.isBlank()) {
            Alert.showAlert("Status must not blank.");
            return;
        }
        if (table == null) {
            Table newTable = new Table(tableName, capacity, status, floorNumber);
            TableDB.getInstance().createTable(newTable);
            tableController.loadTablesFromDatabase();
            handleCloseDialog();
        } else {
            TableDB.getInstance().updateTable(table.getId(), tableName, capacity, status, floorNumber);
            tableController.loadTablesFromDatabase();
            handleCloseDialog();
        }
    }

    @FXML
    public void handleCloseDialog() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
