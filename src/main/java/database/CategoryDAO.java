package database;

import helper.ConnectDatabase;
import model.Category;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class CategoryDAO {

    // Get all categories
    public ObservableList<Category> getAllCategories() {
        ObservableList<Category> categoryList = FXCollections.observableArrayList();
        String query = "SELECT * FROM category";

        try (Connection conn = ConnectDatabase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Category category = new Category(
                        rs.getInt("id"),
                        rs.getString("category_name"),
                        rs.getString("description")
                );
                categoryList.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categoryList;
    }

    // Get category by ID
    public Category getCategoryById(int categoryId) {
        String query = "SELECT * FROM category WHERE id = ?";

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, categoryId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Category(
                            rs.getInt("id"),
                            rs.getString("category_name"),
                            rs.getString("description")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}