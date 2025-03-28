package controller.CoffeeShop;

import helper.CoffeeShop.CartManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import model.CoffeeShop.Coffee;

import javafx.scene.shape.Rectangle;

import java.sql.*;


public class CoffeeItemController {
    @FXML private Label coffeeName;
    @FXML private Label coffeePrice;
    @FXML private Label coffeeDescription;
    @FXML private Label ratingLabel;
    @FXML private Label featuredBadge;
    @FXML private StackPane coffeeImageContainer;

    @FXML private ToggleButton sizeSmall;
    @FXML private ToggleButton sizeMedium;
    @FXML private ToggleButton sizeLarge;

    @FXML private Button decreaseBtn;
    @FXML private Button increaseBtn;
    @FXML private TextField quantityField;
    @FXML private Button addToCartBtn;

    private Coffee coffee;
    private int quantity = 1;
    private String selectedSize = "S";
    private double priceMultiplier = 1.0;

    @FXML
    private void initialize() {
        // Initialize quantity field with default value
        quantityField.setText("1");

        // Set up quantity control
        decreaseBtn.setOnAction(e -> decreaseQuantity());
        increaseBtn.setOnAction(e -> increaseQuantity());
        quantityField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                quantityField.setText(newVal.replaceAll("[^\\d]", ""));
            }
            if (!newVal.isEmpty()) {
                quantity = Integer.parseInt(newVal);
            }
        });

        // Set up size selection
        sizeSmall.setOnAction(e -> {
            if (sizeSmall.isSelected()) {
                sizeMedium.setSelected(false);
                sizeLarge.setSelected(false);
                selectedSize = "S";
                priceMultiplier = 1.0;
                updatePrice();
            } else {
                sizeSmall.setSelected(true); // Always keep one size selected
            }
        });

        sizeMedium.setOnAction(e -> {
            if (sizeMedium.isSelected()) {
                sizeSmall.setSelected(false);
                sizeLarge.setSelected(false);
                selectedSize = "M";
                priceMultiplier = 1.2;
                updatePrice();
            } else {
                sizeMedium.setSelected(true);
            }
        });

        sizeLarge.setOnAction(e -> {
            if (sizeLarge.isSelected()) {
                sizeSmall.setSelected(false);
                sizeMedium.setSelected(false);
                selectedSize = "L";
                priceMultiplier = 1.4;
                updatePrice();
            } else {
                sizeLarge.setSelected(true);
            }
        });

        // Set up add to cart button
        addToCartBtn.setOnAction(e -> addToCart());
    }

    public void setCoffee(Coffee coffee) {
        this.coffee = coffee;

        // Set the coffee details in the UI
        coffeeName.setText(coffee.getName());
        coffeePrice.setText(String.format("%.2f", coffee.getPrice()));
        coffeeDescription.setText(coffee.getDescription());

        // Load coffee image
        loadCoffeeImage();

        // Load and display category name
        loadCategoryName(coffee.getCategoryId());

        // Set random rating for demo purposes
        double rating = 3.5 + Math.random() * 1.5;
        ratingLabel.setText(String.format("%.1f", rating));

        // Show featured badge for certain items (optional logic)
        featuredBadge.setVisible(rating >= 4.5);
    }

    @FXML private HBox coffeeTagsContainer;

    private void loadCategoryName(int categoryId) {
        try (Connection connection = helper.ConnectDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT category_name FROM category WHERE id = ?")) {

            statement.setInt(1, categoryId);
            ResultSet resultSet = statement.executeQuery();

            // Clear existing tags
            coffeeTagsContainer.getChildren().clear();

            if (resultSet.next()) {
                String categoryName = resultSet.getString("category_name");

                // Create a new label for the category
                Label categoryLabel = new Label(categoryName);
                categoryLabel.getStyleClass().add("coffee-tag");

                // Add it to the tags container
                coffeeTagsContainer.getChildren().add(categoryLabel);

            }
        } catch (SQLException e) {
            System.err.println("Error loading category name: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadCoffeeImage() {
        try {
            String imagePath = coffee.getImage();
            Image image = null;

            // Define target dimensions
            final double TARGET_WIDTH = 210;
            final double TARGET_HEIGHT = 200;

            // Try loading from different sources
            String resourcePath = "/assets/images/CoffeeItem/" + imagePath;
            try {
                image = new Image(getClass().getResourceAsStream(resourcePath));
            } catch (Exception e) {
                System.err.println("Failed to load from resource path: " + resourcePath);
            }

            if (image == null || image.isError()) {
                try {
                    image = new Image(getClass().getResourceAsStream("/assets/images/CoffeeItem/default.jpg"));
                } catch (Exception e) {
                    System.err.println("Even default image failed to load: " + e.getMessage());
                }
            }

            // Display the image with fixed dimensions
            if (image != null && !image.isError()) {
                ImageView imageView = new ImageView(image);

                // Force the exact dimensions - CRITICAL CHANGE
                imageView.setFitWidth(TARGET_WIDTH);
                imageView.setFitHeight(TARGET_HEIGHT);
                imageView.setPreserveRatio(false);

                imageView.setSmooth(true);

                // Clip the image to ensure it doesn't overflow container
                Rectangle clip = new Rectangle(TARGET_WIDTH, TARGET_HEIGHT);
                clip.setArcWidth(10);
                clip.setArcHeight(10);
                imageView.setClip(clip);

                // Replace existing content with new image
                coffeeImageContainer.getChildren().clear();
                coffeeImageContainer.getChildren().add(imageView);
            }
        } catch (Exception e) {
            System.err.println("Error loading coffee image: " + e.getMessage());
            e.printStackTrace();
        }
    }



    private void decreaseQuantity() {
        if (quantity > 1) {
            quantity--;
            quantityField.setText(String.valueOf(quantity));
        }
    }

    private void increaseQuantity() {
        if (quantity < 99) {
            quantity++;
            quantityField.setText(String.valueOf(quantity));
        }
    }

    private void updatePrice() {
        if (coffee != null) {
            double adjustedPrice = coffee.getPrice() * priceMultiplier;
            coffeePrice.setText(String.format("%.2f", adjustedPrice));
        }
    }

//    private void addToCart() {
//        // Here you would implement the logic to add the item to the cart
//        System.out.println("Added to cart: " + coffee.getName() +
//                ", Size: " + selectedSize +
//                ", Quantity: " + quantity);
//        // You can create a CartManager class to handle the shopping cart functionality
//    }

    private void addToCart() {
        if (coffee == null) return;

        // Calculate the adjusted price based on size
        double adjustedPrice = coffee.getPrice() * priceMultiplier;

        // Add to cart using CartManager
        CartManager.getInstance().addToCart(
                coffee.getId(),
                coffee.getName(),
                selectedSize,
                quantity,
                adjustedPrice
        );

        // Show success notification
        showNotification("Added to cart: " + coffee.getName() + " (" + selectedSize + ") x" + quantity);

        // Reset quantity to 1 after adding to cart
        quantity = 1;
        quantityField.setText("1");
    }

    private void showNotification(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cart Updated");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}