package controller.CoffeeShop;

import helper.CoffeeShop.CartManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @FXML private StackPane contentArea;
    @FXML private Button menuBtn;
    @FXML private Button cartBtn;
    @FXML private Button historyBtn;
    @FXML private Button profileBtn;
    @FXML private Label cartItemCount;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up button actions
        menuBtn.setOnAction(e -> loadView("/com/example/manage_account/CoffeeShop/MenuView.fxml"));
        cartBtn.setOnAction(e -> loadView("/com/example/manage_account/CoffeeShop/CartView.fxml"));
        historyBtn.setOnAction(e -> loadView("/com/example/manage_account/CoffeeShop/OrdersView.fxml"));
        profileBtn.setOnAction(e -> loadView("/com/example/manage_account/CoffeeShop/ProfileView.fxml"));

        // Load menu view by default
        loadView("/com/example/manage_account/CoffeeShop/MenuView.fxml");

        // Example for updating cart badge
//        cartItemCount.setText("0");

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(2), event -> updateCartCount())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateCartCount() {
        int count = CartManager.getInstance().getCartItemCount();
        cartItemCount.setText(String.valueOf(count));
        cartItemCount.setVisible(count > 0);
    }


    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);

            // Update active button styling
            menuBtn.getStyleClass().remove("active");
            cartBtn.getStyleClass().remove("active");
            historyBtn.getStyleClass().remove("active");
            profileBtn.getStyleClass().remove("active");

            // Set active style for selected button
            if (fxmlFile.contains("MenuView")) {
                menuBtn.getStyleClass().add("active");
            } else if (fxmlFile.contains("CartView")) {
                cartBtn.getStyleClass().add("active");
            } else if (fxmlFile.contains("OrdersView")) {
                historyBtn.getStyleClass().add("active");
            } else if (fxmlFile.contains("ProfileView")) {
                profileBtn.getStyleClass().add("active");
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading view: " + fxmlFile + " - " + e.getMessage());
        }
    }
}