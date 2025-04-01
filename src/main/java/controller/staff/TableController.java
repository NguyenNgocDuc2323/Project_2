package controller.staff;

import database.TableDB;
import helper.Alert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Table;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

public class TableController implements Initializable {
    @FXML
    public Button deleteButton;
    @FXML
    private TableView<Table> tableTable;
    @FXML
    private TableColumn<Table, Integer> idColumn;
    @FXML
    private TableColumn<Table, String> nameColumn;
    @FXML
    private TableColumn<Table, Integer> capacityColumn;
    @FXML
    private TableColumn<Table, String> statusColumn;
    @FXML
    private TableColumn<Table, Integer> floorColumn;
    @FXML
    private ComboBox<Integer> floorFilterComboBox;
    private final ObservableList<Table> tableObservableList = FXCollections.observableArrayList();
    private final ObservableList<Table> filteredTableObservableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("tableName"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        floorColumn.setCellValueFactory(new PropertyValueFactory<>("floorNumber"));
        loadTablesFromDatabase();
    }

    private void loadTablesFromDatabase() {
        tableObservableList.clear();
        tableObservableList.addAll(TableDB.getInstance().getAllTables());
        tableTable.setItems(tableObservableList);
        populateFloorFilter();
    }

    private void populateFloorFilter() {
        Set<Integer> floorNumbers = tableObservableList.stream()
                .map(Table::getFloorNumber)
                .collect(Collectors.toSet());
        ObservableList<Integer> floorOptions = FXCollections.observableArrayList(floorNumbers);
        floorOptions.addFirst(null);
        floorFilterComboBox.setItems(floorOptions);
    }

    @FXML
    private void handleFilterByFloor() {
        Integer selectedFloor = floorFilterComboBox.getValue();
        if (selectedFloor == null) {
            tableTable.setItems(tableObservableList);
        } else {
            filteredTableObservableList.setAll(tableObservableList.stream()
                    .filter(table -> Objects.equals(table.getFloorNumber(), selectedFloor))
                    .collect(Collectors.toList()));
            tableTable.setItems(filteredTableObservableList);
        }
    }

    @FXML
    private void handleCreateTable() {
        openDialog("Create Table", null);
        loadTablesFromDatabase();
    }

    @FXML
    private void handleUpdateTable() {
        Table selected = tableTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            openDialog("Update Table", selected);
            loadTablesFromDatabase();
        } else {
            Alert.showAlert("Please select table to update.");
        }
    }

    @FXML
    private void handleDeleteTable() {
        Table selected = tableTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            int tableId = selected.getId();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/manage_account/Staff/TableDialog.fxml"));
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
