package com.example.coffeeshop.controller;
import com.example.coffeeshop.helper.Alert;
import com.example.coffeeshop.database.DB_Helper.AccountDB;
import com.example.coffeeshop.helper.Navigator;
import com.example.coffeeshop.helper.REGEX;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginUIController implements Initializable {
    private String email;
    private String password;
    @FXML
    private Button btn_login;

    @FXML
    private TextField txt_email;
    private com.example.coffeeshop.model.Account selectedAccount;
    @FXML
    private PasswordField txt_password;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";


    public LoginUIController() {
    }

    public LoginUIController(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public void login() throws SQLException {
        String passFill = txt_password.getText();
        String emailFill = txt_email.getText();

        if (!REGEX.isValidEmail(emailFill)) {
            com.example.coffeeshop.helper.Alert.showAlert("Invalid email format!");
            return;
        }
        if (!REGEX.isValidPassword(passFill)) {
            Alert.showAlert("Password must be at least 8 characters long and contain at least one letter, one number, and one special character.");
            return;
        }

        com.example.coffeeshop.model.Account acc = AccountDB.getAccountByEmail(emailFill);

        if (acc == null || !BCrypt.checkpw(passFill, acc.getPassword())) {
            Alert.showAlert("Invalid email or password");
            return;
        }

        if (acc.isLocked()) {
            Alert.showAlert("AccountDB is locked. Please contact the administrator.");
            return;
        }

        Alert.showSuccess("Login successful");

        try {
            if (acc.getTypeAsString().equalsIgnoreCase("Admin")) {
                Navigator.getInstance().gotoAdminHome();
            } else if (acc.getTypeAsString().equalsIgnoreCase("Staff")) {
                Navigator.getInstance().gotoStaffDashboard();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void onResetPassword(ActionEvent event) {
        try {
            Navigator.getInstance().gotoResetPassword();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btn_login.setOnAction(event -> {
            try {
                login();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
    @FXML
    void goRegister(ActionEvent event) {
        try {
            Navigator.getInstance().gotoRegister();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
