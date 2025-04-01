package controller;
import helper.Alert;
import helper.DB_Helper.Account;
import helper.Navigator;
import helper.REGEX;
import helper.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class LoginUIController implements Initializable {
    private String email;
    private String password;
    @FXML
    private Button btn_login;

    @FXML
    private TextField txt_email;
    private model.Account selectedAccount;
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
            helper.Alert.showAlert("Invalid email format!");
            return;
        }
        if (!REGEX.isValidPassword(passFill)) {
            Alert.showAlert("Password must be at least 8 characters long and contain at least one letter, one number, and one special character.");
            return;
        }

        model.Account acc = Account.getAccountByEmail(emailFill);

        if (acc == null || !BCrypt.checkpw(passFill, acc.getPassword())) {
            Alert.showAlert("Invalid email or password");
            return;
        }

        if (acc.isLocked()) {
            Alert.showAlert("Account is locked. Please contact the administrator.");
            return;
        }

        // Set the current user in the session
        Session.getInstance().setCurrentUser(acc);

        Alert.showSuccess("Login successful");

        try {
            switch (acc.getType()) {
                case model.Account.TYPE_ADMIN:  // Type 1 = admin
                    Navigator.getInstance().gotoAdminHome();
                    break;
                case model.Account.TYPE_STAFF:  // Type 2 = staff
                    Navigator.getInstance().gotoMenu();
                    break;
//                case model.Account.TYPE_GUEST:  // Type 3 = employee
//                    break;
                default:
                    Alert.showAlert("Unknown account type");
                    break;
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
