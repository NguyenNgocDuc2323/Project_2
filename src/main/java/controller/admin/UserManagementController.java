package controller.admin;

import helper.Alert;
import helper.Navigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Account;
import database.AccountDAO;

import java.io.IOException;
import java.util.Optional;

public class UserManagementController {
    @FXML
    private TableView<Account> userTableView;
    @FXML
    private TableColumn<Account, Integer> idColumn;
    @FXML
    private TableColumn<Account, String> fullNameColumn;
    @FXML
    private TableColumn<Account, String> emailColumn;
    @FXML
    private TableColumn<Account, String> passwordColumn;
    @FXML
    private TableColumn<Account, String> roleColumn;
    @FXML
    private TableColumn<Account, String> lockStatusColumn;

    private ObservableList<Account> userList;
    private AccountDAO accountDAO;

    @FXML
    public void initialize() {
        accountDAO = new AccountDAO();
        userList = FXCollections.observableArrayList();

        // Initialize table columns
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        fullNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        passwordColumn.setCellValueFactory(cellData -> cellData.getValue().passwordProperty());
        roleColumn.setCellValueFactory(cellData -> cellData.getValue().typeAsStringProperty());
        lockStatusColumn.setCellValueFactory(cellData -> cellData.getValue().lockedAsStringProperty());
        
        // Set row factory to highlight locked accounts
        userTableView.setRowFactory(tv -> {
            TableRow<Account> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null && newItem.isLocked()) {
                    row.setStyle("-fx-background-color: #eee;-fx-text-fill: #fff");
                } else {
                    row.setStyle("");
                }
            });
            return row;
        });

        loadUsers();
    }

    private void loadUsers() {
        try {
            userList.clear();
            userList.addAll(accountDAO.getAllAccounts());
            userTableView.setItems(userList);
        } catch (Exception e) {
            Alert.showAlert("Failed to load users");
        }
    }

    @FXML
    private void handleAddButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/manage_account/Admin/UserDialog.fxml"));
            Scene scene = new Scene(loader.load());
            
            UserDialogController controller = loader.getController();
            controller.setMode("ADD");
            controller.setAccount(null); // null for new account
            
            controller.setOnUserSavedCallback(success -> {
                if (success) loadUsers();
            });
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add New User");
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            Alert.showAlert("Failed to open add user form");
        }
    }

    @FXML
    private void handleEditButton() {
        Account selectedUser = userTableView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            Alert.showAlert("Please select a user to edit");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/manage_account/Admin/UserDialog.fxml"));
            Scene scene = new Scene(loader.load());
            
            UserDialogController controller = loader.getController();
            controller.setMode("EDIT");
            controller.setAccount(selectedUser); // Pass the selected user for editing
            
            controller.setOnUserSavedCallback(success -> {
                if (success) loadUsers();
            });
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Edit User");
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            Alert.showAlert("Failed to edit user");
        }
    }

    @FXML
    private void handleDeleteButton() {
        Account selectedUser = userTableView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            Alert.showAlert("Please select a user to delete");
            return;
        }

        if (Alert.confirm("Are you sure you want to delete this user?")) {
            try {
                boolean deleted = accountDAO.deleteAccount(selectedUser.getId());
                if (deleted) {
                    loadUsers();
                    Alert.showSuccess("User deleted successfully");
                } else {
                    Alert.showAlert("Failed to delete user");
                }
            } catch (Exception e) {
                Alert.showAlert("Failed to delete user: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

}