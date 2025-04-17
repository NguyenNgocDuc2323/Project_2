package helper.DB_Helper;

import helper.ConnectDatabase;
import model.Admin.OrderDetailDisplay;
import model.Order;
import model.OrderDetail;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static List<OrderDetailDisplay> getRevenueData(String sortBy, String sortOrder) throws SQLException {
        List<OrderDetailDisplay> displayList = new ArrayList<>();
        List<Order> orders = getAllOrdersWithDetails();
        for (Order order : orders) {
            for (OrderDetail detail : order.getOrderDetails()) {
                displayList.add(new OrderDetailDisplay(
                        detail.getCategoryName(),
                        detail.getProductName(),
                        detail.getQuantity(),
                        detail.getUnitPrice() * detail.getQuantity(),
                        order.getOrderDate()
                ));
            }
        }
        if ("amount".equals(sortBy)) {
            displayList.sort((a, b) -> "ASC".equals(sortOrder) ?
                    Double.compare(a.getAmount(), b.getAmount()) :
                    Double.compare(b.getAmount(), a.getAmount()));
        } else if ("date".equals(sortBy)) {
            displayList.sort((a, b) -> "ASC".equals(sortOrder) ?
                    a.getDate().compareTo(b.getDate()) :
                    b.getDate().compareTo(a.getDate()));
        }
        return displayList;
    }
}
