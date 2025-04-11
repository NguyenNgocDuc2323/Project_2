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

    // Add new account
    public boolean addAccount(Account account) {
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

    // Update existing account
    public boolean updateAccount(Account account) {
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

    // Delete account
    public boolean deleteAccount(int accountId) {
        String query = "DELETE FROM account WHERE id = ?";

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, accountId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
}