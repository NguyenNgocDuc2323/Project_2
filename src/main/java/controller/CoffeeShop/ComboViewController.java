package controller.CoffeeShop;

import helper.Alert;
import helper.CoffeeShop.CartManager;
import helper.CoffeeShop.ComboManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import model.CoffeeShop.Coffee;
import model.CoffeeShop.ComboProduct;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

public class ComboViewController implements Initializable {
    @FXML private VBox comboContainer;
    @FXML private TextField searchField;
    @FXML private FlowPane combosFlowPane;

    private ComboManager comboManager;
    private CartManager cartManager;
    private DecimalFormat decimalFormat;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        comboManager = ComboManager.getInstance();
        cartManager = CartManager.getInstance();
        decimalFormat = new DecimalFormat("#,##0.00");

        setupSearch();
        loadCombos();
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterCombos(newValue);
        });
    }

    private void filterCombos(String searchText) {
        comboContainer.getChildren().clear();

        // Add title
        Label titleLabel = createTitleLabel();
        comboContainer.getChildren().add(titleLabel);

        // Create FlowPane for combo items
        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(20);
        flowPane.setVgap(20);
        flowPane.setPadding(new Insets(10));

        // Filter and add combos
        comboManager.getActiveCombos().stream()
                .filter(combo -> matchesSearch(combo, searchText))
                .forEach(combo -> flowPane.getChildren().add(createComboItem(combo)));

        comboContainer.getChildren().add(flowPane);
    }

    private boolean matchesSearch(ComboProduct combo, String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return true;
        }

        String searchLower = searchText.toLowerCase();
        return combo.getName().toLowerCase().contains(searchLower) ||
                combo.getDescription().toLowerCase().contains(searchLower) ||
                combo.getProducts().stream()
                        .anyMatch(product -> product.getName().toLowerCase().contains(searchLower));
    }

    private Label createTitleLabel() {
        Label titleLabel = new Label("COMBO KHUYẾN MÃI");
        titleLabel.getStyleClass().add("section-title");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-padding: 15 0 15 0;");
        return titleLabel;
    }

    private void loadCombos() {
        comboContainer.getChildren().clear();

        // Add title
        Label titleLabel = createTitleLabel();
        comboContainer.getChildren().add(titleLabel);

        // Create FlowPane for combo items
        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(20);
        flowPane.setVgap(20);
        flowPane.setPadding(new Insets(10));

        // Add all active combos
        comboManager.getActiveCombos().forEach(combo ->
                flowPane.getChildren().add(createComboItem(combo))
        );

        comboContainer.getChildren().add(flowPane);
    }

    private VBox createComboItem(ComboProduct combo) {
        VBox comboBox = new VBox(10);
        comboBox.getStyleClass().add("combo-item");
        comboBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 8; -fx-background-color: white; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0); "
                + "-fx-padding: 15; -fx-min-width: 300; -fx-max-width: 300;");

        // Combo Name with badge
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(combo.getName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label badgeLabel = new Label("COMBO");
        badgeLabel.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; "
                + "-fx-padding: 2 8 2 8; -fx-background-radius: 12; -fx-font-size: 12px;");

        headerBox.getChildren().addAll(nameLabel, badgeLabel);

        // Description
        Label descLabel = new Label(combo.getDescription());
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");

        // Products List
        VBox productsBox = createProductsList(combo);

        // Price Section
        HBox priceBox = createPriceSection(combo);

        // Add to Cart Button
        Button addToCartButton = createAddToCartButton(combo);

        // Add all components
        comboBox.getChildren().addAll(
                headerBox,
                new Separator(),
                descLabel,
                productsBox,
                new Separator(),
                priceBox,
                addToCartButton
        );

        return comboBox;
    }

    private VBox createProductsList(ComboProduct combo) {
        VBox productsBox = new VBox(5);
        productsBox.setStyle("-fx-padding: 10 0 10 0;");

        Label productsTitle = new Label("Sản phẩm trong combo:");
        productsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        productsBox.getChildren().add(productsTitle);

        for (Coffee product : combo.getProducts()) {
            Label productLabel = new Label("• " + product.getName());
            productLabel.setStyle("-fx-font-size: 13px;");
            productsBox.getChildren().add(productLabel);
        }

        return productsBox;
    }

    private HBox createPriceSection(ComboProduct combo) {
        HBox priceBox = new HBox(15);
        priceBox.setAlignment(Pos.CENTER_LEFT);

        Label originalPriceLabel = new Label("$" + decimalFormat.format(combo.getOriginalPrice()));
        originalPriceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #999; -fx-strikethrough: true;");

        Label finalPriceLabel = new Label("$" + decimalFormat.format(combo.getFinalPrice()));
        finalPriceLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

        Label discountLabel = new Label("-" + decimalFormat.format(combo.getDiscountPercent()) + "%");
        discountLabel.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; "
                + "-fx-padding: 3 8 3 8; -fx-background-radius: 4;");

        priceBox.getChildren().addAll(originalPriceLabel, finalPriceLabel, discountLabel);

        return priceBox;
    }

    private Button createAddToCartButton(ComboProduct combo) {
        Button addToCartButton = new Button("Thêm vào giỏ hàng");
        addToCartButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; "
                + "-fx-font-size: 14px; -fx-padding: 10 15 10 15; -fx-background-radius: 5;");
        addToCartButton.setPrefWidth(Double.MAX_VALUE);

        addToCartButton.setOnAction(event -> {
            // Vô hiệu hóa button tạm thời để ngăn chặn double-click
            addToCartButton.setDisable(true);

            try {
                // Thêm combo vào giỏ hàng không cần kiểm tra
                cartManager.addComboToCart(combo);
                Alert.showSuccess("Đã thêm combo " + combo.getName() + " vào giỏ hàng!");
            } catch (Exception e) {
                Alert.showAlert("Lỗi khi thêm combo vào giỏ hàng: " + e.getMessage());
            } finally {
                // Kích hoạt lại button sau một khoảng thời gian ngắn
                new Thread(() -> {
                    try {
                        Thread.sleep(500);
                        javafx.application.Platform.runLater(() -> addToCartButton.setDisable(false));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        });

        // Hover effect
        addToCartButton.setOnMouseEntered(e ->
                addToCartButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; "
                        + "-fx-font-size: 14px; -fx-padding: 10 15 10 15; -fx-background-radius: 5;")
        );

        addToCartButton.setOnMouseExited(e ->
                addToCartButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; "
                        + "-fx-font-size: 14px; -fx-padding: 10 15 10 15; -fx-background-radius: 5;")
        );

        return addToCartButton;
    }

    public void refreshCombos() {
        comboManager.refreshComboProducts();
        loadCombos();
    }
}