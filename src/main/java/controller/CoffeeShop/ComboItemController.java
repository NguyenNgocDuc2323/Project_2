package controller.CoffeeShop;

import helper.Alert;
import helper.CoffeeShop.CartManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.CoffeeShop.ComboProduct;

import java.text.DecimalFormat;

public class ComboItemController {
    @FXML private ImageView imageView;
    @FXML private Label nameLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label priceLabel;
    @FXML private Button addToCartButton;
    @FXML private Label discountLabel;
    @FXML private Label productsLabel;

    private ComboProduct combo;
    private final DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");

    public void setCombo(ComboProduct combo) {
        this.combo = combo;

        // Debug logging
        System.out.println("\nSetting up combo in ComboItemController:");
        System.out.println("Combo name: " + combo.getName());
        System.out.println("Original price: " + combo.getOriginalPrice());
        System.out.println("Discount: " + combo.getDiscountPercent() + "%");
        System.out.println("Final price: " + combo.getFinalPrice());
        System.out.println("Number of products: " + combo.getProducts().size());

        // Force price recalculation
        combo.validatePrices();

        // Display info after validation
        displayComboInfo();
        setupEventHandlers();
    }

    private void displayComboInfo() {
        if (combo != null) {
            nameLabel.setText(combo.getName());
            descriptionLabel.setText(combo.getDescription());

            // Format and display prices
            double originalPrice = combo.getOriginalPrice();
            double finalPrice = combo.getFinalPrice();

            System.out.println("Displaying prices:");
            System.out.println("Original: $" + currencyFormat.format(originalPrice));
            System.out.println("Final: $" + currencyFormat.format(finalPrice));

            // Show original price if there's a discount
            if (combo.getDiscountPercent() > 0) {
                String priceText = String.format("$%s", currencyFormat.format(finalPrice));
                if (originalPrice != finalPrice) {
                    priceText = String.format("$%s  (Was: $%s)",
                            currencyFormat.format(finalPrice),
                            currencyFormat.format(originalPrice));
                }
                priceLabel.setText(priceText);
            } else {
                priceLabel.setText("$" + currencyFormat.format(finalPrice));
            }

            // Display discount
            double discount = combo.getDiscountPercent();
            discountLabel.setText(String.format("-%.0f%%", discount));
            discountLabel.setVisible(discount > 0);

            // Display products
            StringBuilder products = new StringBuilder("Includes:\n");
            combo.getProducts().forEach(coffee -> {
                products.append("- ").append(coffee.getName())
                        .append(" ($").append(currencyFormat.format(coffee.getPrice()))
                        .append(")\n");
                System.out.println("Product in combo: " + coffee.getName() +
                        " - Price: $" + coffee.getPrice());
            });
            productsLabel.setText(products.toString());

            loadDefaultImage();
        } else {
            System.err.println("Warning: Attempting to display null combo");
        }
    }

    private void loadDefaultImage() {
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/com/example/manage_account/assets/images/default-combo.png"));
            imageView.setImage(defaultImage);
        } catch (Exception e) {
            System.err.println("Error loading default image: " + e.getMessage());
        }
    }

    private void setupEventHandlers() {
        addToCartButton.setOnAction(event -> addToCart());
        addToCartButton.setOnMouseEntered(e -> addToCartButton.setStyle("-fx-background-color: #8B4513;"));
        addToCartButton.setOnMouseExited(e -> addToCartButton.setStyle("-fx-background-color: #A0522D;"));
    }

    private void addToCart() {
        try {
            if (combo == null) {
                Alert.showAlert("Error: Invalid combo");
                return;
            }

            // Validate price before adding to cart
            if (combo.getFinalPrice() <= 0) {
                System.err.println("Warning: Attempting to add combo with invalid price");
                combo.validatePrices();
                if (combo.getFinalPrice() <= 0) {
                    Alert.showAlert("Error: Invalid combo price");
                    return;
                }
            }

            if (combo.isActive()) {
                if (!CartManager.getInstance().isComboInCart(combo.getId())) {
                    CartManager.getInstance().addComboToCart(combo);
                    Alert.showSuccess("Added " + combo.getName() + " to cart!");
                } else {
                    Alert.showAlert("This combo is already in your cart!");
                }
            } else {
                Alert.showAlert("This combo is currently not available!");
            }
        } catch (Exception e) {
            Alert.showAlert("Error adding combo to cart: " + e.getMessage());
            e.printStackTrace();
        }
    }
}