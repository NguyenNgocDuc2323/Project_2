package helper.CoffeeShop;

import helper.ConnectDatabase;
import model.Coffee;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CoffeeItemManager {
    private static CoffeeItemManager instance ;

    public static CoffeeItemManager getInstance() {
        if (instance == null) {
            instance = new CoffeeItemManager();
        }
        return instance;
    }

    private CoffeeItemManager() {
        // Private constructor for singleton pattern
    }

    public List<Coffee> getAllCoffeeItems() {
        List<Coffee> coffeeItems = new ArrayList<>();
        String query = "SELECT * FROM product";

        try (Connection conn = ConnectDatabase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int categoryId = rs.getInt("category_id");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");
                String image = rs.getString("image");
                int unitId = rs.getInt("unit_id");
                String description = rs.getString("description");

                Coffee coffee = new Coffee(id, name, categoryId, price, quantity, image, unitId, description);
                coffeeItems.add(coffee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return coffeeItems;
    }

    private Coffee mapResultSetToCoffeeItem(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        int categoryId = rs.getInt("category_id");
        double price = rs.getDouble("price");
        int quantity = rs.getInt("quantity");
        String image = rs.getString("image");
        int unitId = rs.getInt("unit_id");
        String description = rs.getString("description");

        return new Coffee(id, name, categoryId, price, quantity, image, unitId, description);
    }

}
