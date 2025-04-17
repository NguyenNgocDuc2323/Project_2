package database;

import helper.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.CoffeeShop.Coffee;

import java.sql.*;
import java.util.ArrayList;

public class CoffeeDAO {

    // Get all coffee items
    public ObservableList<model.CoffeeShop.Coffee> getAllCoffee() {
        ObservableList<Coffee> coffeeList = FXCollections.observableArrayList();
        String query = "SELECT * FROM product";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Coffee coffee = new Coffee(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("category_id"),
                        rs.getDouble("price"),
                        0, // quantity không còn được sử dụng
                        rs.getString("image"),
                        rs.getInt("unit_id"),
                        rs.getString("description")
                );
                coffee.setStatus(rs.getInt("status"));
                coffeeList.add(coffee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return coffeeList;
    }

    // Add new coffee
    public boolean addCoffee(Coffee coffee) {
        String query = "INSERT INTO product (name, category_id, price, status, image, unit_id, description) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, coffee.getName());
            stmt.setInt(2, coffee.getCategoryId());
            stmt.setDouble(3, coffee.getPrice());
            stmt.setInt(4, coffee.getStatus());
            stmt.setString(5, coffee.getImage());
            stmt.setInt(6, coffee.getUnitId());
            stmt.setString(7, coffee.getDescription());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update existing coffee
    public boolean updateCoffee(Coffee coffee) {
        String query = "UPDATE product SET name = ?, category_id = ?, price = ?, status = ?, image = ?, unit_id = ?, description = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, coffee.getName());
            stmt.setInt(2, coffee.getCategoryId());
            stmt.setDouble(3, coffee.getPrice());
            stmt.setInt(4, coffee.getStatus());
            stmt.setString(5, coffee.getImage());
            stmt.setInt(6, coffee.getUnitId());
            stmt.setString(7, coffee.getDescription());
            stmt.setInt(8, coffee.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete coffee
    public boolean deleteCoffee(int coffeeId) {
        String query = "DELETE FROM product WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, coffeeId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get coffee by ID
    public Coffee getCoffeeById(int coffeeId) {
        String query = "SELECT * FROM product WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, coffeeId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Coffee coffee = new Coffee(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("category_id"),
                            rs.getDouble("price"),
                            0, // quantity không còn được sử dụng
                            rs.getString("image"),
                            rs.getInt("unit_id"),
                            rs.getString("description")
                    );
                    coffee.setStatus(rs.getInt("status"));
                    return coffee;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    
    // Get all available images from database
    public ObservableList<String> getAllImages() {
        ObservableList<String> imageList = FXCollections.observableArrayList();
        String query = "SELECT DISTINCT image FROM product WHERE image IS NOT NULL AND image != ''";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                String imagePath = rs.getString("image");
                if (imagePath != null && !imagePath.isEmpty()) {
                    imageList.add(imagePath);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return imageList;
    }
}