package controller.CoffeeShop;

import helper.Alert;
import helper.CoffeeShop.CartManager;
import helper.CoffeeShop.ComboManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import model.CoffeeShop.Coffee;
import model.CoffeeShop.ComboProduct;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

/**
 * Controller để hiển thị và quản lý combo sản phẩm trong giao diện người dùng
 */
public class ComboViewController implements Initializable {
    @FXML
    private VBox comboContainer;
    
    private ComboManager comboManager;
    private CartManager cartManager;
    private DecimalFormat decimalFormat;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        comboManager = ComboManager.getInstance();
        cartManager = CartManager.getInstance();
        decimalFormat = new DecimalFormat("#,##0.00");
        
        // Tải và hiển thị danh sách combo
        loadCombos();
    }
    
    /**
     * Tải và hiển thị danh sách combo
     */
    private void loadCombos() {
        comboContainer.getChildren().clear();
        
        // Tiêu đề phần combo
        Label titleLabel = new Label("COMBO KHUYẾN MÃI");
        titleLabel.getStyleClass().add("section-title");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10 0 10 0;");
        comboContainer.getChildren().add(titleLabel);
        
        // Tải danh sách combo đang active
        for (ComboProduct combo : comboManager.getActiveCombos()) {
            comboContainer.getChildren().add(createComboItem(combo));
        }
    }
    
    /**
     * Tạo giao diện hiển thị cho một combo
     * @param combo Combo cần hiển thị
     * @return VBox chứa thông tin combo
     */
    private VBox createComboItem(ComboProduct combo) {
        VBox comboBox = new VBox(10);
        comboBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 10; -fx-background-color: white;");
        comboBox.setPrefWidth(300);
        
        // Tên combo
        Label nameLabel = new Label(combo.getName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Mô tả combo
        Label descLabel = new Label(combo.getDescription());
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-font-size: 12px;");
        
        // Danh sách sản phẩm trong combo
        VBox productsBox = new VBox(5);
        productsBox.setStyle("-fx-padding: 5 0 5 10;");
        
        for (Coffee product : combo.getProducts()) {
            Label productLabel = new Label("• " + product.getName());
            productsBox.getChildren().add(productLabel);
        }
        
        // Hiển thị giá và phần trăm giảm giá
        HBox priceBox = new HBox(10);
        priceBox.setAlignment(Pos.CENTER_LEFT);
        
        Label originalPriceLabel = new Label("$" + decimalFormat.format(combo.getOriginalPrice()));
        originalPriceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #888; -fx-strikethrough: true;");
        
        Label finalPriceLabel = new Label("$" + decimalFormat.format(combo.getFinalPrice()));
        finalPriceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #d35400;");
        
        Label discountLabel = new Label("-" + decimalFormat.format(combo.getDiscountPercent()) + "%");
        discountLabel.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 2 5 2 5; -fx-background-radius: 3;");
        
        priceBox.getChildren().addAll(originalPriceLabel, finalPriceLabel, discountLabel);
        
        // Nút thêm vào giỏ hàng
        Button addToCartButton = new Button("Thêm vào giỏ hàng");
        addToCartButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        addToCartButton.setPrefWidth(Double.MAX_VALUE);
        addToCartButton.setOnAction(event -> {
            comboManager.addComboToCart(combo, cartManager);
            Alert.showSuccess("Đã thêm combo " + combo.getName() + " vào giỏ hàng!");
        });
        
        // Thêm tất cả vào container
        comboBox.getChildren().addAll(nameLabel, descLabel, productsBox, priceBox, addToCartButton);
        
        return comboBox;
    }
    
    /**
     * Làm mới danh sách combo
     */
    public void refreshCombos() {
        comboManager.loadActiveCombos();
        loadCombos();
    }
}