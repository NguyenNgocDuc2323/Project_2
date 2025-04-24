package helper.DB_Helper;

import helper.ConnectDatabase;
import model.Admin.ProductSale;

import java.sql.*;
import java.time.LocalDateTime;
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
    public static List<ProductSale> getProductData(String groupBy, Integer day, Integer month, Integer year) throws SQLException {
        List<ProductSale> products = new ArrayList<>();
        String query;

        switch (groupBy.toLowerCase()) {
            case "month":
                query = "SELECT p.id, p.name, c.category_name as category, " +
                        "SUM(od.quantity) as total_sold, SUM(od.quantity * od.unit_price) as total_revenue, " +
                        "MAX(o.order_date) as time " +
                        "FROM product p " +
                        "JOIN order_detail od ON p.id = od.product_id " +
                        "JOIN orders o ON od.order_id = o.id " +
                        "JOIN category c ON p.category_id = c.id " +
                        "WHERE o.order_date IS NOT NULL " +
                        (year != null ? "AND YEAR(o.order_date) = ? " : "") +
                        (month != null ? "AND MONTH(o.order_date) = ? " : "") +
                        "GROUP BY p.id, p.name, c.category_name, YEAR(o.order_date), MONTH(o.order_date)";
                break;
            case "year":
                query = "SELECT p.id, p.name, c.category_name as category, " +
                        "SUM(od.quantity) as total_sold, SUM(od.quantity * od.unit_price) as total_revenue, " +
                        "MAX(o.order_date) as time " +
                        "FROM product p " +
                        "JOIN order_detail od ON p.id = od.product_id " +
                        "JOIN orders o ON od.order_id = o.id " +
                        "JOIN category c ON p.category_id = c.id " +
                        "WHERE o.order_date IS NOT NULL " +
                        (year != null ? "AND YEAR(o.order_date) = ? " : "") +
                        "GROUP BY p.id, p.name, c.category_name, YEAR(o.order_date)";
                break;
            default: // day
                query = "SELECT p.id, p.name, c.category_name as category, " +
                        "SUM(od.quantity) as total_sold, SUM(od.quantity * od.unit_price) as total_revenue, " +
                        "MAX(o.order_date) as time " +
                        "FROM product p " +
                        "JOIN order_detail od ON p.id = od.product_id " +
                        "JOIN orders o ON od.order_id = o.id " +
                        "JOIN category c ON p.category_id = c.id " +
                        "WHERE o.order_date IS NOT NULL " +
                        (year != null ? "AND YEAR(o.order_date) = ? " : "") +
                        (month != null ? "AND MONTH(o.order_date) = ? " : "") +
                        (day != null ? "AND DAY(o.order_date) = ? " : "") +
                        "GROUP BY p.id, p.name, c.category_name, DATE(o.order_date)";
                break;
        }

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            int paramIndex = 1;
            if (year != null) {
                stmt.setInt(paramIndex++, year);
            }
            if (month != null) {
                stmt.setInt(paramIndex++, month);
            }
            if (day != null && groupBy.equalsIgnoreCase("day")) {
                stmt.setInt(paramIndex, day);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                LocalDateTime timeValue = rs.getTimestamp("time").toLocalDateTime();
                products.add(new ProductSale(
                        timeValue,
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
