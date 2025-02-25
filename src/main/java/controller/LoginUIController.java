package controller;

import helper.DB_Helper.Account;
import helper.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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
        if (!emailFill.matches(EMAIL_REGEX)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Invalid email format!");
            alert.showAndWait();
            return;
        }
        if (!passFill.matches(PASSWORD_REGEX)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Password must be at least 8 characters long and contain at least one letter and one number.");
            alert.showAndWait();
            return;
        }

        model.Account acc = Account.getAccountByEmailAndPassword(emailFill, passFill);

        if (acc == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Invalid email or password");
            alert.showAndWait();
        } else {
            if (acc.isLocked()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Account is locked. Please contact the administrator.");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Login successful");
                alert.showAndWait();

                if (acc.getTypeAsString().equalsIgnoreCase("Admin")) {
                    try {
                        Navigator.getInstance().gotoAdminHome();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else if (acc.getTypeAsString().equalsIgnoreCase("Staff")) {
                    try {
                        Navigator.getInstance().gotoStaffDashboard();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
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
}
