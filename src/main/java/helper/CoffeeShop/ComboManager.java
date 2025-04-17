package helper.CoffeeShop;

import database.ComboDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.CoffeeShop.Coffee;
import model.CoffeeShop.ComboProduct;

/**
 * Lớp quản lý combo sản phẩm, cung cấp các phương thức để tương tác với combo
 * và tích hợp vào hệ thống bán hàng
 */
public class ComboManager {
    private static ComboManager instance;
    private ComboDAO comboDAO;
    private ObservableList<ComboProduct> activeCombos;
    
    private ComboManager() {
        comboDAO = new ComboDAO();
        activeCombos = FXCollections.observableArrayList();
        loadActiveCombos();
    }
    
    public static ComboManager getInstance() {
        if (instance == null) {
            instance = new ComboManager();
        }
        return instance;
    }
    
    /**
     * Tải danh sách combo đang active
     */
    public void loadActiveCombos() {
        activeCombos.clear();
        activeCombos.addAll(comboDAO.getActiveCombos());
    }
    
    /**
     * Lấy danh sách combo đang active
     * @return Danh sách combo active
     */
    public ObservableList<ComboProduct> getActiveCombos() {
        return activeCombos;
    }
    
    /**
     * Thêm combo vào giỏ hàng
     * @param combo Combo cần thêm
     * @param cartManager Đối tượng quản lý giỏ hàng
     */
    public void addComboToCart(ComboProduct combo, CartManager cartManager) {
        // Thêm từng sản phẩm trong combo vào giỏ hàng với giá đã giảm
        for (Coffee product : combo.getProducts()) {
            // Tính giá đã giảm cho từng sản phẩm
            double discountedPrice = calculateDiscountedPrice(product.getPrice(), combo.getDiscountPercent());
            
            // Thêm vào giỏ hàng với ghi chú là thuộc combo nào
            cartManager.addToCart(
                product.getId(),
                product.getName() + " (Combo: " + combo.getName() + ")",
                "Regular", // Size mặc định
                1, // Số lượng
                discountedPrice // Giá đã giảm
            );
        }
    }
    
    /**
     * Tính giá đã giảm cho một sản phẩm
     * @param originalPrice Giá gốc
     * @param discountPercent Phần trăm giảm giá
     * @return Giá đã giảm
     */
    private double calculateDiscountedPrice(double originalPrice, double discountPercent) {
        return originalPrice * (1 - discountPercent / 100.0);
    }
    
    /**
     * Lấy combo theo ID
     * @param comboId ID của combo
     * @return Đối tượng combo
     */
    public ComboProduct getComboById(int comboId) {
        return comboDAO.getComboById(comboId);
    }
}