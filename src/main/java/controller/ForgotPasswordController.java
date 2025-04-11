package controller;

import helper.Alert;
import helper.DB_Helper.Account;
import helper.Navigator;
import helper.REGEX;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ForgotPasswordController implements Initializable {
    @FXML
    private TextField emailField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField newPasswordField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @FXML
    void onResetPassword(ActionEvent event) {
        String email = emailField.getText();
        String name = nameField.getText();
        String newPassword = newPasswordField.getText();

        // Validate email format
        if (!REGEX.isValidEmail(email)) {
            Alert.showAlert("Invalid email format!");
            return;
        }

        // Validate password format
        if (!REGEX.isValidPassword(newPassword)) {
            Alert.showAlert("Password must be at least 8 characters long and contain at least one letter, one number, and one special character.");
            return;
        }

        // Get account by email
        model.Account account = Account.getAccountByEmail(email);
        if (account == null) {
            Alert.showAlert("No account found with this email.");
            return;
        }

        // Verify name matches
        if (!account.getName().equals(name)) {
            Alert.showAlert("Name does not match the account.");
            return;
        }

        // Hash the new password
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

        // Update password
        boolean success = Account.resetPassword(account.getId(), hashedPassword);
        if (success) {
            Alert.showSuccess("Password has been reset successfully!");
            try {
                Navigator.getInstance().gotoLogin();
            } catch (IOException e) {
                e.printStackTrace();
                Alert.showAlert("Error navigating to login page.");
            }
        } else {
            Alert.showAlert("Failed to reset password. Please try again.");
        }
    }

    @FXML
    void onBack(ActionEvent event) {
        try {
            Navigator.getInstance().gotoLogin();
        } catch (IOException e) {
            e.printStackTrace();
            Alert.showAlert("Error navigating to login page.");
        }
    }
}