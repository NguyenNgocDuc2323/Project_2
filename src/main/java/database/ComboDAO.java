package database;

import helper.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.CoffeeShop.Coffee;
import model.CoffeeShop.ComboProduct;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComboDAO {

    private CoffeeDAO coffeeDAO;

    public ComboDAO() {
        coffeeDAO = new CoffeeDAO();
    }

    /**
     * Lấy tất cả combo sản phẩm
     * @return Danh sách combo
     */
    public ObservableList<ComboProduct> getAllCombos() {
        ObservableList<ComboProduct> comboList = FXCollections.observableArrayList();
        String query = "SELECT * FROM combos";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                ComboProduct combo = new ComboProduct(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("discount_percent"),
                        rs.getInt("status")
                );
                
                // Lấy danh sách sản phẩm trong combo
                List<Coffee> products = getComboProducts(combo.getId());
                combo.setProducts(products);
                
                comboList.add(combo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return comboList;
    }

    /**
     * Lấy danh sách sản phẩm trong combo
     * @param comboId ID của combo
     * @return Danh sách sản phẩm
     */
    public List<Coffee> getComboProducts(int comboId) {
        List<Coffee> products = new ArrayList<>();
        String query = "SELECT p.* FROM product p " +
                "JOIN combo_products cp ON p.id = cp.product_id " +
                "WHERE cp.combo_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, comboId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Coffee coffee = new Coffee(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("category_id"),
                        rs.getDouble("price"),
                        0, // quantity không còn được sử dụng
                        rs.getString("image"),
                        rs.getInt("unit_id"),
                        rs.getString("description")
                );
                coffee.setStatus(rs.getInt("status"));
                products.add(coffee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    /**
     * Thêm combo mới
     * @param combo Combo cần thêm
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean addCombo(ComboProduct combo) {
        String insertComboQuery = "INSERT INTO combos (name, description, original_price, discount_percent, final_price, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false); // Bắt đầu transaction

            try (PreparedStatement stmt = conn.prepareStatement(insertComboQuery, Statement.RETURN_GENERATED_KEYS)) {
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

                // Lấy ID của combo vừa thêm
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int comboId = generatedKeys.getInt(1);
                    combo.setId(comboId);

                    // Thêm các sản phẩm vào combo
                    if (!addProductsToCombo(conn, comboId, combo.getProducts())) {
                        conn.rollback();
                        return false;
                    }
                } else {
                    conn.rollback();
                    return false;
                }

                conn.commit(); // Hoàn tất transaction
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Thêm các sản phẩm vào combo
     * @param conn Connection đang sử dụng
     * @param comboId ID của combo
     * @param products Danh sách sản phẩm
     * @return true nếu thành công, false nếu thất bại
     */
    private boolean addProductsToCombo(Connection conn, int comboId, List<Coffee> products) throws SQLException {
        String insertProductQuery = "INSERT INTO combo_products (combo_id, product_id) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(insertProductQuery)) {
            for (Coffee product : products) {
                stmt.setInt(1, comboId);
                stmt.setInt(2, product.getId());
                stmt.addBatch();
            }

            int[] results = stmt.executeBatch();
            for (int result : results) {
                if (result <= 0) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Cập nhật combo
     * @param combo Combo cần cập nhật
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean updateCombo(ComboProduct combo) {
        String updateComboQuery = "UPDATE combos SET name = ?, description = ?, original_price = ?, " +
                "discount_percent = ?, final_price = ?, status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false); // Bắt đầu transaction

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

                // Xóa các sản phẩm cũ trong combo
                String deleteProductsQuery = "DELETE FROM combo_products WHERE combo_id = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteProductsQuery)) {
                    deleteStmt.setInt(1, combo.getId());
                    deleteStmt.executeUpdate();
                }

                // Thêm lại các sản phẩm mới
                if (!addProductsToCombo(conn, combo.getId(), combo.getProducts())) {
                    conn.rollback();
                    return false;
                }

                conn.commit(); // Hoàn tất transaction
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa combo
     * @param comboId ID của combo cần xóa
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean deleteCombo(int comboId) {
        String query = "DELETE FROM combos WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, comboId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy combo theo ID
     * @param comboId ID của combo
     * @return Đối tượng combo
     */
    public ComboProduct getComboById(int comboId) {
        String query = "SELECT * FROM combos WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, comboId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                ComboProduct combo = new ComboProduct(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("discount_percent"),
                        rs.getInt("status")
                );
                
                // Lấy danh sách sản phẩm trong combo
                List<Coffee> products = getComboProducts(combo.getId());
                combo.setProducts(products);
                
                return combo;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    
    /**
     * Lấy danh sách combo đang active
     * @return Danh sách combo active
     */
    public ObservableList<ComboProduct> getActiveCombos() {
        ObservableList<ComboProduct> comboList = FXCollections.observableArrayList();
        String query = "SELECT * FROM combos WHERE status = 1";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                ComboProduct combo = new ComboProduct(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("discount_percent"),
                        rs.getInt("status")
                );
                
                // Lấy danh sách sản phẩm trong combo
                List<Coffee> products = getComboProducts(combo.getId());
                combo.setProducts(products);
                
                comboList.add(combo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return comboList;
    }
}