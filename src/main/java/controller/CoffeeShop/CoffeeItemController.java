package controller.CoffeeShop;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import model.Coffee;

import java.io.File;

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

        // Set random rating for demo purposes
        double rating = 3.5 + Math.random() * 1.5;
        ratingLabel.setText(String.format("%.1f", rating));

        // Show featured badge for certain items (optional logic)
        featuredBadge.setVisible(rating >= 4.5);
    }

    private void loadCoffeeImage() {
        try {
            String imagePath = coffee.getImage();
            Image image = null;

            // Try different approaches to load the image
            // 1. Try to load from resources
            String resourcePath = "/assets/images/coffee/" + imagePath;
            try {
                image = new Image(getClass().getResourceAsStream(resourcePath));
            } catch (Exception e) {
                // Resource not found, try other methods
            }

            // 2. Try as direct file path
            if (image == null || image.isError()) {
                File file = new File(imagePath);
                if (file.exists()) {
                    image = new Image(file.toURI().toString());
                }
            }

            // 3. Try with application-specific path
            if (image == null || image.isError()) {
                File file = new File("src/main/resources/assets/images/coffee/" + imagePath);
                if (file.exists()) {
                    image = new Image(file.toURI().toString());
                }
            }

            // 4. Use default image if all attempts fail
            if (image == null || image.isError()) {
                image = new Image(getClass().getResourceAsStream("/assets/images/coffee/default_coffee.png"));
            }

            // Display the image
            if (image != null && !image.isError()) {
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(200);
                imageView.setFitHeight(140);
                imageView.setPreserveRatio(true);

                // Clear previous content and add the image view
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

    private void addToCart() {
        // Here you would implement the logic to add the item to the cart
        System.out.println("Added to cart: " + coffee.getName() +
                ", Size: " + selectedSize +
                ", Quantity: " + quantity);
        // You can create a CartManager class to handle the shopping cart functionality
    }
}