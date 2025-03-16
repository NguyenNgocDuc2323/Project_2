package helper.DB_Helper;

import model.Account;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Account_DB_Helper {

    public static Account getAccountByEmailAndPassword(String email, String password) {
        try (Connection conn = ConnectDatabase.getConnection()) {
            String query = "SELECT * FROM account WHERE email = ? AND password = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addAccount(Account account) {
        String checkQuery = "SELECT * FROM account WHERE email = ?";
        String insertQuery = "INSERT INTO account (full_name, email, password, type, lock_status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkQuery);
             PreparedStatement ps = conn.prepareStatement(insertQuery)) {

            checkPs.setString(1, account.getEmail());
            ResultSet rs = checkPs.executeQuery();
            if (rs.next()) {
                System.out.println("Account with email " + account.getEmail() + " already exists.");
                return;
            }

            ps.setString(1, account.getName());
            ps.setString(2, account.getEmail());
            ps.setString(3, account.getPassword());
            ps.setInt(4, account.getType());
            ps.setBoolean(5, account.isLocked());

            int rowsAffected = ps.executeUpdate();
            System.out.println(rowsAffected > 0 ? "Account added successfully!" : "Failed to add account.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int countAcc() {
        String query = "SELECT COUNT(*) FROM account";
        try (Connection conn = ConnectDatabase.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getEmailByAccountId(int accountId) {
        String query = "SELECT email FROM account WHERE id = ?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("email");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean resetPassword(int accountId, String newPassword) {
        String query = "UPDATE account SET password = ? WHERE id = ?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newPassword);
            stmt.setInt(2, accountId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getAccountIdByEmail(String email) {
        String query = "SELECT id FROM account WHERE email = ?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        String query = "SELECT * FROM account";
        try (Connection conn = ConnectDatabase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                accounts.add(new Account(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getInt("type"),
                        rs.getBoolean("lock_status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accounts;
    }

    public static void updateAccount(Account account) {
        String query = "UPDATE account SET full_name=?, email=?, password=?, type=?, lock_status=? WHERE id=?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, account.getName());
            stmt.setString(2, account.getEmail());
            stmt.setString(3, account.getPassword());
            stmt.setInt(4, account.getType());
            stmt.setBoolean(5, account.isLocked());
            stmt.setInt(6, account.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteAccount(int id) {
        String query = "DELETE FROM account WHERE id=?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
