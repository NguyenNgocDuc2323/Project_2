package controller.staff;

import helper.Alert;
import helper.CoffeeShop.CartManager;
import helper.Navigator;
import helper.Session;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import model.Account;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SidebarController {
    @FXML
    private StackPane contentArea;
    @FXML
    private Button orderStatisticBtn;
    @FXML
    private Button orderManagementBtn;
    @FXML
    private Button tableManagementBtn;
    @FXML
    private Button categoryManagementBtn;
    @FXML
    private Button logoutBtn;
    @FXML
    private Label userNameLabel;
    @FXML
    private Label userRoleLabel;
    @FXML
    private Circle userAvatar;
    @FXML
    private Label userInitialsLabel;

    @FXML
    public void initialize() {
        orderStatisticBtn.setOnAction(e -> loadView(Navigator.ORDER_STATISTIC));
        orderManagementBtn.setOnAction(e -> loadView(Navigator.ORDER_MANAGEMENT));
        tableManagementBtn.setOnAction(e -> loadView(Navigator.TABLE_MANAGEMENT));
        categoryManagementBtn.setOnAction(e -> loadView(Navigator.CATEGORY_MANAGEMENT));
        loadUserInfo();
        loadView(Navigator.ORDER_STATISTIC);
    }

    @FXML
    private void handleSignOut() {
        try {
            Session.getInstance().clearCurrentUser();

            Navigator.getInstance().gotoLogin();
        } catch (IOException e) {
            e.printStackTrace();
            Alert.showAlert("Error signing out. Please try again.");
        }
    }

    @FXML
    private void handleGoToProfile() {
        loadView(Navigator.PROFILE);
    }

    private void loadUserInfo() {
        Account currentAccount = Session.getInstance().getCurrentUser();

        if (currentAccount != null) {
            userNameLabel.setText(currentAccount.getName());
            userRoleLabel.setText(currentAccount.getTypeAsString());

            String initials = getInitials(currentAccount.getName());
            userInitialsLabel.setText(initials);
        } else {
            userNameLabel.setText("Guest");
            userRoleLabel.setText("Not logged in");
            userInitialsLabel.setText("");
            userAvatar.setFill(Color.GRAY);
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

    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading view: " + fxmlFile + " - " + e.getMessage());
        }
    }
}
