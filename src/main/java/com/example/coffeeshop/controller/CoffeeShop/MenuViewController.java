package com.example.coffeeshop.controller.CoffeeShop;

import com.example.coffeeshop.database.CoffeeItemManager;
import com.example.coffeeshop.database.ConnectDatabase;
import com.example.coffeeshop.model.Coffee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class MenuViewController implements Initializable {
    @FXML private FlowPane coffeeItemsContainer;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private ComboBox<String> sortOptions;

    private List<Coffee> allCoffeeItems = new ArrayList<>();
    private List<String> categories = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Load categories for the filter
        loadCategories();

        // Set up sort options
        setupSortOptions();

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

    private void setupSortOptions() {
        ObservableList<String> options = FXCollections.observableArrayList(
                "Default",
                "Price: High to Low",
                "Price: Low to High",
                "Name: A to Z",
                "Name: Z to A"
        );
        sortOptions.setItems(options);
        sortOptions.getSelectionModel().selectFirst();
    }

    private void setupEventListeners() {
        categoryFilter.setOnAction(e -> applyFiltersAndSort());
        sortOptions.setOnAction(e -> applyFiltersAndSort());
    }

    private void applyFiltersAndSort() {
        String selectedCategory = categoryFilter.getValue();
        String selectedSortOption = sortOptions.getValue();

        List<Coffee> filteredItems = new ArrayList<>(allCoffeeItems);

        // Apply category filter
        if (selectedCategory != null && !selectedCategory.equals("All Categories")) {
            filteredItems = filteredItems.stream()
                    .filter(coffee -> getCategoryName(coffee.getCategoryId()).equals(selectedCategory))
                    .toList();
        }

        // Apply sorting
        if (selectedSortOption != null) {
            filteredItems = new ArrayList<>(filteredItems); // Create a mutable copy
            switch (selectedSortOption) {
                case "Price: High to Low":
                    filteredItems.sort(Comparator.comparing(Coffee::getPrice).reversed());
                    break;
                case "Price: Low to High":
                    filteredItems.sort(Comparator.comparing(Coffee::getPrice));
                    break;
                case "Name: A to Z":
                    filteredItems.sort(Comparator.comparing(Coffee::getName));
                    break;
                case "Name: Z to A":
                    filteredItems.sort(Comparator.comparing(Coffee::getName).reversed());
                    break;
            }
        }

        // Display the filtered and sorted items
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/coffeeshop/view/CoffeeShop/CoffeeItem.fxml"));
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