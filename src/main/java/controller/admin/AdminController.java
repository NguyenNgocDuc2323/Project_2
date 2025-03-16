package controller.admin;

import helper.AccountManager;
import helper.DB_Helper.Account_DB_Helper;
import helper.Navigator;
import helper.Translator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Account;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML private Button btn_add, btn_change_password, btn_log, btn_logout, btn_reset_password, btn_un_log, btn_manage_users;
    @FXML private TableView<Account> tbl_account;
    @FXML private TableColumn<Account, Integer> tbl_id;
    @FXML private TableColumn<Account, String> tbl_name, tbl_email, tbl_password, tbl_status, tbl_type;

    private Account selectedAccount;
    private ObservableList<Account> accountList;

    // 🛠 Các phần quản lý tài khoản CRUD
    @FXML private TableView<Account> tblUsers;
    @FXML private TableColumn<Account, Integer> colId;
    @FXML private TableColumn<Account, String> colName, colEmail, colType, colLockStatus;
    @FXML private TextField txtName, txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cbType, cbLockStatus;
    @FXML private Button btnAdd, btnUpdate, btnDelete;
    private ObservableList<Account> userList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadAccounts();
        loadUsers();
    }

    // 🛠 Load danh sách tài khoản vào bảng chính
    private void loadAccounts() {
        accountList = FXCollections.observableArrayList(AccountManager.getInstance().getAccounts());
        tbl_account.setItems(accountList);
        tbl_id.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        tbl_name.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
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

    // 🛠 Load danh sách User vào bảng quản lý tài khoản
    private void loadUsers() {
        List<Account> accounts = Account_DB_Helper.getAllAccounts();
        userList = FXCollections.observableArrayList(accounts);
        tblUsers.setItems(userList);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colType.setCellValueFactory(cellData -> cellData.getValue().typeAsStringProperty());
        colLockStatus.setCellValueFactory(cellData -> cellData.getValue().lockedAsStringProperty());

        cbType.setItems(FXCollections.observableArrayList("Admin", "Staff", "Student"));
        cbLockStatus.setItems(FXCollections.observableArrayList("Yes", "No"));
    }

    // 🔹 Thêm tài khoản mới
    @FXML
    private void handleAdd() {
        String name = txtName.getText();
        String email = txtEmail.getText();
        String password = txtPassword.getText();
        int type = cbType.getSelectionModel().getSelectedIndex() + 1;
        boolean locked = cbLockStatus.getValue().equals("Yes");

        Account newAccount = new Account(name, email, password, type, locked);
        Account_DB_Helper.addAccount(newAccount);
        loadUsers();
    }

    // 🔹 Cập nhật tài khoản
    @FXML
    private void handleUpdate() {
        Account selected = tblUsers.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setName(txtName.getText());
            selected.setEmail(txtEmail.getText());
            selected.setPassword(txtPassword.getText());
            selected.setType(cbType.getSelectionModel().getSelectedIndex() + 1);
            selected.setLocked(cbLockStatus.getValue().equals("Yes"));

            Account_DB_Helper.updateAccount(selected);
            loadUsers();
        }
    }

    // 🔹 Xóa tài khoản
    @FXML
    private void handleDelete() {
        Account selected = tblUsers.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Account_DB_Helper.deleteAccount(selected.getId());
            loadUsers();
        }
    }

    // 🔹 Mở giao diện quản lý người dùng
    @FXML
    void onManageUsers(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/manage_account/Admin/UserManagement.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("User Management");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load User Management.");
        }
    }

    // 🔹 Khóa tài khoản
    @FXML
    void onLockAccount(ActionEvent event) {
        Account account = tbl_account.getSelectionModel().getSelectedItem();
        if (account != null) {
            account.setLocked(true);
            tbl_account.refresh();
        }
    }

    // 🔹 Mở khóa tài khoản
    @FXML
    void unLockAccount(ActionEvent event) {
        Account account = tbl_account.getSelectionModel().getSelectedItem();
        if (account != null) {
            account.setLocked(false);
            tbl_account.refresh();
        }
    }

    // 🔹 Đặt lại mật khẩu
    @FXML
    public void onResetPassword(ActionEvent event) {
        if (selectedAccount != null) {
            try {
                Navigator.getInstance().gotoResetPasswordWithAccountId(selectedAccount.getId());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 🔹 Đổi mật khẩu
    @FXML
    void onChangePassword(ActionEvent event) {
        if (selectedAccount != null) {
            try {
                Navigator.getInstance().gotoChangePasswordWithAccountId(selectedAccount.getId());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 🔹 Đăng xuất
    @FXML
    void onLogout(ActionEvent event) {
        try {
            Navigator.getInstance().gotoLogin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 🔹 Chuyển đổi ngôn ngữ
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

    @FXML
    public void onAccountSelected(MouseEvent mouseEvent) {
        selectedAccount = tbl_account.getSelectionModel().getSelectedItem();
        if (selectedAccount != null) {
            // Cập nhật thông tin cho các TextField hoặc các thành phần giao diện khác
            txtName.setText(selectedAccount.getName());
            txtEmail.setText(selectedAccount.getEmail());
            // etc.
        }
    }

}
