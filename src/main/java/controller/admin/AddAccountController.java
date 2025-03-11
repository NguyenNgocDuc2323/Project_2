package controller.admin;

import helper.DB_Helper.Account;
import model.displayText.DisplayText;
import helper.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

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
            System.out.println("Please fill all fields.");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setContentText("All fields must be filled out.");
            alert.showAndWait();
            return;
        }
        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match.");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setContentText("Passwords do not match.");
            alert.showAndWait();
            return;
        }
        DisplayText selectedType = txt_acc_type.getValue();
        int type = selectedType != null ? selectedType.getValue() : 0;
        DisplayText selectedLockStatus = txt_acc_lock_status.getValue();
        boolean lockStatus = selectedLockStatus != null && selectedLockStatus.getValue() == 1;
        int accNumber = Account.countAcc();
        model.Account newAccount = new model.Account(accNumber + 1,name,email, password, type, lockStatus);
        if (Account.getAccountByEmailAndPassword(email,password) != null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setContentText("An account with this email already exists.");
            alert.showAndWait();
            return;
        }
        Account.addAccount(newAccount);
        try {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Add Account Successfully!");
            alert.setContentText("New account added successfully!");
            alert.showAndWait();
            Navigator.getInstance().gotoAdminHome();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("An error occurred while navigating to Admin Home.");
            alert.showAndWait();
        }
    }



}
