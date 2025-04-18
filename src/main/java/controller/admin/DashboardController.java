package controller.admin;

import helper.Alert;
import helper.Navigator;
import helper.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import model.Account;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @FXML private StackPane contentArea;
    @FXML private Button menuBtn;
    @FXML private Button tableBtn;
    @FXML private Button categoryBtn;
    @FXML private Button orderBtn;
    @FXML private Button productBtn;
    @FXML private Button userBtn;
    @FXML private Button comboBtn;

    // User information UI elements
    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;
    @FXML private Circle userAvatar;
    @FXML private Label userInitialsLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        menuBtn.setOnAction(e -> loadView("/com/example/manage_account/Admin/Home.fxml"));
        tableBtn.setOnAction(e -> loadView("/com/example/manage_account/Staff/Table.fxml"));
        categoryBtn.setOnAction(e -> loadView("/com/example/manage_account/Staff/Category.fxml"));
        orderBtn.setOnAction(e -> loadView("/com/example/manage_account/Staff/Order.fxml"));
        productBtn.setOnAction(e -> loadView("/com/example/manage_account/Admin/Product.fxml"));
        userBtn.setOnAction(e -> loadView("/com/example/manage_account/Admin/UserManagement.fxml"));
        comboBtn.setOnAction(e -> loadView("/com/example/manage_account/CoffeeShop/ComboManagement.fxml"));

        loadUserInfo();

        loadView("/com/example/manage_account/Admin/Home.fxml");
    }

    private void loadUserInfo() {
        Account currentAccount = Session.getInstance().getCurrentUser();

        if (currentAccount != null) {
            userNameLabel.setText(currentAccount.getName());
            userRoleLabel.setText(currentAccount.getTypeAsString());

            String initials = getInitials(currentAccount.getName());
            userInitialsLabel.setText(initials);

            Color avatarColor = getColorFromName(currentAccount.getName());
            userAvatar.setFill(avatarColor);
        } else {
            userNameLabel.setText("Guest");
            userRoleLabel.setText("Not logged in");
            userInitialsLabel.setText("?");
            userAvatar.setFill(Color.GRAY);
        }
    }

    @FXML
    public void handleSignOut() {
        try {
            Session.getInstance().clearCurrentUser();

            Navigator.getInstance().gotoLogin();
        } catch (IOException e) {
            e.printStackTrace();
            Alert.showAlert("Error signing out. Please try again.");
        }
    }

    private String getInitials(String fullName) {
        if (fullName == null || fullName.isEmpty()) {
            return "?";
        }

        String[] names = fullName.split(" ");
        StringBuilder initials = new StringBuilder();

        for (String name : names) {
            if (!name.isEmpty()) {
                initials.append(name.charAt(0));
            }
        }

        return initials.toString().toUpperCase();
    }

    private Color getColorFromName(String name) {
        return Color.rgb(184, 92, 56);
    }

    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);

            menuBtn.getStyleClass().remove("active");
            tableBtn.getStyleClass().remove("active");
            categoryBtn.getStyleClass().remove("active");
            orderBtn.getStyleClass().remove("active");
            productBtn.getStyleClass().remove("active");
            userBtn.getStyleClass().remove("active");
            comboBtn.getStyleClass().remove("active");

            if (fxmlFile.contains("Home")) {
                menuBtn.getStyleClass().add("active");
            } else if (fxmlFile.contains("Table")) {
                tableBtn.getStyleClass().add("active");
            } else if (fxmlFile.contains("Category")) {
                categoryBtn.getStyleClass().add("active");
            } else if (fxmlFile.contains("Order")) {
                orderBtn.getStyleClass().add("active");
            }else if (fxmlFile.contains("Product")) {
                productBtn.getStyleClass().add("active");
            }
            else if (fxmlFile.contains("UserManagement")) {
                userBtn.getStyleClass().add("active");
            }
            else if (fxmlFile.contains("ComboManagement")) {
                userBtn.getStyleClass().add("active");
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading view: " + fxmlFile + " - " + e.getMessage());
        }
    }
}