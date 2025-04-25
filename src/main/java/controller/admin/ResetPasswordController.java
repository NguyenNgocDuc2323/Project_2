package controller.admin;

import helper.Alert;
import helper.DB_Helper.Account;
import helper.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javafx.event.ActionEvent;

import java.io.IOException;
public class ResetPasswordController {
    @FXML
    private Button btn_reset_password;

    @FXML
    private PasswordField txt_confirm_password;

    @FXML
    private TextField txt_email;  // TextField hiển thị email

    @FXML
    private PasswordField txt_password;
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    private int accountId;

    // Phương thức setAccountId nhận accountId và tự lấy email từ DB
    public void setAccountId(int accountId) {
        this.accountId = accountId;
        // Giả sử bạn có method Account.getEmailByAccountId(accountId) để lấy email
        String email = Account.getEmailByAccountId(accountId);
        txt_email.setText(email);
    }

    @FXML
    public void onResetPassword(ActionEvent event) {
        if (accountId == 0) {
            Alert.showAlert("Invalid email. Please enter a valid email.");
            return;
        }

        String password = txt_password.getText();
        String confirmPassword = txt_confirm_password.getText();
        if (!password.matches(PASSWORD_REGEX)) {
            helper.Alert.showAlert("Password ");
        }
        if (!password.equals(confirmPassword)) {
            Alert.showAlert("Passwords do not match");
            return;
        }
        boolean isResetSuccessful = Account.resetPassword(accountId, password);
        if (isResetSuccessful) {
            Alert.showSuccess("Password Reset Successfully.");
            try {
                Navigator.getInstance().gotoLogin();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            Alert.showAlert("Something went wrong.");
        }
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

