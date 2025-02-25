package helper;

import model.Book;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BookManager {
    private List<Book> books = new ArrayList<Book>();
    private static BookManager bookManager;
    private BookManager() {}
    public static BookManager getInstance() {
        if (bookManager == null) {
            bookManager = new BookManager();
        }
        return bookManager;
    }
    public List<Book> getBooks() {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM books";

        try (Connection conn = ConnectDatabase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                boolean isBorrowed = rs.getBoolean("is_borrowed");
                books.add(new Book(id, name, price, isBorrowed));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
}
