package controller;

import database.TableDB;
import helper.Alert;
import model.Table;
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

    private void loadTablesFromDatabase() {
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
            String tableInfo = String.format("%d - %s: %d seats, %s", table.getId(), table.getTableName(), table.getCapacity(), table.getStatus());
            TreeItem<String> tableItem = new TreeItem<>(tableInfo);
            floorNode.getChildren().add(tableItem);
        }
        tableTreeView.setRoot(root);
        tableTreeView.setShowRoot(false);
    }

    @FXML
    private void handleCreateTable() {
        openDialog("Create Table", null);
        loadTablesFromDatabase();
    }

    @FXML
    private void handleUpdateTable() {
        TreeItem<String> selected = tableTreeView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String selectedTableValue = selected.getValue();
            String[] parts = selectedTableValue.split(" ");
            int tableId = Integer.parseInt(parts[0]);
            Table table = TableDB.getInstance().getTableById(tableId);
            if (table != null) {
                openDialog("Update Table", table);
                loadTablesFromDatabase();
            } else {
                Alert.showAlert("Error getting table with id: " + tableId);
            }
        } else {
            Alert.showAlert("Please select table to update.");
        }
    }

    @FXML
    private void handleDeleteTable() {
        TreeItem<String> selected = tableTreeView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String selectedTableValue = selected.getValue();
            String[] parts = selectedTableValue.split(" ");
            int tableId = Integer.parseInt(parts[0]);
            deleteButton.setOnAction(event -> {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Confirmation");
                alert.setHeaderText("Are you sure you want to delete this item?");
                alert.setContentText("This action cannot be reversed.");
                alert.showAndWait().ifPresent(response -> {
                    if (response.getText().equals("OK")) {
                        TableDB.getInstance().deleteTable(tableId);
                        loadTablesFromDatabase();
                    }
                });
            });
        } else {
            Alert.showAlert("Please select table to delete.");
        }
    }

    private void openDialog(String title, Table table) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/manage_account/TableDialog.fxml"));
            Parent root = loader.load();
            TableDialogController controller = loader.getController();
            controller.setTable(table);
            controller.setTitle(title);
            Stage dialog = new Stage();
            dialog.setTitle(title);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
