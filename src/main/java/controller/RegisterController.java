package controller;

import helper.DB_Helper.Account;
import helper.Navigator;
import helper.REGEX;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.awt.*;
import java.io.IOException;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController implements Initializable {

    @FXML
    private Button btn_register;

    @FXML
    private TextField register_name;
    @FXML
    private TextField register_email;

    @FXML
    private TextField register_password;

    @FXML
    private TextField register_repassword;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


    @FXML
    public void goLogin(ActionEvent event) {
        try {
            Navigator.getInstance().gotoLogin();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void onRegister(ActionEvent event) {
        String email = register_email.getText();
        String password = register_password.getText();
        String rePassword = register_repassword.getText();
        String name = register_name.getText();
        if (!REGEX.isValidEmail(email)) {
            showAlert("Invalid email format!");
            return;
        }

        if (!REGEX.isValidPassword(password)) {
            showAlert("Password must be at least 8 characters long and contain at least one letter, one number, and one special character.");
            return;
        }

        if (!password.equals(rePassword)) {
            showAlert("Passwords do not match!");
            return;
        }

        model.Account newAccount = new model.Account(email,name, password, 2, false);
        Account.addAccount(newAccount);

        showSuccess("Registration successful! You can now log in.");
        goLogin(event);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
