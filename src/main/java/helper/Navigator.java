package helper;

import java.io.IOException;

import controller.OrderDetailController;
import controller.admin.ChangePasswordController;
import controller.admin.ResetPasswordController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Navigator {

    private static Navigator navigator;
    private Stage state;
    private FXMLLoader fxLoader;

    public static final String ADMIN_MENU = "/com/example/manage_account/AdminMenuUI.fxml";
    public static final String LOGIN_SCENE = "/com/example/manage_account/Auth/LoginUI.fxml";
    public static final String REGISTER_SCENE = "/com/example/manage_account/Auth/RegisterUI.fxml";
    public static final String ADMIN_HOME = "/com/example/manage_account/Admin/admin_dashboard.fxml";
    public static final String CALCULATE_HOME = "/com/example/manage_account/CalculateUI.fxml";
    public static final String RESET_PASSWORD = "/com/example/manage_account/Admin/reset_password.fxml";
    public static final String FORGOT_PASSWORD = "/com/example/manage_account/Auth/ForgotPassword.fxml";
    public static final String CHANGE_PASSWORD = "/com/example/manage_account/Admin/change_password.fxml";
    public static final String MENU_SCENE = "/com/example/manage_account/CoffeeShop/Dashboard.fxml";
    public static final String TABLE = "/com/example/manage_account/Table.fxml";
    public static final String ORDER = "/com/example/manage_account/Order.fxml";
    public static final String ORDER_DIALOG = "/com/example/manage_account/OrderDialog.fxml";
    public static final String ORDER_DETAIL = "/com/example/manage_account/OrderDetail.fxml";

    private Navigator() {
    }

    public static Navigator getInstance() {
        if (navigator == null) {
            navigator = new Navigator();
        }
        return navigator;
    }

    public void setState(Stage state) {
        navigator.state = state;
    }

    public void gotoScene(String title, String URL) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource(URL), Translator.getResourceBundle());
        Parent root = loader.load();
        Scene scene = new Scene(root);
        state.setTitle(title);
        state.setScene(scene);
        state.show();
    }

    public void gotoLogin() throws IOException {
        gotoScene(Translator.translate("title.login"), LOGIN_SCENE);
    }

    public void gotoForgotPassword() throws IOException {
        gotoScene(Translator.translate("title.forgotPassword"), FORGOT_PASSWORD);
    }

    public void gotoAdminHome() throws IOException {
        gotoScene(Translator.translate("title.adminHome"), ADMIN_HOME);
    }

    public void gotoCalculate() throws IOException {
        gotoScene("Calculator", CALCULATE_HOME);
    }

    public void gotoAddNewAccount() throws IOException {
        gotoScene(Translator.translate("title.addNewAccount"), "/com/example/manage_account/Admin/add_new_account.fxml");
    }

    public void gotoResetPassword() throws IOException {
        gotoScene(Translator.translate("title.resetPassword"), "/com/example/manage_account/Admin/reset_password.fxml");
    }

    public void gotoStaffDashboard() throws IOException {
        gotoScene(Translator.translate("title.staffDashboard"), "/com/example/manage_account/Staff/staff_dashboard.fxml");
    }

    public void gotoRegister() throws IOException {
        gotoScene(Translator.translate("title.register"), REGISTER_SCENE);
    }

    public void gotoResetPasswordWithAccountId(int accountId) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(RESET_PASSWORD), Translator.getResourceBundle());
        Parent root = loader.load();
        ResetPasswordController controller = loader.getController();
        controller.setAccountId(accountId);
        Scene scene = new Scene(root);
        state.setTitle(Translator.translate("title.resetPassword"));
        state.setScene(scene);
        state.show();
    }

    public void gotoChangePasswordWithAccountId(int accountId) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(CHANGE_PASSWORD), Translator.getResourceBundle());
        Parent root = loader.load();
        ChangePasswordController controller = loader.getController();
        controller.setAccountId(accountId);
        Scene scene = new Scene(root);
        state.setTitle(Translator.translate("title.changePassword"));
        state.setScene(scene);
        state.show();
    }

    public void gotoMenu() throws IOException {
        gotoScene("Menu", MENU_SCENE);
    }

    public void gotoTable() throws IOException {
        gotoScene("Table Management", TABLE);
    }

    public void gotoOrder() throws IOException {
        gotoScene("Order Management", ORDER);
    }

    public void gotoOrderDetail(int orderId) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(ORDER_DETAIL));
        Parent root = loader.load();
        OrderDetailController controller = loader.getController();
        controller.setOrderId(orderId);
        state.setTitle("Order Detail Management");
        state.setScene(new Scene(root));
        state.show();
    }
}