package com.example.coffeeshop.controller;

import com.example.coffeeshop.database.TableDB;
import com.example.coffeeshop.helper.Alert;
import com.example.coffeeshop.model.Table;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class TableController implements Initializable {
    @FXML
    public Button deleteButton;
    @FXML
    private TreeView<String> tableTreeView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadTablesFromDatabase();
    }

    public void loadTablesFromDatabase() {
        List<Table> tables = TableDB.getInstance().getAllTables();
        Map<Integer, TreeItem<String>> floorNodes = new HashMap<>();
        TreeItem<String> root = new TreeItem<>();
        root.setExpanded(true);

        for (Table table: tables) {
            int floorNumber = table.getFloorNumber();
            TreeItem<String> floorNode = floorNodes.get(floorNumber);

            if (floorNode == null) {
                floorNode = new TreeItem<>("Floor " + floorNumber);
                floorNode.setExpanded(true);
                floorNodes.put(floorNumber, floorNode);
                root.getChildren().add(floorNode);
            }
            String tableInfo = String.format("%d - %s (%d seats, %s)", table.getId(), table.getTableName(), table.getCapacity(), table.getStatus());
            TreeItem<String> tableItem = new TreeItem<>(tableInfo);
            floorNode.getChildren().add(tableItem);
        }
        tableTreeView.setRoot(root);
        tableTreeView.setShowRoot(false);
    }

    @FXML
    private void handleCreateTable() {
        openDialog("Create Table", null);
    }

    @FXML
    private void handleUpdateTable() {
        TreeItem<String> selectedTable = tableTreeView.getSelectionModel().getSelectedItem();
        if (selectedTable != null) {
            String selectedTableValue = selectedTable.getValue();
            String[] parts = selectedTableValue.split(" ");
            int tableId = Integer.parseInt(parts[0]);
            Table table = TableDB.getInstance().getTableById(tableId);
            if (table != null) {
                openDialog("Update Table", table);
            }
        } else {
            Alert.showAlert("Please select a table to update.");
        }
    }

    @FXML
    private void handleDeleteTable() {
        TreeItem<String> selectedTable = tableTreeView.getSelectionModel().getSelectedItem();
        if (selectedTable != null) {
            String selectedTableValue = selectedTable.getValue();
            String[] parts = selectedTableValue.split(" ");
            int tableId = Integer.parseInt(parts[0]);
            deleteButton.setOnAction(event -> {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Confirmation");
                alert.setHeaderText("Are you sure you want to delete this item?");
                alert.setContentText("This action cannot be undone.");
                alert.showAndWait().ifPresent(response -> {
                    if (response.getText().equals("OK")) {
                        TableDB.getInstance().deleteTable(tableId);
                        loadTablesFromDatabase();
                    }
                });
            });
        } else {
            Alert.showAlert("Please select a table to delete.");
        }
    }

    private void openDialog(String title, Table table) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/coffeeshop/view/TableDialog.fxml"));
            Parent root = loader.load();
            TableDialogController controller = loader.getController();
            controller.setTable(table);
            controller.setTitle(title);
            controller.setTableController(this);
            Stage dialog = new Stage();
            dialog.setTitle(title);
            Scene dialogScene = new Scene(root);
            dialog.setScene(dialogScene);
            dialog.show();
        } catch (IOException e) {
            System.err.println(e.getMessage());;
        }
    }
}
