package database;

import helper.Alert;
import helper.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.CoffeeShop.Coffee;
import model.CoffeeShop.ComboProduct;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComboDAO {
    private final CoffeeDAO coffeeDAO;

    public ComboDAO() {
        coffeeDAO = new CoffeeDAO();
    }

    /**
     * Lấy tất cả combo sản phẩm
     * @return Danh sách combo
     */
    public ObservableList<ComboProduct> getAllCombos() {
        ObservableList<ComboProduct> combos = FXCollections.observableArrayList();
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM combos")) {

                while (rs.next()) {
                    int comboId = rs.getInt("id");
                    ComboProduct combo = new ComboProduct(
                            comboId,
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDouble("discount_percent"),
                            rs.getInt("status")
                    );

                    // Sử dụng cùng connection để lấy sản phẩm
                    List<Coffee> products = getComboProducts(conn, comboId);
                    combo.setProducts(products);

                    // Tính giá
                    double originalPrice = calculateOriginalPrice(products);
                    combo.setOriginalPrice(originalPrice);
                    combo.setFinalPrice(calculateFinalPrice(originalPrice, combo.getDiscountPercent()));

                    combos.add(combo);
                }
            }
        } catch (SQLException e) {
            Alert.showAlert("Lỗi khi lấy danh sách combo: " + e.getMessage());
            e.printStackTrace();
        }
        return combos;
    }

    private ComboProduct extractComboFromResultSet(Connection conn, ResultSet rs) throws SQLException {
        int comboId = rs.getInt("id");
        ComboProduct combo = new ComboProduct(
                comboId,
                rs.getString("name"),
                rs.getString("description"),
                rs.getDouble("discount_percent"),
                rs.getInt("status")
        );

        // Sử dụng connection được truyền vào
        List<Coffee> products = getComboProducts(conn, comboId);
        combo.setProducts(products);

        // Calculate and set prices
        double originalPrice = calculateOriginalPrice(products);
        combo.setOriginalPrice(originalPrice);
        combo.setFinalPrice(calculateFinalPrice(originalPrice, combo.getDiscountPercent()));

        return combo;
    }

    private double calculateOriginalPrice(List<Coffee> products) {
        return products.stream()
                .mapToDouble(Coffee::getPrice)
                .sum();
    }

    private double calculateFinalPrice(double originalPrice, double discountPercent) {
        return originalPrice * (1 - discountPercent / 100.0);
    }

    /**
     * Lấy danh sách sản phẩm trong combo sử dụng connection hiện có
     */
    private List<Coffee> getComboProducts(Connection conn, int comboId) throws SQLException {
        List<Coffee> products = new ArrayList<>();
        String query = """
            SELECT p.* FROM product p
            JOIN combo_products cp ON p.id = cp.product_id
            WHERE cp.combo_id = ?
            ORDER BY p.name
        """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, comboId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Coffee coffee = new Coffee(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("category_id"),
                            rs.getDouble("price"),
                            0,
                            rs.getString("image"),
                            rs.getInt("unit_id"),
                            rs.getString("description")
                    );
                    coffee.setStatus(rs.getInt("status"));
                    products.add(coffee);
                }
            }
        }

        return products;
    }

    /**
     * Lấy danh sách sản phẩm trong combo
     */
    public List<Coffee> getComboProducts(int comboId) {
        List<Coffee> products = new ArrayList<>();
        String query = """
            SELECT p.* FROM product p
            JOIN combo_products cp ON p.id = cp.product_id
            WHERE cp.combo_id = ?
            ORDER BY p.name
        """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, comboId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Coffee coffee = new Coffee(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("category_id"),
                            rs.getDouble("price"),
                            0,
                            rs.getString("image"),
                            rs.getInt("unit_id"),
                            rs.getString("description")
                    );
                    coffee.setStatus(rs.getInt("status"));
                    products.add(coffee);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting combo products: " + e.getMessage());
            e.printStackTrace();
        }

        return products;
    }

    /**
     * Thêm combo mới
     */
    public boolean addCombo(ComboProduct combo) {
        String insertComboQuery = """
            INSERT INTO combos (name, description, original_price, 
                              discount_percent, final_price, status)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(insertComboQuery,
                    Statement.RETURN_GENERATED_KEYS)) {

                stmt.setString(1, combo.getName());
                stmt.setString(2, combo.getDescription());
                stmt.setDouble(3, combo.getOriginalPrice());
                stmt.setDouble(4, combo.getDiscountPercent());
                stmt.setDouble(5, combo.getFinalPrice());
                stmt.setInt(6, combo.getStatus());

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    conn.rollback();
                    return false;
                }

                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int comboId = generatedKeys.getInt(1);
                    combo.setId(comboId);

                    if (!addProductsToCombo(conn, comboId, combo.getProducts())) {
                        conn.rollback();
                        return false;
                    }
                } else {
                    conn.rollback();
                    return false;
                }

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.err.println("Error adding combo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Thêm sản phẩm vào combo
     */
    private boolean addProductsToCombo(Connection conn, int comboId, List<Coffee> products)
            throws SQLException {
        String insertProductQuery =
                "INSERT INTO combo_products (combo_id, product_id) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(insertProductQuery)) {
            for (Coffee product : products) {
                stmt.setInt(1, comboId);
                stmt.setInt(2, product.getId());
                stmt.addBatch();
            }

            int[] results = stmt.executeBatch();
            for (int result : results) {
                if (result <= 0) return false;
            }
            return true;
        }
    }

    /**
     * Cập nhật combo
     */
    public boolean updateCombo(ComboProduct combo) {
        String updateComboQuery = """
            UPDATE combos 
            SET name = ?, description = ?, original_price = ?,
                discount_percent = ?, final_price = ?, status = ?
            WHERE id = ?
        """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(updateComboQuery)) {
                stmt.setString(1, combo.getName());
                stmt.setString(2, combo.getDescription());
                stmt.setDouble(3, combo.getOriginalPrice());
                stmt.setDouble(4, combo.getDiscountPercent());
                stmt.setDouble(5, combo.getFinalPrice());
                stmt.setInt(6, combo.getStatus());
                stmt.setInt(7, combo.getId());

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    conn.rollback();
                    return false;
                }

                // Update products
                if (!updateComboProducts(conn, combo)) {
                    conn.rollback();
                    return false;
                }

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.err.println("Error updating combo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private boolean updateComboProducts(Connection conn, ComboProduct combo) throws SQLException {
        // Delete existing products
        String deleteQuery = "DELETE FROM combo_products WHERE combo_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
            stmt.setInt(1, combo.getId());
            stmt.executeUpdate();
        }

        // Add new products
        return addProductsToCombo(conn, combo.getId(), combo.getProducts());
    }

    /**
     * Xóa combo
     */
    public boolean deleteCombo(int comboId) {
        String query = "DELETE FROM combos WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, comboId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting combo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy combo theo ID
     */
    public ComboProduct getComboById(int comboId) {
        String query = "SELECT * FROM combos WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, comboId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractComboFromResultSet(conn, rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting combo by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Lấy danh sách combo đang active
     */
    public ObservableList<ComboProduct> getActiveCombos() {
        ObservableList<ComboProduct> comboList = FXCollections.observableArrayList();
        String query = """
            SELECT DISTINCT c.*, GROUP_CONCAT(cp.product_id) as product_ids 
            FROM combos c
            LEFT JOIN combo_products cp ON c.id = cp.combo_id
            WHERE c.status = 1
            GROUP BY c.id, c.name, c.description, c.original_price, 
                     c.discount_percent, c.final_price, c.status
        """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {

            try (ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    ComboProduct combo = extractComboFromResultSet(conn, rs);
                    if (combo != null) {
                        comboList.add(combo);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting active combos: " + e.getMessage());
            e.printStackTrace();
            Alert.showAlert("Error loading active combos: " + e.getMessage());
        }

        return comboList;
    }
}