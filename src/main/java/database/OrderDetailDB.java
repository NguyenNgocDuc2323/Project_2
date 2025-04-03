package database;

import helper.Alert;
import helper.ConnectDatabase;
import model.OrderDetail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailDB {
    private static OrderDetailDB instance;

    public OrderDetailDB() {
    }

    public static OrderDetailDB getInstance() {
        if (instance == null) {
            instance = new OrderDetailDB();
        }
        return instance;
    }

    public List<OrderDetail> getAllOrderDetailByOrderId(int orderId) {
        List<OrderDetail> orderDetailList = new ArrayList<>();
        String query = "SELECT * FROM `order_detail` WHERE order_id = ?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setId(rs.getInt("id"));
                    orderDetail.setOrderId(rs.getInt("order_id"));
                    orderDetail.setProductId(rs.getInt("product_id"));
                    orderDetail.setProductName(ProductDB.getInstance().getProductNameById(rs.getInt("product_id")));
                    orderDetail.setQuantity(rs.getInt("quantity"));
                    orderDetail.setUnitPrice(rs.getDouble("unit_price"));
                    orderDetailList.add(orderDetail);
                }
            }
        } catch (SQLException e) {
            Alert.showAlert("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return orderDetailList;
    }

    public OrderDetail getOrderDetailById(int id) {
        String query = "SELECT * FROM `order_detail` WHERE id = ?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setId(rs.getInt("id"));
                    orderDetail.setOrderId(rs.getInt("order_id"));
                    orderDetail.setProductId(rs.getInt("product_id"));
                    orderDetail.setQuantity(rs.getInt("quantity"));
                    orderDetail.setUnitPrice(rs.getDouble("unit_price"));
                    return orderDetail;
                }
            }
        } catch (SQLException e) {
            Alert.showAlert("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public void createOrderDetailAndUpdateOrder(OrderDetail orderDetail) {
        String insertOrderDetail = "INSERT INTO `order_detail` (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
        String totalPriceQuery = "SELECT SUM(quantity * unit_price) AS total_price FROM order_detail WHERE order_id = ?";
        String updateOrder = "UPDATE `orders` SET total_price = ? WHERE id = ?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement insertPS = conn.prepareStatement(insertOrderDetail)) {
            conn.setAutoCommit(false);
            insertPS.setInt(1, orderDetail.getOrderId());
            insertPS.setInt(2, orderDetail.getProductId());
            insertPS.setInt(3, orderDetail.getQuantity());
            insertPS.setDouble(4, ProductDB.getInstance().getProductById(orderDetail.getProductId()).getPrice());
            if (insertPS.executeUpdate() > 0) {
                Alert.showSuccess("Order detail created successfully");
            }
            PreparedStatement totalPricePS = conn.prepareStatement(totalPriceQuery);
            totalPricePS.setInt(1, orderDetail.getOrderId());
            ResultSet rs = totalPricePS.executeQuery();
            double newTotalPrice = 0.0;
            if (rs.next()) {
                newTotalPrice = rs.getDouble("total_price");
            }
            PreparedStatement updatePS = conn.prepareStatement(updateOrder);
            updatePS.setDouble(1, newTotalPrice);
            updatePS.setInt(2, orderDetail.getOrderId());
            if (updatePS.executeUpdate() > 0) {
                Alert.showSuccess("Order total price updated successfully");
            }
            conn.commit();
        } catch (SQLException e) {
            Alert.showAlert("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateOrderDetailAndOrder(OrderDetail orderDetail) {
        String updateOrderDetail = "UPDATE `order_detail` SET product_id = ?, quantity = ?, unit_price = ? WHERE id = ?";
        String totalPriceQuery = "SELECT SUM(quantity * unit_price) AS total_price FROM order_detail WHERE order_id = ?";
        String updateOrder = "UPDATE `orders` SET total_price = ? WHERE id = ?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateOrderDetail)) {
            conn.setAutoCommit(false);
            ps.setInt(1, orderDetail.getProductId());
            ps.setInt(2, orderDetail.getQuantity());
            ps.setDouble(3, ProductDB.getInstance().getProductById(orderDetail.getProductId()).getPrice());
            ps.setInt(4, orderDetail.getId());
            if (ps.executeUpdate() > 0) {
                Alert.showSuccess("Order detail updated successfully");
            }
            PreparedStatement totalPricePS = conn.prepareStatement(totalPriceQuery);
            totalPricePS.setInt(1, orderDetail.getOrderId());
            ResultSet rs = totalPricePS.executeQuery();
            double newTotalPrice = 0.0;
            if (rs.next()) {
                newTotalPrice = rs.getDouble("total_price");
            }
            PreparedStatement updatePS = conn.prepareStatement(updateOrder);
            updatePS.setDouble(1, newTotalPrice);
            updatePS.setInt(2, orderDetail.getOrderId());
            if (updatePS.executeUpdate() > 0) {
                Alert.showSuccess("Order total price updated successfully");
            }
            conn.commit();
        } catch (SQLException e) {
            Alert.showAlert("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
