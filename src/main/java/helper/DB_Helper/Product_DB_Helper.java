package helper.DB_Helper;

import helper.ConnectDatabase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Product_DB_Helper {
    public static int countProducts() {
        int count = 0;
        String query = "SELECT COUNT(*) FROM product";
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
}
