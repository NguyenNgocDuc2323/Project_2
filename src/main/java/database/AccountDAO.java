package database;

import helper.ConnectDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Account;

import java.sql.*;

public class AccountDAO {

    // Get all accounts
    public ObservableList<Account> getAllAccounts() {
        ObservableList<Account> accountList = FXCollections.observableArrayList();
        String query = "SELECT * FROM account";

        try (Connection conn = ConnectDatabase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Account account = new Account(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getInt("type"),
                        rs.getBoolean("lock_status")
                );
                accountList.add(account);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return accountList;
    }

    public boolean addAccount(Account account) {
        if (emailExists(account.getEmail())) {
            System.out.println("Email đã tồn tại!");
            return false;
        }

        String query = "INSERT INTO account (full_name, email, password, type, lock_status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, account.getName());
            stmt.setString(2, account.getEmail());
            stmt.setString(3, account.getPassword());
            stmt.setInt(4, account.getType());
            stmt.setBoolean(5, account.isLocked());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAccount(int accountId) throws SQLException {
        String checkAdminQuery = "SELECT type FROM account WHERE id = ?";

        try (Connection conn = ConnectDatabase.getConnection()) {
            try (PreparedStatement checkAdminStmt = conn.prepareStatement(checkAdminQuery)) {
                checkAdminStmt.setInt(1, accountId);
                ResultSet rsAdmin = checkAdminStmt.executeQuery();
                if (rsAdmin.next() && rsAdmin.getInt("type") == 1) {
                    throw new SQLException("Cannot delete user because they are an admin.");
                }
            }

            String checkOrdersQuery = "SELECT COUNT(*) FROM orders WHERE user_id = ?";
            try (PreparedStatement checkOrdersStmt = conn.prepareStatement(checkOrdersQuery)) {
                checkOrdersStmt.setInt(1, accountId);
                ResultSet rsOrders = checkOrdersStmt.executeQuery();
                if (rsOrders.next() && rsOrders.getInt(1) > 0) {
                    throw new SQLException("Cannot delete user because they have associated orders.");
                }
            }

            String deleteQuery = "DELETE FROM account WHERE id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                deleteStmt.setInt(1, accountId);
                int rowsAffected = deleteStmt.executeUpdate();
                return rowsAffected > 0;
            }
        }
    }

    // Get account by ID
    public Account getAccountById(int accountId) {
        String query = "SELECT * FROM account WHERE id = ?";


        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, accountId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Account(
                            rs.getInt("id"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getInt("type"),
                            rs.getBoolean("lock_status")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean updateAccount(Account account) {
        if (emailExistsForOtherAccount(account.getEmail(), account.getId())) {
            System.out.println("Email đã tồn tại ở tài khoản khác!");
            return false;
        }

        String query = "UPDATE account SET full_name = ?, email = ?, password = ?, type = ?, lock_status = ? WHERE id = ?";

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, account.getName());
            stmt.setString(2, account.getEmail());
            stmt.setString(3, account.getPassword());
            stmt.setInt(4, account.getType());
            stmt.setBoolean(5, account.isLocked());
            stmt.setInt(6, account.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM account WHERE email = ?";

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean emailExistsForOtherAccount(String email, int id) {
        String query = "SELECT COUNT(*) FROM account WHERE email = ? AND id != ?";

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setInt(2, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}