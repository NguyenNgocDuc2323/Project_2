package database;

import helper.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.CoffeeShop.Coffee;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

public class CoffeeDAO {
    private static final long MAX_IMAGE_SIZE = 2 * 1024 * 1024;
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
        if (isImageTooLarge(coffee.getImage())) {
            System.out.println("Image is too large. Cannot add to database.");
            return false;
        }

        if (isNameExistsInCategory(coffee.getName(), coffee.getCategoryId(), -1)) {
            System.out.println("Coffee name already exists in the selected category. Cannot add to database.");
            return false;
        }

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

    public boolean updateCoffee(Coffee coffee) {
        if (isImageTooLarge(coffee.getImage())) {
            System.out.println("Image is too large. Cannot update in database.");
            return false;
        }

        if (isNameExistsInCategory(coffee.getName(), coffee.getCategoryId(), coffee.getId())) {
            System.out.println("Coffee name already exists in the selected category. Cannot update in database.");
            return false;
        }

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

    private boolean isNameExistsInCategory(String name, int categoryId, int excludeId) {
        String query = "SELECT COUNT(*) FROM product WHERE name = ? AND category_id = ?";
        if (excludeId != -1) {
            query += " AND id <> ?";
        }

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name);
            stmt.setInt(2, categoryId);
            if (excludeId != -1) {
                stmt.setInt(3, excludeId);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isImageTooLarge(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return false;
        }

        File file = new File(imagePath);
        return file.exists() && file.length() > MAX_IMAGE_SIZE;
    }
    public boolean deleteCoffee(int coffeeId) throws SQLException {
        String checkOrderDetailQuery = "SELECT COUNT(*) FROM order_detail WHERE product_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            try (PreparedStatement checkOrderStmt = conn.prepareStatement(checkOrderDetailQuery)) {
                checkOrderStmt.setInt(1, coffeeId);
                ResultSet rsOrder = checkOrderStmt.executeQuery();
                if (rsOrder.next() && rsOrder.getInt(1) > 0) {
                    throw new SQLException("Cannot delete coffee because it is associated with an order.");
                }
            }

            // Nếu không thuộc danh mục và không có trong order_detail, tiến hành xóa
            String deleteQuery = "DELETE FROM product WHERE id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                deleteStmt.setInt(1, coffeeId);
                int rowsAffected = deleteStmt.executeUpdate();
                return rowsAffected > 0;
            }
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