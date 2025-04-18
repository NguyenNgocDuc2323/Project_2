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
    private ComboBox<String> statusComboBox;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private ImageView coffeeImageView;

    @FXML
    private Button browseImageButton;
    
    @FXML
    private ComboBox<String> imageComboBox;

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

        // Set numeric validation for price field
        priceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                priceField.setText(oldValue);
            }
        });

        // Initialize status combo box
        statusComboBox.setItems(FXCollections.observableArrayList("Active", "Inactive"));
        
        // Load images from database
        loadImagesFromDatabase();
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

    private Image loadImage(String imagePath) {
        Image image = null;

        if (imagePath == null || imagePath.trim().isEmpty()) {
            System.err.println("Warning: Empty or null image path provided");
            return loadDefaultImage();
        }
        
        try {
            System.out.println("Attempting to load image from: " + imagePath);

            if (!imagePath.startsWith("file:") && !imagePath.startsWith("http")) {
                String resourcePath = "/assets/images/CoffeeItem/" + imagePath;
                System.out.println("Trying resource path: " + resourcePath);
                image = new Image(getClass().getResourceAsStream(resourcePath));
            }

            if (image == null || image.isError()) {
                System.out.println("Resource not found, trying direct path: " + imagePath);
                image = new Image(imagePath);
            }

            if (image == null || image.isError()) {
                System.err.println("Failed to load image from path: " + imagePath);
                return loadDefaultImage();
            }
        } catch (Exception e) {
            System.err.println("Error loading image '" + imagePath + "': " + e.getMessage());
            return loadDefaultImage();
        }
        
        return image;
    }

    private Image loadDefaultImage() {
        try {
            String defaultImagePath = "/assets/images/CoffeeItem/default.jpg";
            System.out.println("Loading default image from: " + defaultImagePath);
            return new Image(getClass().getResourceAsStream(defaultImagePath));
        } catch (Exception ex) {
            System.err.println("Critical error: Failed to load default image: " + ex.getMessage());
            return null;
        }
    }
    
    private void loadImagesFromDatabase() {
        try {
            ObservableList<String> images = coffeeDAO.getAllImages();
            imageComboBox.setItems(images);
            
            // Add listener to image combo box
            imageComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null && !newValue.isEmpty()) {
                    try {
                        Image image = loadImage(newValue);
                        coffeeImageView.setImage(image);
                        selectedImageUrl = newValue;
                    } catch (Exception e) {
                        Alert.showAlert("Failed to load image: " + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            Alert.showAlert("Failed to load images: " + e.getMessage());
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
            statusComboBox.setValue(coffee.getStatus() == 1 ? "Active" : "Inactive");
            descriptionArea.setText(coffee.getDescription());

            // Load image if available
            if (coffee.getImage() != null && !coffee.getImage().isEmpty()) {
                try {
                    // Sử dụng phương thức loadImage để tải hình ảnh
                    Image image = loadImage(coffee.getImage());
                    coffeeImageView.setImage(image);
                    selectedImageUrl = coffee.getImage();
                    
                    // Select the image in the combo box if it exists
                    for (String imgPath : imageComboBox.getItems()) {
                        if (imgPath.equals(coffee.getImage())) {
                            imageComboBox.setValue(imgPath);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        String defaultImagePath = "/assets/images/CoffeeItem/default.jpg";
                        Image defaultImage = new Image(getClass().getResourceAsStream(defaultImagePath));
                        coffeeImageView.setImage(defaultImage);
                    } catch (Exception ex) {
                        Alert.showAlert("Failed to load default image");
                    }
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
                // Lấy đường dẫn tuyệt đối của file
                String filePath = selectedFile.toURI().toString();
                
                // Sử dụng phương thức loadImage để tải hình ảnh
                Image image = loadImage(filePath);
                coffeeImageView.setImage(image);
                selectedImageUrl = filePath;
                
                // Clear the selection in the combo box when a new image is selected
                imageComboBox.getSelectionModel().clearSelection();
            } catch (Exception e) {
                Alert.showAlert("Failed to load image: " + e.getMessage());
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
        int status = statusComboBox.getValue().equals("Active") ? 1 : 0;
        String description = descriptionArea.getText().trim();

        boolean success = false;

        if ("ADD".equals(mode)) {
            Coffee newCoffee = new Coffee(
                    0,
                    name,
                    category.getId(),
                    price,
                    0, // quantity is no longer used, set to 0
                    selectedImageUrl,
                    1,
                    description
            );
            newCoffee.setStatus(status);
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
            coffee.setStatus(status);
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