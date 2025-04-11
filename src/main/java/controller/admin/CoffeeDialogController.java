package controller.admin;


import database.CategoryDAO;
import database.CoffeeDAO;
import helper.Alert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Category;
import model.CoffeeShop.Coffee;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class CoffeeDialogController implements Initializable {

    @FXML
    private TextField nameField;

    @FXML
    private ComboBox<Category> categoryComboBox;

    @FXML
    private TextField priceField;

    @FXML
    private TextField quantityField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private ImageView coffeeImageView;

    @FXML
    private Button browseImageButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private String mode;
    private model.CoffeeShop.Coffee coffee;
    private CoffeeDAO coffeeDAO;
    private CategoryDAO categoryDAO;
    private Consumer<Boolean> onSavedCallback;
    private String selectedImageUrl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        coffeeDAO = new CoffeeDAO();
        categoryDAO = new CategoryDAO();

        // Initialize category combo box
        loadCategories();

        // Set numeric validation for price and quantity fields
        priceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                priceField.setText(oldValue);
            }
        });

        quantityField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                quantityField.setText(oldValue);
            }
        });
    }

    private void loadCategories() {
        try {
            ObservableList<Category> categories = categoryDAO.getAllCategories();
            categoryComboBox.setItems(categories);
        } catch (Exception e) {
            Alert.showAlert("Failed to load categories" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setMode(String mode) {
        this.mode = mode;
    }


    public void setOnCoffeeSavedCallback(Consumer<Boolean> callback) {
        this.onSavedCallback = callback;
    }

    private void populateFields() {
        if (coffee != null) {
            nameField.setText(coffee.getName());
            for (Category category : categoryComboBox.getItems()) {
                if (category.getId() == coffee.getCategoryId()) {
                    categoryComboBox.setValue(category);
                    break;
                }
            }

            priceField.setText(String.valueOf(coffee.getPrice()));
            quantityField.setText(String.valueOf(coffee.getQuantity()));
            descriptionArea.setText(coffee.getDescription());

            // Load image if available
            if (coffee.getImage() != null && !coffee.getImage().isEmpty()) {
                try {
                    Image image = new Image(coffee.getImage());
                    coffeeImageView.setImage(image);
                    selectedImageUrl = coffee.getImage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void handleBrowseImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        Stage stage = (Stage) browseImageButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                Image image = new Image(selectedFile.toURI().toString());
                coffeeImageView.setImage(image);
                selectedImageUrl = selectedFile.toURI().toString();
            } catch (Exception e) {
                Alert.showAlert("Failed to load image" + e.getMessage());
            }
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        // Get data from input fields
        String name = nameField.getText().trim();
        Category category = categoryComboBox.getValue();
        double price = Double.parseDouble(priceField.getText());
        int quantity = Integer.parseInt(quantityField.getText());
        String description = descriptionArea.getText().trim();

        boolean success = false;

        if ("ADD".equals(mode)) {
            Coffee newCoffee = new Coffee(
                    0,
                    name,
                    category.getId(),
                    price,
                    quantity,
                    selectedImageUrl,
                    1,
                    description
            );
            success = coffeeDAO.addCoffee(newCoffee);

            if (success) {
                Alert.showSuccess("Coffee added successfully");
            } else {
                Alert.showAlert("Database operation failed");
                return;
            }
        } else if ("EDIT".equals(mode)) {
            coffee.setName(name);
            coffee.setCategoryId(category.getId());
            coffee.setPrice(price);
            coffee.setQuantity(quantity);
            coffee.setDescription(description);
            coffee.setImage(selectedImageUrl);  // Fixed truncated line

            success = coffeeDAO.updateCoffee(coffee);

            if (success) {
                Alert.showSuccess("Coffee updated successfully");
            } else {
                Alert.showAlert("Failed to update coffee");
                return;
            }
        }

        if (onSavedCallback != null) {
            onSavedCallback.accept(success);
        }

        // Close the dialog
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private boolean validateInputs() {
        StringBuilder errorMessage = new StringBuilder();

        if (nameField.getText().trim().isEmpty()) {
            errorMessage.append("Name is required.\n");
        }

        if (categoryComboBox.getValue() == null) {
            errorMessage.append("Category is required.\n");
        }

        if (priceField.getText().trim().isEmpty()) {
            errorMessage.append("Price is required.\n");
        }

        if (quantityField.getText().trim().isEmpty()) {
            errorMessage.append("Quantity is required.\n");
        }

        if (errorMessage.length() > 0) {
            Alert.showAlert("Validation Error: " + errorMessage.toString());
            return false;
        }

        return true;
    }

    public void setCoffee(model.CoffeeShop.Coffee selectedCoffee) {
        this.coffee = selectedCoffee;
        populateFields();
    }
}