package database;

import helper.Alert;
import helper.ConnectDatabase;
import model.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDB {
    private static OrderDB instance;

    public OrderDB() {
    }

    public static OrderDB getInstance() {
        if (instance == null) {
            instance = new OrderDB();
        }
        return instance;
    }

    public List<Order> getAllOrders() {
        List<Order> orderList = new ArrayList<>();
        String query = "SELECT * FROM `orders`";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int orderId = rs.getInt("id");
                int userId = rs.getInt("user_id");
                int tableId = rs.getInt("table_id");
                String tableName = TableDB.getInstance().getTableNameById(tableId);
                LocalDateTime orderDate = rs.getTimestamp("order_date").toLocalDateTime();
                String status = rs.getString("status");
                double totalPrice = rs.getDouble("total_price");
                String paymentMethod = rs.getString("payment_method");
                orderList.add(new Order(orderId, userId, tableId, tableName, orderDate, status, totalPrice, paymentMethod));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderList;
    }

    public List<Order> getAllOrdersByUserId(int userId) {
        List<Order> orderList = new ArrayList<>();
        String query = "SELECT * FROM `orders` WHERE user_id = ?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int orderId = rs.getInt("id");
                int tableId = rs.getInt("table_id");
                String tableName = TableDB.getInstance().getTableNameById(tableId);
                LocalDateTime orderDate = rs.getTimestamp("order_date").toLocalDateTime();
                String status = rs.getString("status");
                double totalPrice = rs.getDouble("total_price");
                String paymentMethod = rs.getString("payment_method");
                orderList.add(new Order(orderId, userId, tableId, tableName, orderDate, status, totalPrice, paymentMethod));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderList;
    }

    public Order getOrderById(int id) {
        String query = "SELECT * FROM `orders` WHERE id = ?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Order(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getInt("table_id"),
                            rs.getTimestamp("order_date").toLocalDateTime(),
                            rs.getString("status"),
                            rs.getDouble("total_price"),
                            rs.getString("payment_method")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void createOrder(Order order) {
        String query = "INSERT INTO `orders` (user_id, table_id, payment_method) VALUES (?, ?, ?)";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, order.getUserId());
            ps.setInt(2, order.getTableId());
            ps.setString(3, order.getPaymentMethod());
            if (ps.executeUpdate() > 0) {
                Alert.showSuccess("Order created successfully");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateOrderStatus(Order order) {
        String query = "UPDATE `orders` SET status = ? WHERE id = ?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, order.getStatus());
            ps.setInt(2, order.getId());
            if (ps.executeUpdate() > 0) {
                Alert.showSuccess("Order updated successfully");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
