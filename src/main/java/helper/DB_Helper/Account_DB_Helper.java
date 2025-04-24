package helper.DB_Helper;

import helper.ConnectDatabase;
import model.Admin.CustomerPurchase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Account_DB_Helper {
    public static model.Account getAccountByEmailAndPassword(String email, String password) {
        try(Connection conn = ConnectDatabase.getConnection();) {
            PreparedStatement ps = conn.prepareStatement("select * from account where email = ? and password = ?");
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String result_email = rs.getString("email");
                String result_password = rs.getString("password");
                int type = rs.getInt("type");
                boolean lock_status = rs.getBoolean("lock_status");
                return new model.Account(id,name,result_email,result_password,type,lock_status);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void addAccount(model.Account account) {
        try (Connection conn = ConnectDatabase.getConnection()) {
            String checkQuery = "SELECT * FROM account WHERE email = ?";
            PreparedStatement checkPs = conn.prepareStatement(checkQuery);
            checkPs.setString(1, account.getEmail());
            ResultSet rs = checkPs.executeQuery();
            if (rs.next()) {
                System.out.println("Account_DB_Helper with email " + account.getEmail() + " already exists.");
                return;
            }
            String insertQuery = "INSERT INTO account (email, password, type, lock_status) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(insertQuery);
            ps.setString(1, account.getEmail());
            ps.setString(2, account.getPassword());
            ps.setInt(3, account.getType());
            ps.setBoolean(4, account.isLocked());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Account_DB_Helper successfully added: " + account.getEmail());
            } else {
                System.out.println("Failed to add the account.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static int countAcc() {
        int count = 0;
        String query = "SELECT COUNT(*) FROM account";
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
    public static String getEmailByAccountId(int accountId) {
        String email = null;
        String query = "SELECT email FROM account WHERE id = ?";

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                email = rs.getString("email");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return email;
    }
    public static boolean resetPassword(int accountId, String newPassword) {
        String query = "UPDATE account SET password = ? WHERE id = ?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newPassword);
            stmt.setInt(2, accountId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getAccountIdByEmail(String email) {
        int accountId = 0;
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id FROM account WHERE email = ?")) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                accountId = rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accountId;
    }
    public static ResultSet getAccounts(){
        String query = "SELECT * FROM account";
        try (Connection conn = ConnectDatabase.getConnection()){
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            return rs;
        }
        catch (SQLException exception){
            exception.printStackTrace();
        }
        return null;
    }
    public static int getTotalCustomers() throws SQLException {
        String query = "SELECT COUNT(DISTINCT user_id) FROM orders";
        try (Connection conn = ConnectDatabase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
    public static List<CustomerPurchase> getCustomerData(String groupBy, Integer day, Integer month, Integer year) throws SQLException {
        List<CustomerPurchase> customers = new ArrayList<>();
        String query;

        switch (groupBy.toLowerCase()) {
            case "month":
                query = "SELECT c.id, c.full_name, COUNT(o.id) as order_count, " +
                        "SUM(o.total_price) as total_spent, MAX(o.order_date) as last_order_date " +
                        "FROM account c LEFT JOIN orders o ON c.id = o.user_id " +
                        "WHERE o.order_date IS NOT NULL " +
                        (year != null ? "AND YEAR(o.order_date) = ? " : "") +
                        (month != null ? "AND MONTH(o.order_date) = ? " : "") +
                        "GROUP BY c.id, c.full_name, YEAR(o.order_date), MONTH(o.order_date)";
                break;
            case "year":
                query = "SELECT c.id, c.full_name, COUNT(o.id) as order_count, " +
                        "SUM(o.total_price) as total_spent, MAX(o.order_date) as last_order_date " +
                        "FROM account c LEFT JOIN orders o ON c.id = o.user_id " +
                        "WHERE o.order_date IS NOT NULL " +
                        (year != null ? "AND YEAR(o.order_date) = ? " : "") +
                        "GROUP BY c.id, c.full_name, YEAR(o.order_date)";
                break;
            default:
                query = "SELECT c.id, c.full_name, COUNT(o.id) as order_count, " +
                        "SUM(o.total_price) as total_spent, MAX(o.order_date) as last_order_date " +
                        "FROM account c LEFT JOIN orders o ON c.id = o.user_id " +
                        "WHERE o.order_date IS NOT NULL " +
                        (year != null ? "AND YEAR(o.order_date) = ? " : "") +
                        (month != null ? "AND MONTH(o.order_date) = ? " : "") +
                        (day != null ? "AND DAY(o.order_date) = ? " : "") +
                        "GROUP BY c.id, c.full_name, DATE(o.order_date)";
                break;
        }

        try (Connection conn = ConnectDatabase.getConnection(); // Assume getConnection() is defined
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
                customers.add(new CustomerPurchase(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getInt("order_count"),
                        rs.getDouble("total_spent"),
                        rs.getTimestamp("last_order_date") != null ? rs.getTimestamp("last_order_date").toLocalDateTime() : null
                ));
            }
        }

        return customers;
    }
}
