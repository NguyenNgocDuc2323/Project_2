package controller.CoffeeShop;

import helper.CoffeeShop.CartManager;
import helper.ConnectDatabase;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import model.CoffeeShop.Coffee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CoffeeItemController {
    @FXML private Label coffeeName;
    @FXML private Label coffeePrice;
    @FXML private Label coffeeDescription;
    @FXML private Label ratingLabel;
    @FXML private Label featuredBadge;
    @FXML private HBox coffeeTagsContainer;
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
    private Map<String, Double> availableSizes = new HashMap<>();

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
        sizeSmall.setOnAction(e -> selectSize("S"));
        sizeMedium.setOnAction(e -> selectSize("M"));
        sizeLarge.setOnAction(e -> selectSize("L"));

        // Set up add to cart button
        addToCartBtn.setOnAction(e -> addToCart());
    }

    private void selectSize(String size) {
        // Only process if this size is available
        if (!availableSizes.containsKey(size)) {
            return;
        }

        // Update UI
        switch (size) {
            case "S":
                sizeSmall.setSelected(true);
                sizeMedium.setSelected(false);
                sizeLarge.setSelected(false);
                break;
            case "M":
                sizeSmall.setSelected(false);
                sizeMedium.setSelected(true);
                sizeLarge.setSelected(false);
                break;
            case "L":
                sizeSmall.setSelected(false);
                sizeMedium.setSelected(false);
                sizeLarge.setSelected(true);
                break;
        }

        // Update selected size and price
        selectedSize = size;
        updatePriceFromSelectedSize();
    }

    public void setCoffee(Coffee coffee) {
        this.coffee = coffee;

        // Set the coffee details in the UI
        coffeeName.setText(coffee.getName());
        coffeeDescription.setText(coffee.getDescription());

        // Load coffee image
        loadCoffeeImage();

        // Load available sizes and prices from database
        loadProductSizes();

        // Load and display category name
        loadCategoryName(coffee.getCategoryId());

        // Set random rating for demo purposes
        double rating = 3.5 + Math.random() * 1.5;
        ratingLabel.setText(String.format("%.1f", rating));

        // Show featured badge for certain items
        featuredBadge.setVisible(rating >= 4.5);
    }

    private void loadProductSizes() {
        try (Connection connection = ConnectDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT ps.size_id, s.symbol, ps.price " +
                             "FROM product_sizes ps " +
                             "JOIN sizes s ON ps.size_id = s.id " +
                             "WHERE ps.product_id = ? " +
                             "ORDER BY s.id")) {

            statement.setInt(1, coffee.getId());
            ResultSet resultSet = statement.executeQuery();

            // Clear previous size data
            availableSizes.clear();

            // Disable all size buttons by default
            sizeSmall.setDisable(true);
            sizeMedium.setDisable(true);
            sizeLarge.setDisable(true);

            boolean hasDefaultSize = false;

            while (resultSet.next()) {
                String symbol = resultSet.getString("symbol");
                double price = resultSet.getDouble("price");

                // Store size and price
                availableSizes.put(symbol, price);

                // Enable corresponding button
                switch (symbol) {
                    case "S":
                        sizeSmall.setDisable(false);
                        if (!hasDefaultSize) {
                            sizeSmall.setSelected(true);
                            selectedSize = "S";
                            hasDefaultSize = true;
                        }
                        break;
                    case "M":
                        sizeMedium.setDisable(false);
                        if (!hasDefaultSize) {
                            sizeMedium.setSelected(true);
                            selectedSize = "M";
                            hasDefaultSize = true;
                        }
                        break;
                    case "L":
                        sizeLarge.setDisable(false);
                        if (!hasDefaultSize) {
                            sizeLarge.setSelected(true);
                            selectedSize = "L";
                            hasDefaultSize = true;
                        }
                        break;
                }
            }

            // Update price based on selected size
            updatePriceFromSelectedSize();

        } catch (SQLException e) {
            System.err.println("Error loading product sizes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updatePriceFromSelectedSize() {
        if (availableSizes.containsKey(selectedSize)) {
            double price = availableSizes.get(selectedSize);
            coffeePrice.setText(String.format("%.2f", price));
        }
    }

    private void loadCategoryName(int categoryId) {
        try (Connection connection = ConnectDatabase.getConnection();
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

                // Force the exact dimensions
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

    private void addToCart() {
        if (coffee == null) return;

        // Get the actual price based on selected size
        if (!availableSizes.containsKey(selectedSize)) {
            showNotification("This size is not available");
            return;
        }

        double price = availableSizes.get(selectedSize);

        // Add to cart using CartManager
        CartManager.getInstance().addToCart(
                coffee.getId(),
                coffee.getName(),
                selectedSize,
                quantity,
                price
        );

        // Show success notification
        showNotification("Added to cart: " + coffee.getName() + " (" + selectedSize + ") x" + quantity);

        // Reset quantity to 1 after adding to cart
        quantity = 1;
        quantityField.setText("1");
    }

    private void showNotification(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cart Update");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}