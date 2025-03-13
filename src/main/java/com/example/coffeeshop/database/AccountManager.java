package com.example.coffeeshop.database;

import com.example.coffeeshop.model.Account;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        accounts.add(account);
    }
}
