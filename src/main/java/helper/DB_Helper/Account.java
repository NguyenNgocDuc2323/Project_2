package helper.DB_Helper;

import helper.ConnectDatabase;

import java.sql.*;

public class Account {
    public static model.Account getAccountByEmailAndPassword(String email, String password) {
        try(Connection conn = ConnectDatabase.getConnection();) {
            PreparedStatement ps = conn.prepareStatement("select * from account where email = ? and password = ?");
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("full_name");
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
            try (PreparedStatement checkPs = conn.prepareStatement(checkQuery)) {
                checkPs.setString(1, account.getEmail());
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Account with email " + account.getEmail() + " already exists.");
                        return;
                    }
                }
            }

            String insertQuery = "INSERT INTO account (email, password, full_name, type, lock_status) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
                ps.setString(1, account.getEmail());
                ps.setString(2, account.getPassword());
                String fullName = account.getName();
                if (fullName == null || fullName.trim().isEmpty()) {
                    fullName = "Unknown";
                }
                ps.setString(3, fullName);

                ps.setInt(4, account.getType());
                ps.setBoolean(5, account.isLocked());

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Account successfully added: " + account.getEmail());
                } else {
                    System.out.println("Failed to add the account.");
                }
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
}
