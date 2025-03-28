package database;

import helper.Alert;
import helper.ConnectDatabase;
import model.Table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public List<Table> getAllTables() {
        List<Table> tableList = new ArrayList<>();
        String query = "SELECT * FROM `tables`";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tableList.add(new Table(
                        rs.getInt("id"),
                        rs.getString("table_name"),
                        rs.getInt("capacity"),
                        rs.getString("status"),
                        rs.getInt("floor_number")
                ));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return tableList;
    }

    public List<String> getAllTablesName() {
        List<String> tableList = new ArrayList<>();
        String query = "SELECT table_name FROM `tables`";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tableList.add(rs.getString("table_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tableList;
    }

    public Table getTableById(int id) {
        String query = "SELECT * FROM `tables` WHERE id = ?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Table(
                            rs.getInt("id"),
                            rs.getString("table_name"),
                            rs.getInt("capacity"),
                            rs.getString("status"),
                            rs.getInt("floor_number")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Table getTableByName(String name) {
        String query = "SELECT * FROM `tables` WHERE table_name = ?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Table(
                            rs.getInt("id"),
                            rs.getString("table_name"),
                            rs.getInt("capacity"),
                            rs.getString("status"),
                            rs.getInt("floor_number")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTableNameById(int id) {
        String query = "SELECT table_name FROM `tables` WHERE id = ?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("table_name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void createTable(Table table) {
        String query = "INSERT INTO `tables` (table_name, capacity, status, floor_number) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, table.getTableName());
            ps.setInt(2, table.getCapacity());
            ps.setString(3, table.getStatus());
            ps.setInt(4, table.getFloorNumber());
            if (ps.executeUpdate() > 0) {
                Alert.showSuccess("Table created successfully");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTable(Table table) {
        String query = "UPDATE `tables` SET table_name = ?, capacity = ?, status = ?, floor_number = ? WHERE id = ?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, table.getTableName());
            ps.setInt(2, table.getCapacity());
            ps.setString(3, table.getStatus());
            ps.setInt(4, table.getFloorNumber());
            ps.setInt(5, table.getId());
            if (ps.executeUpdate() > 0) {
                Alert.showSuccess("Table updated successfully");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTable(int id) {
        String query = "DELETE FROM `tables` WHERE id = ?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() > 0) {
                Alert.showSuccess("Table deleted successfully");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
