package controller.CoffeeShop;

import helper.CoffeeShop.CoffeeItemManager;
import helper.CoffeeShop.ComboManager;
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
import model.CoffeeShop.ComboProduct;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MenuViewController implements Initializable {
    @FXML private FlowPane coffeeItemsContainer;
    @FXML private FlowPane comboItemsContainer;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private ComboBox<String> comboTypeFilter;
    @FXML private TextField searchField;
    @FXML private TextField comboSearchField;

    private List<Coffee> allCoffeeItems = new ArrayList<>();
    private List<ComboProduct> allComboItems = new ArrayList<>();
    private List<String> categories = new ArrayList<>();
    private List<String> comboTypes = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadCategories();
        loadComboTypes();

        allCoffeeItems = CoffeeItemManager.getInstance().getAllCoffeeItems();
        allComboItems = ComboManager.getInstance().getAllComboProducts();

        displayCoffeeItems(allCoffeeItems);
        displayComboItems(allComboItems);

        setupEventListeners();
    }

    private void loadCategories() {
        categories.add("All Categories");

        try (Connection connection = ConnectDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id, category_name FROM category ORDER BY category_name");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                categories.add(resultSet.getString("category_name"));
            }

            categoryFilter.setItems(FXCollections.observableArrayList(categories));
            categoryFilter.getSelectionModel().selectFirst();

        } catch (SQLException e) {
            System.err.println("Error loading categories: " + e.getMessage());
        }
    }

    private void loadComboTypes() {
        comboTypes.add("All Types");

        try (Connection connection = ConnectDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT DISTINCT combo_type FROM combo_products WHERE combo_type IS NOT NULL ORDER BY combo_type");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String comboType = resultSet.getString("combo_type");
                if (comboType != null && !comboType.trim().isEmpty()) {
                    comboTypes.add(comboType);
                }
            }

            comboTypeFilter.setItems(FXCollections.observableArrayList(comboTypes));
            comboTypeFilter.getSelectionModel().selectFirst();

        } catch (SQLException e) {
            System.err.println("Error loading combo types: " + e.getMessage());
        }
    }

    private void setupEventListeners() {
        categoryFilter.setOnAction(e -> applyFilters());
        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());

        comboTypeFilter.setOnAction(e -> applyComboFilters());
        comboSearchField.textProperty().addListener((observable, oldValue, newValue) -> applyComboFilters());
    }

    private void applyFilters() {
        String selectedCategory = categoryFilter.getValue();
        String searchQuery = searchField.getText().trim().toLowerCase();

        List<Coffee> filteredItems = new ArrayList<>(allCoffeeItems);

        if (!searchQuery.isEmpty()) {
            filteredItems = filteredItems.stream()
                    .filter(coffee -> coffee.getName().toLowerCase().contains(searchQuery))
                    .collect(Collectors.toList());
        }

        if (selectedCategory != null && !selectedCategory.equals("All Categories")) {
            filteredItems = filteredItems.stream()
                    .filter(coffee -> getCategoryName(coffee.getCategoryId()).equals(selectedCategory))
                    .collect(Collectors.toList());
        }

        displayCoffeeItems(filteredItems);
    }

    private void applyComboFilters() {
        String selectedType = comboTypeFilter.getValue();
        String searchQuery = comboSearchField.getText().trim().toLowerCase();

        List<ComboProduct> filteredItems = new ArrayList<>(allComboItems);

        if (!searchQuery.isEmpty()) {
            filteredItems = filteredItems.stream()
                    .filter(combo -> combo.getName().toLowerCase().contains(searchQuery))
                    .collect(Collectors.toList());
        }

        if (selectedType != null && !selectedType.equals("All Types")) {
            filteredItems = filteredItems.stream()
                    .filter(combo -> {
                        String comboType = combo.getComboType();
                        return comboType != null && comboType.equals(selectedType);
                    })
                    .collect(Collectors.toList());
        }

        displayComboItems(filteredItems);
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
                System.err.println("Could not load coffee item: " + e.getMessage());
            }
        }
    }

    private void displayComboItems(List<ComboProduct> comboItems) {
        comboItemsContainer.getChildren().clear();

        for (ComboProduct combo : comboItems) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/manage_account/CoffeeShop/ComboItem.fxml"));
                Node comboItem = loader.load();

                ComboItemController controller = loader.getController();
                controller.setCombo(combo);

                comboItemsContainer.getChildren().add(comboItem);
            } catch (IOException e) {
                System.err.println("Could not load combo item: " + e.getMessage());
            }
        }
    }
}