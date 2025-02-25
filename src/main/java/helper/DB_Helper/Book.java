package helper.DB_Helper;


import helper.ConnectDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Book {
    public static model.Book getAllBooks(){
        try (Connection conn = ConnectDatabase.getConnection()){
            PreparedStatement ps = conn.prepareStatement("select * from books");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                boolean is_borrowed = rs.getBoolean("is_borrowed");
                return new model.Book(id,name,price,is_borrowed);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
