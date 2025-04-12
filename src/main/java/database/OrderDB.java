package database;

import helper.Alert;
import helper.ConnectDatabase;
import model.Order;
import model.OrderStatistic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        try (Connection conn = ConnectDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setUserId(rs.getInt("user_id"));
                order.setTableId(rs.getInt("table_id"));
                order.setTableName(TableDB.getInstance().getTableNameById(rs.getInt("table_id")));
                order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
                order.setStatus(rs.getString("status"));
                order.setTotalPrice(rs.getDouble("total_price"));
                order.setPaymentMethod(rs.getString("payment_method"));
                orderList.add(order);
            }
        } catch (SQLException e) {
            Alert.showAlert("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return orderList;
    }

    public List<Order> getAllOrdersByUserId(int userId) {
        List<Order> orderList = new ArrayList<>();
        String query = "SELECT * FROM `orders` WHERE user_id = ?";
        try (Connection conn = ConnectDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setTableId(rs.getInt("table_id"));
                order.setTableName(TableDB.getInstance().getTableNameById(rs.getInt("table_id")));
                order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
                order.setStatus(rs.getString("status"));
                order.setTotalPrice(rs.getDouble("total_price"));
                order.setPaymentMethod(rs.getString("payment_method"));
                orderList.add(order);
            }
        } catch (SQLException e) {
            Alert.showAlert("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return orderList;
    }

    public Order getOrderById(int id) {
        String query = "SELECT * FROM `orders` WHERE id = ?";
        try (Connection conn = ConnectDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order order = new Order();
                    order.setId(rs.getInt("id"));
                    order.setUserId(rs.getInt("user_id"));
                    order.setTableId(rs.getInt("table_id"));
                    order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
                    order.setStatus(rs.getString("status"));
                    order.setTotalPrice(rs.getDouble("total_price"));
                    order.setPaymentMethod(rs.getString("payment_method"));
                    return order;
                }
            }
        } catch (SQLException e) {
            Alert.showAlert("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void createOrder(Order order) {
        String query = "INSERT INTO `orders` (user_id, table_id, payment_method) VALUES (?, ?, ?)";
        try (Connection conn = ConnectDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, order.getUserId());
            ps.setInt(2, order.getTableId());
            ps.setString(3, order.getPaymentMethod());
            if (ps.executeUpdate() > 0) {
                Alert.showSuccess("Order created successfully");
            }
        } catch (SQLException e) {
            Alert.showAlert("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateOrder(Order order) {
        String query = "UPDATE `orders` SET table_id = ?, status = ?, payment_method = ? WHERE id = ?";
        try (Connection conn = ConnectDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, order.getTableId());
            ps.setString(2, order.getStatus());
            ps.setString(3, order.getPaymentMethod());
            ps.setInt(4, order.getId());
            if (ps.executeUpdate() > 0) {
                Alert.showSuccess("Order updated successfully");
            }
        } catch (SQLException e) {
            Alert.showAlert("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<OrderStatistic> getOrderMonthlyStatistic() {
        List<OrderStatistic> orderStatisticList = new ArrayList<>();
        String query = """
                SELECT
                    YEAR(order_date) AS year,
                    MONTH(order_date) AS month,
                    COUNT(id) AS order_count,
                    SUM(total_price) AS revenue
                FROM
                    orders
                GROUP BY
                    YEAR(order_date),
                    MONTH(order_date)
                """;
        try (Connection connection = ConnectDatabase.getConnection(); PreparedStatement ps = connection.prepareStatement(query); ResultSet resultSet = ps.executeQuery(query)) {
            while (resultSet.next()) {
                OrderStatistic orderStatistic = new OrderStatistic();
                orderStatistic.setYear(resultSet.getInt("year"));
                orderStatistic.setMonth(resultSet.getInt("month"));
                orderStatistic.setOrderCount(resultSet.getInt("order_count"));
                orderStatistic.setRevenue(resultSet.getDouble("revenue"));
                orderStatisticList.add(orderStatistic);
            }
        } catch (SQLException e) {
            Alert.showAlert("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return orderStatisticList;
    }

    public List<OrderStatistic> getOrderDailyStatistic() {
        List<OrderStatistic> orderStatisticList = new ArrayList<>();
        String query = """
                SELECT
                    YEAR(order_date) AS year,
                    MONTH(order_date) AS month,
                    DAY(order_date) AS day,
                    COUNT(id) AS order_count,
                    SUM(total_price) AS revenue
                FROM
                    orders
                GROUP BY
                    YEAR(order_date),
                    MONTH(order_date),
                    DAY(order_date)
                """;
        try (Connection connection = ConnectDatabase.getConnection(); PreparedStatement ps = connection.prepareStatement(query); ResultSet resultSet = ps.executeQuery(query)) {
            while (resultSet.next()) {
                OrderStatistic orderStatistic = new OrderStatistic();
                orderStatistic.setYear(resultSet.getInt("year"));
                orderStatistic.setMonth(resultSet.getInt("month"));
                orderStatistic.setDay(resultSet.getInt("day"));
                orderStatistic.setOrderCount(resultSet.getInt("order_count"));
                orderStatistic.setRevenue(resultSet.getDouble("revenue"));
                orderStatisticList.add(orderStatistic);
            }
        } catch (SQLException e) {
            Alert.showAlert("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return orderStatisticList;
    }

    public OrderStatistic getTotalOrderCountAndTotalRevenue() {
        String query = """
                SELECT
                    COUNT(id) AS order_count,
                    SUM(total_price) AS revenue
                FROM
                    orders
                """;
        try (Connection connection = ConnectDatabase.getConnection(); PreparedStatement ps = connection.prepareStatement(query); ResultSet rs = ps.executeQuery(query)) {
            if (rs.next()) {
                OrderStatistic orderStatistic = new OrderStatistic();
                orderStatistic.setOrderCount(rs.getInt("order_count"));
                orderStatistic.setRevenue(rs.getDouble("revenue"));
                return orderStatistic;
            }
        } catch (SQLException e) {
            Alert.showAlert("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
