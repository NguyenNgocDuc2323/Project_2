package database;

import helper.Alert;
import helper.ConnectDatabase;
import model.Category;
import model.Category;
import model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDB {
    private static CategoryDB instance;

    public CategoryDB() {
    }

    public static CategoryDB getInstance() {
        if (instance == null) {
            instance = new CategoryDB();
        }
        return instance;
    }

    public List<Category> getAllCategory() {
        List<Category> categoryList = new ArrayList<>();
        String query = "SELECT * FROM `category`";
        try (Connection conn = ConnectDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Category category = new Category();
                category.setId(rs.getInt("id"));
                category.setName(rs.getString("category_name"));
                category.setDescription(rs.getString("description"));
                categoryList.add(category);
            }
        } catch (SQLException e) {
            Alert.showAlert("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return categoryList;
    }

    public Category getCategoryById(int id) {
        String query = "SELECT * FROM `category` WHERE id = ?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Category category = new Category();
                    category.setId(rs.getInt("id"));
                    category.setName(rs.getString("category_name"));
                    category.setDescription(rs.getString("description"));
                    return category;
                }
            }
        } catch (SQLException e) {
            Alert.showAlert("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    public void createCategory(Category category) {
        String query = "INSERT INTO `category` (category_name, description) VALUES (?, ?)";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int newId = generatedKeys.getInt(1);
                        category.setId(newId); // lưu lại ID cho thao tác tiếp theo
                        System.out.println("Created category ID: " + newId);
                    }
                }
                Alert.showSuccess("Category created successfully");
            }
        } catch (SQLException e) {
            Alert.showAlert("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateCategory(Category category) {
        String query = "UPDATE `category` SET category_name = ?, description = ? WHERE id = ?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());
            ps.setInt(3, category.getId());

            if (ps.executeUpdate() > 0) {
                Alert.showSuccess("Category updated successfully");
            } else {
                Alert.showAlert("Update failed: category may not exist.");
            }
        } catch (SQLException e) {
            Alert.showAlert("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteCategory(int id) {
        String checkQuery = "SELECT COUNT(*) AS product_count FROM `product` WHERE category_id = ?";
        String deleteQuery = "DELETE FROM `category` WHERE id = ?";

        try (Connection conn = ConnectDatabase.getConnection()) {
            // Kiểm tra xem có sản phẩm nào đang dùng category này không
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, id);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        int count = rs.getInt("product_count");
                        System.out.println("Related product count: " + count);
                        if (count > 0) {
                            Alert.showAlert("Cannot delete this category because it is being used by " + count + " product(s).");
                            return;
                        }
                    }
                }
            }

            // Xóa nếu không bị liên kết
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                deleteStmt.setInt(1, id);
                int rowsAffected = deleteStmt.executeUpdate();
                if (rowsAffected > 0) {
                    Alert.showSuccess("Category deleted successfully.");
                } else {
                    Alert.showAlert("Delete failed: category may not exist.");
                }
            }

        } catch (SQLException e) {
            Alert.showAlert("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }




}
