package helper;

import model.Account;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountManager {
    private List<Account> accounts = new ArrayList<>();
    private static AccountManager accountManager;

    private AccountManager() {
        loadAccountsFromDatabase();
    }

    public static AccountManager getInstance() {
        if (accountManager == null) {
            accountManager = new AccountManager();
        }
        return accountManager;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    private void loadAccountsFromDatabase() {
        String query = "SELECT * FROM account";
        try (Connection conn = ConnectDatabase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            accounts.clear();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String password = rs.getString("password");
                int type = rs.getInt("type");
                boolean isLocked = rs.getInt("lock_status") == 1;
                accounts.add(new Account(id,name, email, password, type, isLocked));
            }

            System.out.println("Tải dữ liệu thành công. Số tài khoản: " + accounts.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void refreshAccounts() {
        loadAccountsFromDatabase();
    }

    public void addAccount(Account account) {
        String query = "INSERT INTO account (id, name, email, password, type, lock_status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, account.getId());
            pstmt.setString(2, account.getName());
            pstmt.setString(3, account.getEmail());
            pstmt.setString(4, account.getPassword());
            pstmt.setInt(5, account.getType());
            pstmt.setInt(6, account.isLocked() ? 1 : 0);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                accounts.add(account);
                System.out.println("Account added successfully to database");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to add account to database: " + e.getMessage());
        }
    }

    public Account getAccountByEmail(String email) {
        return accounts.stream()
                .filter(account -> account.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    public int getNextId() {
        return accounts.stream()
                .mapToInt(Account::getId)
                .max()
                .orElse(0) + 1;
    }
}
