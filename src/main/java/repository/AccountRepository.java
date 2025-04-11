package repository;

import helper.DatabaseConnection;
import model.Account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountRepository {
    private static AccountRepository instance;
    private final Connection connection;
    
    private AccountRepository() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    public static AccountRepository getInstance() {
        if (instance == null) {
            instance = new AccountRepository();
        }
        return instance;
    }
    
    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        String query = "SELECT * FROM account";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Account account = new Account(
                    rs.getInt("id"),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getInt("type"),
                    rs.getBoolean("lock_status")
                );
                accounts.add(account);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi lấy danh sách tài khoản: " + e.getMessage());
        }
        
        return accounts;
    }
    
    public boolean addAccount(Account account) {
        String query = "INSERT INTO accounts (name, email, password, type, is_locked) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, account.getName());
            stmt.setString(2, account.getEmail());
            stmt.setString(3, account.getPassword());
            stmt.setInt(4, account.getType());
            stmt.setBoolean(5, account.isLocked());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi thêm tài khoản: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateAccount(Account account) {
        String query = "UPDATE accounts SET name = ?, email = ?, password = ?, type = ?, is_locked = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, account.getName());
            stmt.setString(2, account.getEmail());
            stmt.setString(3, account.getPassword());
            stmt.setInt(4, account.getType());
            stmt.setBoolean(5, account.isLocked());
            stmt.setInt(6, account.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi cập nhật tài khoản: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteAccount(int id) {
        String query = "DELETE FROM accounts WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi xóa tài khoản: " + e.getMessage());
            return false;
        }
    }
}