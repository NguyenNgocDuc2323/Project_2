package com.example.coffeeshop.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDatabase {
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost/cafe_management";
        String user = "root";
        String password = "";
        Connection conn = DriverManager.getConnection(url,user,password);
        return conn;
    }
}
