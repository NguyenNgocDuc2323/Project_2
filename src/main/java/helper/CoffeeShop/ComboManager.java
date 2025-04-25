package helper.CoffeeShop;

import helper.ConnectDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.CoffeeShop.Coffee;
import model.CoffeeShop.ComboProduct;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComboManager {
    private static ComboManager instance;
    private final ObservableList<ComboProduct> comboProducts;

    private ComboManager() {
        comboProducts = FXCollections.observableArrayList();
        loadComboProducts();
    }

    public static ComboManager getInstance() {
        if (instance == null) {
            instance = new ComboManager();
        }
        return instance;
    }

    private void loadComboProducts() {
        String sql = "SELECT * FROM combos WHERE status = 1";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ComboProduct combo = new ComboProduct(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("discount_percent"),
                        rs.getInt("status")
                );
                loadComboItems(combo);
                comboProducts.add(combo);
            }
        } catch (SQLException e) {
            System.err.println("Error loading combo products: " + e.getMessage());
        }
    }

    private void loadComboItems(ComboProduct combo) {
        String sql = """
        SELECT p.* 
        FROM product p
        JOIN combo_products cp ON p.id = cp.product_id
        WHERE cp.combo_id = ?
    """;

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, combo.getId());
            ResultSet rs = pstmt.executeQuery();

            List<Coffee> items = new ArrayList<>();
            double totalPrice = 0.0;

            System.out.println("\nLoading items for combo: " + combo.getName());

            while (rs.next()) {
                Coffee coffee = new Coffee();
                coffee.setId(rs.getInt("id"));
                coffee.setName(rs.getString("name"));
                coffee.setDescription(rs.getString("description"));
                coffee.setPrice(rs.getDouble("price"));
                coffee.setCategoryId(rs.getInt("category_id"));
                coffee.setQuantity(rs.getInt("quantity"));
                coffee.setUnitId(rs.getInt("unit_id"));
                coffee.setStatus(rs.getInt("status"));

                totalPrice += coffee.getPrice();
                items.add(coffee);

                System.out.println("Added product: " + coffee.getName() +
                        " with price: $" + coffee.getPrice());
            }

            System.out.println("Total items in combo: " + items.size());
            System.out.println("Total price before discount: $" + totalPrice);

            // Set products first to trigger price recalculation
            combo.setProducts(items);

            // Validate prices to ensure they're correct
            combo.validatePrices();

            System.out.println("Final price after " + combo.getDiscountPercent() +
                    "% discount: $" + combo.getFinalPrice());

        } catch (SQLException e) {
            System.err.println("Error loading combo items: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public ObservableList<ComboProduct> getAllComboProducts() {
        return comboProducts;
    }

    public ObservableList<ComboProduct> getActiveCombos() {
        ObservableList<ComboProduct> activeCombos = FXCollections.observableArrayList();
        String sql = "SELECT * FROM combos WHERE status = 1";

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ComboProduct combo = new ComboProduct(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("discount_percent"),
                        rs.getInt("status")
                );
                loadComboItems(combo);
                activeCombos.add(combo);
            }
        } catch (SQLException e) {
            System.err.println("Error loading active combos: " + e.getMessage());
        }
        return activeCombos;
    }

    public ObservableList<ComboProduct> getInactiveCombos() {
        ObservableList<ComboProduct> inactiveCombos = FXCollections.observableArrayList();
        String sql = "SELECT * FROM combos WHERE status = 0";

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ComboProduct combo = new ComboProduct(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("discount_percent"),
                        rs.getInt("status")
                );
                loadComboItems(combo);
                inactiveCombos.add(combo);
            }
        } catch (SQLException e) {
            System.err.println("Error loading inactive combos: " + e.getMessage());
        }
        return inactiveCombos;
    }

    public boolean addComboProduct(ComboProduct combo) {
        String sql = """
            INSERT INTO combos (name, description, discount_percent, status)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, combo.getName());
            pstmt.setString(2, combo.getDescription());
            pstmt.setDouble(3, combo.getDiscountPercent());
            pstmt.setInt(4, combo.getStatus());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    combo.setId(generatedKeys.getInt(1));
                    saveComboItems(combo);
                    comboProducts.add(combo);
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding combo: " + e.getMessage());
        }
        return false;
    }

    private void saveComboItems(ComboProduct combo) {
        String sql = "INSERT INTO combo_products (combo_id, coffee_id) VALUES (?, ?)";

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (Coffee coffee : combo.getProducts()) {
                pstmt.setInt(1, combo.getId());
                pstmt.setInt(2, coffee.getId());
                pstmt.addBatch();
            }
            pstmt.executeBatch();

        } catch (SQLException e) {
            System.err.println("Error saving combo items: " + e.getMessage());
        }
    }

    public boolean updateComboProduct(ComboProduct combo) {
        String sql = """
            UPDATE combos 
            SET name = ?, description = ?, discount_percent = ?, status = ?
            WHERE id = ?
        """;

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, combo.getName());
            pstmt.setString(2, combo.getDescription());
            pstmt.setDouble(3, combo.getDiscountPercent());
            pstmt.setInt(4, combo.getStatus());
            pstmt.setInt(5, combo.getId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                updateComboItems(combo);
                refreshComboProducts();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error updating combo: " + e.getMessage());
        }
        return false;
    }

    private void updateComboItems(ComboProduct combo) {
        String deleteSql = "DELETE FROM combo_products WHERE combo_id = ?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {

            pstmt.setInt(1, combo.getId());
            pstmt.executeUpdate();

            saveComboItems(combo);

        } catch (SQLException e) {
            System.err.println("Error updating combo items: " + e.getMessage());
        }
    }

    public boolean deleteComboProduct(int comboId) {
        String deleteItemsSql = "DELETE FROM combo_products WHERE combo_id = ?";
        String deleteComboSql = "DELETE FROM combos WHERE id = ?";

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement itemsStmt = conn.prepareStatement(deleteItemsSql);
             PreparedStatement comboStmt = conn.prepareStatement(deleteComboSql)) {

            itemsStmt.setInt(1, comboId);
            itemsStmt.executeUpdate();

            comboStmt.setInt(1, comboId);
            int affectedRows = comboStmt.executeUpdate();

            if (affectedRows > 0) {
                comboProducts.removeIf(combo -> combo.getId() == comboId);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting combo: " + e.getMessage());
        }
        return false;
    }

    public void toggleComboStatus(ComboProduct combo) {
        String sql = "UPDATE combos SET status = ? WHERE id = ?";

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int newStatus = combo.getStatus() == 1 ? 0 : 1;
            pstmt.setInt(1, newStatus);
            pstmt.setInt(2, combo.getId());

            if (pstmt.executeUpdate() > 0) {
                combo.setStatus(newStatus);
                refreshComboProducts();
            }
        } catch (SQLException e) {
            System.err.println("Error toggling combo status: " + e.getMessage());
        }
    }

    public void updateComboStatus(int comboId, int status) {
        String sql = "UPDATE combos SET status = ? WHERE id = ?";

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, status);
            pstmt.setInt(2, comboId);

            if (pstmt.executeUpdate() > 0) {
                refreshComboProducts();
            }
        } catch (SQLException e) {
            System.err.println("Error updating combo status: " + e.getMessage());
        }
    }

    public void filterCombos(String searchText, boolean showActive) {
        comboProducts.clear();
        String sql = "SELECT * FROM combos WHERE status = ? AND " +
                "(LOWER(name) LIKE ? OR LOWER(description) LIKE ?)";

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, showActive ? 1 : 0);
            String searchPattern = "%" + searchText.toLowerCase() + "%";
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ComboProduct combo = new ComboProduct(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("discount_percent"),
                        rs.getInt("status")
                );
                loadComboItems(combo);
                comboProducts.add(combo);
            }
        } catch (SQLException e) {
            System.err.println("Error filtering combos: " + e.getMessage());
        }
    }

    public ComboProduct getComboById(int id) {
        String sql = "SELECT * FROM combos WHERE id = ?";

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                ComboProduct combo = new ComboProduct(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("discount_percent"),
                        rs.getInt("status")
                );
                loadComboItems(combo);
                return combo;
            }
        } catch (SQLException e) {
            System.err.println("Error getting combo by id: " + e.getMessage());
        }
        return null;
    }

    public void refreshComboProducts() {
        comboProducts.clear();
        loadComboProducts();
    }
}