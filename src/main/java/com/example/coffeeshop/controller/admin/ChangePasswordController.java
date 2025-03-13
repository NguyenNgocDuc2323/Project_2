package com.example.coffeeshop.controller.admin;
import com.example.coffeeshop.database.DB_Helper.AccountDB;
import com.example.coffeeshop.helper.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javafx.event.ActionEvent;

import java.io.IOException;


public class ChangePasswordController {
    @FXML
    private PasswordField txt_confirm_password;

    @FXML
    private TextField txt_email;

    @FXML
    private PasswordField txt_password;
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    private int accountId;

    public void setAccountId(int accountId) {
        this.accountId = accountId;
        String email = AccountDB.getEmailByAccountId(accountId);
        txt_email.setText(email);
    }
    private void fetchAccountIdFromEmail() {
        if (accountId == 0) {
            String email = txt_email.getText();
            if (email != null && !email.isEmpty()) {
                accountId = AccountDB.getAccountIdByEmail(email);
            }
        }
    }

    @FXML
    public void onChangePassword(javafx.event.ActionEvent event) {
        fetchAccountIdFromEmail();
        if (accountId == 0) {
            showAlert(Alert.AlertType.ERROR, "Change Password", "Invalid email. Please enter a valid email.");
            return;
        }

        String password = txt_password.getText();
        String confirmPassword = txt_confirm_password.getText();
        if (!password.matches(PASSWORD_REGEX)) {
            showAlert(Alert.AlertType.ERROR, "Change Password", "Password must be at least 8 characters long, contain at least one letter, one number, and one special character (@$!%*?&).");
            return;
        }
        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Change Password", "Passwords do not match.");
            return;
        }
        boolean isResetSuccessful = AccountDB.resetPassword(accountId, password);
        if (isResetSuccessful) {
            showAlert(Alert.AlertType.INFORMATION, "Change Password", "Password Change Successfully.");
            try {
                Navigator.getInstance().gotoAdminHome();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Change Password", "Something went wrong.");
        }
    }
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    public void onGoHomePage(ActionEvent event) {
        try {
            Navigator.getInstance().gotoAdminHome();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
