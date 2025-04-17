package helper;

import java.io.IOException;

import controller.staff.OrderDetailController;
import controller.admin.ChangePasswordController;
import controller.admin.ResetPasswordController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import controller.admin.CoffeeDialogController;
import javafx.stage.Modality;
import model.CoffeeShop.Coffee;
import java.util.function.Consumer;

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
    public static final String TABLE_MANAGEMENT = "/com/example/manage_account/Staff/Table.fxml";
    public static final String TABLE_DIALOG = "/com/example/manage_account/Staff/TableDialog.fxml";
    public static final String ORDER_MANAGEMENT = "/com/example/manage_account/Staff/Order.fxml";
    public static final String ORDER_DIALOG = "/com/example/manage_account/Staff/OrderDialog.fxml";
    public static final String PRODUCT_MANAGE = "/com/example/manage_account/Admin/Product.fxml";
    public static final String COFFEE_DIALOG = "/com/example/manage_account/CoffeeShop/add_edit_coffee.fxml";
    public static final String ORDER_DETAIL_MANAGEMENT = "/com/example/manage_account/Staff/OrderDetail.fxml";
    public static final String ORDER_DETAIL_DIALOG = "/com/example/manage_account/Staff/OrderDetailDialog.fxml";
    public static final String ORDER_STATISTIC = "/com/example/manage_account/Staff/OrderStatistic.fxml";
    public static final String CATEGORY_MANAGEMENT = "/com/example/manage_account/Staff/Category.fxml";
    public static final String CATEGORY_DIALOG = "/com/example/manage_account/Staff/CategoryDialog.fxml";
    public static final String PROFILE = "/com/example/manage_account/CoffeeShop/ProfileView.fxml";

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

    public void gotoResetPasswordWithAccountId(int accountId) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/manage_account/Auth/ResetPassword.fxml"), Translator.getResourceBundle());
        Parent root = loader.load();
        ResetPasswordController controller = loader.getController();
        controller.setAccountId(accountId);
        Scene scene = new Scene(root);
        state.setTitle(Translator.translate("title.resetPassword"));
        state.setScene(scene);
        state.show();
    }




    public void gotoStaffDashboard() throws IOException {
        gotoScene(Translator.translate("title.staffDashboard"), "/com/example/manage_account/Staff/staff_dashboard.fxml");
    }

    public void gotoRegister() throws IOException {
        gotoScene(Translator.translate("title.register"), REGISTER_SCENE);
    }

    public void gotoChangePasswordWithAccountId(int accountId) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(RESET_PASSWORD), Translator.getResourceBundle());
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

    public void gotoOrderDetailManagement(int orderId) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(ORDER_DETAIL_MANAGEMENT));
        Parent root = loader.load();
        OrderDetailController controller = loader.getController();
        controller.setOrderId(orderId);
        Stage dialogStage = new Stage();
        dialogStage.setScene(new Scene(root));
        dialogStage.setTitle("Order Detail");
        dialogStage.showAndWait();
    }

    public void gotoProductManage() throws IOException {
        gotoScene("Product Management", PRODUCT_MANAGE);
    }

    public void gotoCoffeeDialog(String mode, Coffee coffee, Consumer<Boolean> onSavedCallback) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(COFFEE_DIALOG), Translator.getResourceBundle());
        Parent root = loader.load();

        CoffeeDialogController controller = loader.getController();
        controller.setMode(mode);
        if (coffee != null) {
            controller.setCoffee(coffee);
        }
        controller.setOnCoffeeSavedCallback(onSavedCallback);

        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(state);
        dialogStage.setTitle(mode.equals("ADD") ? "Add New Coffee" : "Edit Coffee");
        dialogStage.setScene(new Scene(root));
        dialogStage.showAndWait();
    }

    public void gotoAddCoffee(Consumer<Boolean> onSavedCallback) throws IOException {
        gotoCoffeeDialog("ADD", null, onSavedCallback);
    }

    public void gotoEditCoffee(Coffee coffee, Consumer<Boolean> onSavedCallback) throws IOException {
        gotoCoffeeDialog("EDIT", coffee, onSavedCallback);
    }

    public void gotoOrderStatistic() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(ORDER_STATISTIC));
        Parent root = loader.load();
        Stage dialogStage = new Stage();
        dialogStage.setScene(new Scene(root));
        dialogStage.setTitle("Order Statistic");
        dialogStage.showAndWait();
    }
}