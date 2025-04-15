package controller.admin;

import helper.Alert;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Account;
import model.DisplayText;
import database.AccountDAO;
import org.mindrot.jbcrypt.BCrypt;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class UserDialogController implements Initializable {
    @FXML private Label titleLabel;
    @FXML private TextField idField;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<DisplayText> userTypeComboBox;
    @FXML private ComboBox<DisplayText> lockStatusComboBox;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    
    private AccountDAO accountDAO;
    private Account account;
    private String mode;
    private Consumer<Boolean> onSavedCallback;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        accountDAO = new AccountDAO();
        
        // Initialize combo boxes
        userTypeComboBox.setItems(FXCollections.observableArrayList(
                new DisplayText("Admin", Account.TYPE_ADMIN),
                new DisplayText("Staff", Account.TYPE_STAFF),
                new DisplayText("Student", Account.TYPE_STUDENT)
        ));
        
        lockStatusComboBox.setItems(FXCollections.observableArrayList(
                new DisplayText("Open", 0),
                new DisplayText("Locked", 1)
        ));
        
        // Default values
        userTypeComboBox.getSelectionModel().select(1); // Staff by default
        lockStatusComboBox.getSelectionModel().select(0); // Open by default
    }
    
    public void setMode(String mode) {
        this.mode = mode;
    }
    
    public void setOnUserSavedCallback(Consumer<Boolean> callback) {
        this.onSavedCallback = callback;
    }
    
    public void setAccount(Account account) {
        this.account = account;
        
        if ("EDIT".equals(mode)) {
            titleLabel.setText("Edit User");
            idField.setText(String.valueOf(account.getId()));
            fullNameField.setText(account.getName());
            emailField.setText(account.getEmail());
            passwordField.setPromptText("Leave blank to keep current password");
            
            // Set user type
            for (DisplayText item : userTypeComboBox.getItems()) {
                if (item.getValue() == account.getType()) {
                    userTypeComboBox.getSelectionModel().select(item);
                    break;
                }
            }
            
            // Set lock status
            int lockValue = account.isLocked() ? 1 : 0;
            for (DisplayText item : lockStatusComboBox.getItems()) {
                if (item.getValue() == lockValue) {
                    lockStatusComboBox.getSelectionModel().select(item);
                    break;
                }
            }
        } else {
            titleLabel.setText("Add New User");
        }
    }
    
    @FXML
    private void handleSave(ActionEvent event) {
        if (!validateInput()) {
            return;
        }
        
        try {
            String fullName = fullNameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();
            int type = userTypeComboBox.getValue().getValue();
            boolean locked = lockStatusComboBox.getValue().getValue() == 1;
            
            boolean success = false;
            
            if ("EDIT".equals(mode)) {
                // Update existing account
                account.setName(fullName);
                account.setEmail(email);
                if (!password.isEmpty()) {
                    String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
                    account.setPassword(hashedPassword);
                }
                account.setType(type);
                account.setLocked(locked);
                
                success = accountDAO.updateAccount(account);
                
                if (success) {
                    Alert.showSuccess("User updated successfully");
                } else {
                    Alert.showAlert("Failed to update user");
                    return;
                }
            } else {
                // Create new account
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
                Account newAccount = new Account(0, fullName, email, hashedPassword, type, locked);
                
                success = accountDAO.addAccount(newAccount);
                
                if (success) {
                    Alert.showSuccess("User added successfully");
                } else {
                    Alert.showAlert("Failed to add user");
                    return;
                }
            }
            
            if (onSavedCallback != null) {
                onSavedCallback.accept(success);
            }
            
            closeDialog();
        } catch (Exception e) {
            Alert.showAlert("Error processing user data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private boolean validateInput() {
        if (fullNameField.getText().trim().isEmpty()) {
            Alert.showAlert("Full name is required");
            fullNameField.requestFocus();
            return false;
        }
        
        if (emailField.getText().trim().isEmpty()) {
            Alert.showAlert("Email is required");
            emailField.requestFocus();
            return false;
        }
        
        if (userTypeComboBox.getValue() == null) {
            Alert.showAlert("User type must be selected");
            userTypeComboBox.requestFocus();
            return false;
        }
        
        if (lockStatusComboBox.getValue() == null) {
            Alert.showAlert("Lock status must be selected");
            lockStatusComboBox.requestFocus();
            return false;
        }
        
        return true;
    }
    
    @FXML
    private void handleCancel(ActionEvent event) {
        closeDialog();
    }
    
    private void closeDialog() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}