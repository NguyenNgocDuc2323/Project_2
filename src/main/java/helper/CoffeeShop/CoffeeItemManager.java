package helper.CoffeeShop;

import helper.ConnectDatabase;
import model.CoffeeShop.Coffee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoffeeItemManager {
    private static CoffeeItemManager instance ;

    public static CoffeeItemManager getInstance() {
        if (instance == null) {
            instance = new CoffeeItemManager();
        }
        return instance;
    }

    private CoffeeItemManager() {
        // Private constructor for singleton pattern
    }

    // In the CoffeeItemManager class
    public List<Coffee> getAllCoffeeItems() {
        List<Coffee> items = new ArrayList<>();

        try (Connection connection = ConnectDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM product WHERE status = 1")) { // Only select active products

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Coffee coffee = new Coffee();
                coffee.setId(resultSet.getInt("id"));
                coffee.setName(resultSet.getString("name"));
                coffee.setCategoryId(resultSet.getInt("category_id"));
                coffee.setPrice(resultSet.getDouble("price"));
                coffee.setQuantity(resultSet.getInt("quantity"));
                coffee.setImage(resultSet.getString("image"));
                coffee.setUnitId(resultSet.getInt("unit_id"));
                coffee.setDescription(resultSet.getString("description"));
                coffee.setStatus(resultSet.getInt("status"));

                items.add(coffee);
            }

        } catch (SQLException e) {
            System.err.println("Error loading coffee items: " + e.getMessage());
            e.printStackTrace();
        }

        return items;
    }



}
