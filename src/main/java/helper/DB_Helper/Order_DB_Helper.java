package helper.DB_Helper;

import helper.ConnectDatabase;
import model.Admin.OrderDetailDisplay;
import model.Admin.RevenueSummary;
import model.Order;
import model.OrderDetail;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class Order_DB_Helper {
    public static int countOrder() {
        int count = 0;
        String query = "SELECT COUNT(*) FROM orders";
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

    public static List<Order> getAllOrdersWithDetails() {
        List<Order> orders = new ArrayList<>();
        Map<Integer, Order> orderMap = new HashMap<>();

        String orderSql = "SELECT * FROM orders ORDER BY total_price DESC";
        String detailSql = """
        SELECT od.*, p.name AS product_name, c.category_name
        FROM order_detail od
        JOIN product p ON od.product_id = p.id
        JOIN category c ON p.category_id = c.id
    """;

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement orderStmt = conn.prepareStatement(orderSql);
             PreparedStatement detailStmt = conn.prepareStatement(detailSql)) {

            ResultSet orderRs = orderStmt.executeQuery();
            while (orderRs.next()) {
                Order order = new Order(
                        orderRs.getInt("id"),
                        orderRs.getInt("user_id"),
                        orderRs.getInt("table_id"),
                        null,
                        orderRs.getTimestamp("order_date").toLocalDateTime(),
                        orderRs.getString("status"),
                        orderRs.getDouble("total_price"),
                        orderRs.getString("payment_method")
                );
                orders.add(order);
                orderMap.put(order.getId(), order);
            }

            ResultSet detailRs = detailStmt.executeQuery();
            while (detailRs.next()) {
                OrderDetail detail = new OrderDetail(
                        detailRs.getInt("id"),
                        detailRs.getInt("order_id"),
                        detailRs.getInt("product_id"),
                        detailRs.getString("product_name"),
                        detailRs.getString("category_name"),
                        detailRs.getInt("quantity"),
                        detailRs.getDouble("unit_price")
                );
                Order order = orderMap.get(detail.getOrderId());
                if (order != null) {
                    order.getOrderDetails().add(detail);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }
    public static double getTotalRevenue() throws SQLException {
        String query = "SELECT SUM(total_price) FROM orders WHERE status = 'completed'";
        try (Connection conn = ConnectDatabase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            return rs.next() ? rs.getDouble(1) : 0;
        }
    }

    public static int getTotalProductsSold() throws SQLException {
        String query = "SELECT SUM(quantity) FROM order_detail";
        try (Connection conn = ConnectDatabase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public static List<OrderDetailDisplay> getRevenueData(String groupBy, Integer day, Integer month, Integer year) throws SQLException {
        List<OrderDetailDisplay> details = new ArrayList<>();

        String dateFormat;
        switch (groupBy != null ? groupBy.toLowerCase() : "day") {
            case "day":
                dateFormat = "DATE_FORMAT(o.order_date, '%Y-%m-%d')";
                break;
            case "month":
                dateFormat = "DATE_FORMAT(o.order_date, '%Y-%m')";
                break;
            case "year":
                dateFormat = "DATE_FORMAT(o.order_date, '%Y')";
                break;
            default:
                throw new IllegalArgumentException("Invalid groupBy value: " + groupBy);
        }

        StringBuilder query = new StringBuilder(
                "SELECT c.category_name, " +
                        "SUM(od.unit_price * od.quantity) AS total_amount, " +
                        "MAX(o.order_date) AS last_order_date " +
                        "FROM order_detail od " +
                        "JOIN orders o ON od.order_id = o.id " +
                        "JOIN product p ON od.product_id = p.id " +
                        "JOIN category c ON p.category_id = c.id " +
                        "WHERE o.order_date IS NOT NULL "
        );

        // Thêm điều kiện lọc theo day, month, year
        List<String> conditions = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();
        if (day != null) {
            conditions.add("DAY(o.order_date) = ?");
            parameters.add(day);
        }
        if (month != null) {
            conditions.add("MONTH(o.order_date) = ?");
            parameters.add(month);
        }
        if (year != null) {
            conditions.add("YEAR(o.order_date) = ?");
            parameters.add(year);
        }
        if (!conditions.isEmpty()) {
            query.append(" AND ").append(String.join(" AND ", conditions));
        }

        query.append(" GROUP BY c.category_name, ").append(dateFormat)
                .append(" ORDER BY total_amount DESC");

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {
            // Gán giá trị cho các tham số
            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Timestamp timestamp = rs.getTimestamp("last_order_date");
                    LocalDateTime lastOrderDate = timestamp != null ? timestamp.toLocalDateTime() : null;

                    details.add(new OrderDetailDisplay(
                            rs.getString("category_name"),
                            null, // productName không cần
                            0,    // quantity không cần
                            rs.getDouble("total_amount"),
                            lastOrderDate
                    ));
                }
            }
        }
        return details;
    }
}
