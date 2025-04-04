package database;

import helper.Alert;
import helper.ConnectDatabase;
import model.Category;
import model.Category;
import model.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());
            if (ps.executeUpdate() > 0) {
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
            }
        } catch (SQLException e) {
            Alert.showAlert("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteCategory(int id) {
        String query = "DELETE FROM `category` WHERE id = ?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() > 0) {
                Alert.showSuccess("Category deleted successfully");
            }
        } catch (SQLException e) {
            Alert.showAlert("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
