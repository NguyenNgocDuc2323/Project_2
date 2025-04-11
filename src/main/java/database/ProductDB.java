package database;

import helper.Alert;
import helper.ConnectDatabase;
import model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDB {
    private static ProductDB instance ;

    public static ProductDB getInstance() {
        if (instance == null) {
            instance = new ProductDB();
        }
        return instance;
    }

    private ProductDB() {
    }

    public List<String> getAllProductsName() {
        List<String> productList = new ArrayList<>();
        String query = "SELECT name FROM product";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                productList.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            Alert.showAlert("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return productList;
    }

    public Product getProductById(int id) {
        String query = "SELECT * FROM `product` WHERE id = ?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Product product = new Product();
                    product.setId(rs.getInt("id"));
                    product.setName(rs.getString("name"));
                    product.setCategoryId(rs.getInt("category_id"));
                    product.setPrice(rs.getDouble("price"));
                    product.setQuantity(rs.getInt("quantity"));
                    product.setImage(rs.getString("image"));
                    product.setUnitId(rs.getInt("unit_id"));
                    product.setDescription(rs.getString("description"));
                    return product;
                }
            }
        } catch (SQLException e) {
            Alert.showAlert("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Product getProductByName(String name) {
        String query = "SELECT * FROM `product` WHERE name = ?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Product product = new Product();
                    product.setId(rs.getInt("id"));
                    product.setName(rs.getString("name"));
                    product.setCategoryId(rs.getInt("category_id"));
                    product.setPrice(rs.getDouble("price"));
                    product.setQuantity(rs.getInt("quantity"));
                    product.setImage(rs.getString("image"));
                    product.setUnitId(rs.getInt("unit_id"));
                    product.setDescription(rs.getString("description"));
                    return product;
                }
            }
        } catch (SQLException e) {
            Alert.showAlert("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public String getProductNameById(int id) {
        String query = "SELECT name FROM `product` WHERE id = ?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        } catch (SQLException e) {
            Alert.showAlert("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
