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
    void onLogin(ActionEvent event) {
        try {
            Navigator.getInstance().gotoLogin();
        } catch (IOException e) {
            e.printStackTrace();
            Alert.showAlert("Error navigating to login page.");
        }
    }

    @FXML
    public void onForgotPassword(javafx.event.ActionEvent event) {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            System.out.println("Please enter your email.");
            return;
        }

        boolean exists = Account.checkEmailExists(email);

        if (exists) {
            int accountId = Account.getAccountIdByEmail(email);
            Alert.showSuccess("Email exists. Proceeding to reset password.");
            try {
                Navigator.getInstance().gotoResetPasswordWithAccountId(accountId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alert.showAlert("Email does not exist in the system.");
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