package controller.admin;
import helper.Alert;
import helper.DB_Helper.Account;
import model.DisplayText;
import helper.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AddAccountController implements Initializable {

    @FXML
    private ComboBox<DisplayText> txt_acc_type;

    @FXML
    private ComboBox<DisplayText> txt_acc_lock_status;


    @FXML
    private PasswordField txt_confirm_password;

    @FXML
    private TextField txt_email;
    @FXML
    private TextField txt_name;
    @FXML
    private PasswordField txt_password;
    @FXML
    private Button btn_add_new_acc;
    @FXML
    void onSaveAccount() {
        String email = txt_email.getText();
        String password = txt_password.getText();
        System.out.println("New Account: " + email + " - " + password);
        btn_add_new_acc.getScene().getWindow().hide();
    }

    @FXML
    void onCancel() {
        btn_add_new_acc.getScene().getWindow().hide();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txt_acc_type.getItems().addAll(
                new DisplayText("Admin", 1),
                new DisplayText("Staff", 2),
                new DisplayText("Student", 3)
        );
        txt_acc_lock_status.getItems().addAll(
                new DisplayText("Locked", 1),
                new DisplayText("Open", 0)
        );
    }
    @FXML
    void addNewAcc(ActionEvent event) {
        String email = txt_email.getText();
        String password = txt_password.getText();
        String confirmPassword = txt_confirm_password.getText();
        String name = txt_name.getText();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Alert.showAlert("All fields must be filled out.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            Alert.showAlert("Passwords do not match.");
            return;
        }

        if (Account.getAccountByEmail(email) != null) {
            Alert.showAlert("An account with this email already exists.");
            return;
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

        int accNumber = Account.countAcc();
        int type = (txt_acc_type != null && txt_acc_type.getValue() != null) ? txt_acc_type.getValue().getValue() : 0;
        boolean lockStatus = (txt_acc_lock_status != null && txt_acc_lock_status.getValue() != null) && txt_acc_lock_status.getValue().getValue() == 1;

        model.Account newAccount = new model.Account(accNumber + 1, name, email, hashedPassword, type, lockStatus);
        Account.addAccount(newAccount);

        try {
            Alert.showSuccess("New account added successfully!");
            Navigator.getInstance().gotoAdminHome();
        } catch (IOException e) {
            e.printStackTrace();
            Alert.showAlert("An error occurred while navigating to Admin Home.");
        }
    }




}
