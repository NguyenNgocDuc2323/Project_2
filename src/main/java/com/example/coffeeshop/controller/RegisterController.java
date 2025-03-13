package com.example.coffeeshop.controller;

import com.example.coffeeshop.helper.Alert;
import com.example.coffeeshop.database.DB_Helper.AccountDB;
import com.example.coffeeshop.helper.Navigator;
import com.example.coffeeshop.helper.REGEX;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.mindrot.jbcrypt.BCrypt;

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
            Alert.showAlert("Invalid email format!");
            return;
        }

        if (!REGEX.isValidPassword(password)) {
            Alert.showAlert("Password must be at least 8 characters long and contain at least one letter, one number, and one special character.");
            return;
        }

        if (!password.equals(rePassword)) {
            Alert.showAlert("Passwords do not match!");
            return;
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

        com.example.coffeeshop.model.Account newAccount = new com.example.coffeeshop.model.Account(email, name, hashedPassword, 2, false);
        AccountDB.addAccount(newAccount);

        Alert.showSuccess("Registration successful! You can now log in.");
        goLogin(event);
    }
}
