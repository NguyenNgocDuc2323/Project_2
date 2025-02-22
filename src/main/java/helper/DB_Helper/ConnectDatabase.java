package helper.DB_Helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDatabase {
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost/java_lession";
        String user = "root";
        String password = "";
        Connection conn = DriverManager.getConnection(url,user,password);
        return conn;
    }
}
