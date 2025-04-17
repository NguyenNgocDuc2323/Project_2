package helper.DB_Helper;

import helper.ConnectDatabase;
import model.Admin.ProductSale;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Product_DB_Helper {
    public static int countProducts() {
        int count = 0;
        String query = "SELECT COUNT(*) FROM product";
        try (Connection conn = ConnectDatabase.getConnection()) {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
    public static List<ProductSale> getProductData(String sortBy, String sortOrder) throws SQLException {
        List<ProductSale> products = new ArrayList<>();

        String validSortBy = switch (sortBy) {
            case "name", "category", "total_sold", "total_revenue" -> sortBy;
            default -> "name";
        };
        String validSortOrder = sortOrder.equalsIgnoreCase("DESC") ? "DESC" : "ASC";

        String query = "SELECT p.id, p.name, c.category_name as category, SUM(oi.quantity) as total_sold, " +
                "SUM(oi.quantity * oi.unit_price) as total_revenue " +
                "FROM product p " +
                "JOIN order_detail oi ON p.id = oi.product_id " +
                "JOIN category c ON p.category_id = c.id " +
                "GROUP BY p.id, p.name, c.category_name " +
                "ORDER BY " + validSortBy + " " + validSortOrder;

        try (
                Connection conn = ConnectDatabase.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)
        ) {
            while (rs.next()) {
                products.add(new ProductSale(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getInt("total_sold"),
                        rs.getDouble("total_revenue")
                ));
            }
        }
        return products;
    }
}
