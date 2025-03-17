package com.example.coffeeshop.database;

import com.example.coffeeshop.helper.Alert;
import com.example.coffeeshop.model.Table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TableDB {
    private static TableDB instance;

    public TableDB() {
    }

    public static TableDB getInstance() {
        if (instance == null) {
            instance = new TableDB();
        }
        return instance;
    }

    public Table getTableById(int id) {
        String sql = "SELECT * FROM `tables` WHERE id = ?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Table(
                        rs.getInt("id"),
                        rs.getString("table_name"),
                        rs.getInt("capacity"),
                        rs.getString("status"),
                        rs.getInt("floor_number")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error reading table: " + e.getMessage());
        }
        return null;
    }

    public List<Table> getAllTables() {
        List<Table> tables = new ArrayList<>();
        String sql = "SELECT * FROM `tables`";
        try (Connection conn = ConnectDatabase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tables.add(new Table(
                        rs.getInt("id"),
                        rs.getString("table_name"),
                        rs.getInt("capacity"),
                        rs.getString("status"),
                        rs.getInt("floor_number")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error reading all tables: " + e.getMessage());
        }
        return tables;
    }

    public void createTable(Table table) {
        String sql = "INSERT INTO `tables` (table_name, capacity, status, floor_number) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, table.getTableName());
            ps.setInt(2, table.getCapacity());
            ps.setString(3, table.getStatus());
            ps.setInt(4, table.getFloorNumber());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Table created successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
        }
    }

    public void updateTable(Integer id, String tableName, Integer capacity, String status, Integer floorNumber) {
        String sql = "UPDATE `tables` SET table_name = ?, capacity = ?, status = ?, floor_number = ? WHERE id = ?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tableName);
            ps.setInt(2, capacity);
            ps.setString(3, status);
            ps.setInt(4, floorNumber);
            ps.setInt(5, id);
            int rowsAffected = ps.executeUpdate();
            System.out.println(rowsAffected + " row(s) updated.");
        } catch (SQLException e) {
            System.err.println("Error updating table: " + e.getMessage());
            Alert.showAlert("Error updating table");
        }
    }

    public void deleteTable(int id) {
        String sql = "DELETE FROM `tables` WHERE id = ?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            System.out.println(rowsAffected + " row(s) deleted.");
        } catch (SQLException e) {
            System.err.println("Error deleting table: " + e.getMessage());
            Alert.showAlert("Error deleting table");
        }
    }
}
