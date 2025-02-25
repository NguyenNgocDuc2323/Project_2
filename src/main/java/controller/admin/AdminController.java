package controller.admin;

import helper.AccountManager;
import helper.Navigator;
import helper.Translator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.Account;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
public class AdminController implements Initializable {
    @FXML
    private Button btn_add;

    @FXML
    private Button btn_change_password;

    @FXML
    private Button btn_log;

    @FXML
    private Button btn_logout;

    @FXML
    private Button btn_reset_password;

    @FXML
    private Button btn_un_log;

    @FXML
    private TableView<Account> tbl_account;

    @FXML
    private TableColumn<Account, String> tbl_email;

    @FXML
    private TableColumn<Account, String> tbl_password;

    @FXML
    private TableColumn<Account, String> tbl_status;

    @FXML
    private TableColumn<Account, String> tbl_type;
    @FXML
    private TableColumn<Account, Integer> tbl_id;
    private Account selectedAccount;
    ObservableList<Account> accountList;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        accountList = FXCollections.observableArrayList(AccountManager.getInstance().getAccounts());
        tbl_account.setItems(accountList);
        tbl_id.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        tbl_email.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        tbl_password.setCellValueFactory(cellData -> cellData.getValue().passwordProperty());
        tbl_type.setCellValueFactory(cellData -> cellData.getValue().typeAsStringProperty());
        tbl_status.setCellValueFactory(cellData -> cellData.getValue().lockedAsStringProperty());
        tbl_account.setRowFactory(tv -> {
            TableRow<Account> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null && newItem.isLocked()) {
                    row.setStyle("-fx-background-color: #eee;-fx-text-fill: #fff");
                } else {
                    row.setStyle("");
                }
            });
            return row;
        });
    }


    @FXML
    void onAddAccount(ActionEvent event) {
        try {
            Navigator.getInstance().gotoAddNewAccount();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to navigate to Add New Account screen.");
        }
    }

    @FXML
    void onLockAccount(ActionEvent event) {
        Account account = tbl_account.getSelectionModel().getSelectedItem();
        if (account == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Account Selected");
            alert.setContentText("Please select an account first");
            alert.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setContentText("Are you sure you want to Lock this account?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                account.setLocked(true);
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Confirmation Dialog");
                alert1.setContentText("Account Locked");
                alert1.show();
                tbl_account.refresh();
            } else {
                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                alert1.setTitle("Confirmation Dialog");
                alert1.setContentText("Failed to lock this account");
                alert1.show();
            }
        }
    }

    @FXML
    void unLockAccount(ActionEvent event) {
        Account account = tbl_account.getSelectionModel().getSelectedItem();
        if (account == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Account Selected");
            alert.setContentText("Please select an account first");
            alert.show();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setContentText("Are you sure you want to Unlock this account?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                account.setLocked(false);
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Confirmation Dialog");
                alert1.setContentText("Account Unlocked");
                alert1.show();
                tbl_account.refresh();
            }
            else {
                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                alert1.setTitle("Confirmation Dialog");
                alert1.setContentText("Failed to unlock this account");
                alert1.show();
            }
        }
    }

    @FXML
    public void onAccountSelected(javafx.scene.input.MouseEvent mouseEvent) {
        selectedAccount = tbl_account.getSelectionModel().getSelectedItem();
    }
    @FXML
    public void onResetPassword(ActionEvent event) {
        if (selectedAccount != null) {
            int accountId = selectedAccount.getId();
            try {
                Navigator.getInstance().gotoResetPasswordWithAccountId(accountId);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to navigate to Reset Password screen.");
            }
        } else {
            System.out.println("No account selected");
        }
    }
    @FXML
    void onLogout(ActionEvent event) {
        try {
            Navigator.getInstance().gotoLogin();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    @FXML
    void onChangePassword(ActionEvent event) {
        if (selectedAccount != null) {
            int accountId = selectedAccount.getId();
            try {
                Navigator.getInstance().gotoChangePasswordWithAccountId(accountId);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to navigate to Reset Password screen.");
            }
        } else {
            System.out.println("No account selected");
        }
    }
    @FXML
    void handleChangeLanguage(ActionEvent event) {
        Locale currentLocale = Translator.getLocale();
        if (currentLocale.getLanguage().equals("en")) {
            Translator.setLocale(new Locale("vi", "VN"));
        } else {
            Translator.setLocale(new Locale("en", "US"));
        }

        reloadUI();
    }

    private void reloadUI() {
        try {
            Navigator.getInstance().gotoAdminHome();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
