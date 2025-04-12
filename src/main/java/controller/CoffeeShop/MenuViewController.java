package controller.CoffeeShop;

import helper.CoffeeShop.CoffeeItemManager;
import helper.ConnectDatabase;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import model.CoffeeShop.Coffee;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MenuViewController implements Initializable {
    @FXML private FlowPane coffeeItemsContainer;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private TextField searchField;

    private List<Coffee> allCoffeeItems = new ArrayList<>();
    private List<String> categories = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Load categories for the filter
        loadCategories();

        // Load all coffee items
        allCoffeeItems = CoffeeItemManager.getInstance().getAllCoffeeItems();

        // Display coffee items
        displayCoffeeItems(allCoffeeItems);

        // Setup event listeners
        setupEventListeners();
    }

    private void loadCategories() {
        categories.add("All Categories");

        try (Connection connection = ConnectDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id, category_name FROM category ORDER BY category_name");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String categoryName = resultSet.getString("category_name");
                categories.add(categoryName);
            }

            categoryFilter.setItems(FXCollections.observableArrayList(categories));
            categoryFilter.getSelectionModel().selectFirst();

        } catch (SQLException e) {
            System.err.println("Error loading categories: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupEventListeners() {
        categoryFilter.setOnAction(e -> applyFilters());

        // Add listener for the search field
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });
    }

    private void applyFilters() {
        String selectedCategory = categoryFilter.getValue();
        String searchQuery = searchField.getText().trim().toLowerCase();

        List<Coffee> filteredItems = new ArrayList<>(allCoffeeItems);

        // Apply search filter if text is entered
        if (!searchQuery.isEmpty()) {
            filteredItems = filteredItems.stream()
                    .filter(coffee -> coffee.getName().toLowerCase().contains(searchQuery))
                    .toList();
        }

        // Apply category filter
        if (selectedCategory != null && !selectedCategory.equals("All Categories")) {
            filteredItems = filteredItems.stream()
                    .filter(coffee -> getCategoryName(coffee.getCategoryId()).equals(selectedCategory))
                    .toList();
        }

        // Display the filtered items
        displayCoffeeItems(filteredItems);
    }

    private String getCategoryName(int categoryId) {
        try (Connection connection = ConnectDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT category_name FROM category WHERE id = ?")) {

            statement.setInt(1, categoryId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("category_name");
            }
        } catch (SQLException e) {
            System.err.println("Error getting category name: " + e.getMessage());
        }
        return "";
    }

    private void displayCoffeeItems(List<Coffee> coffeeItems) {
        coffeeItemsContainer.getChildren().clear();

        for (Coffee coffee : coffeeItems) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/manage_account/CoffeeShop/CoffeeItem.fxml"));
                Node coffeeItem = loader.load();

                CoffeeItemController controller = loader.getController();
                controller.setCoffee(coffee);

                coffeeItemsContainer.getChildren().add(coffeeItem);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Could not load coffee item: " + e.getMessage());
            }
        }
    }
}