package service;

import database.ComboDAO;
import javafx.collections.ObservableList;
import model.CoffeeShop.Coffee;
import model.CoffeeShop.ComboProduct;

import java.util.List;

public class ComboService {
    private ComboDAO comboDAO;
    
    public ComboService() {
        comboDAO = new ComboDAO();
    }
    
    /**
     * Lấy tất cả combo sản phẩm
     * @return Danh sách combo
     */
    public ObservableList<ComboProduct> getAllCombos() {
        return comboDAO.getAllCombos();
    }
    
    /**
     * Lấy danh sách combo đang active
     * @return Danh sách combo active
     */
    public ObservableList<ComboProduct> getActiveCombos() {
        return comboDAO.getActiveCombos();
    }
    
    /**
     * Thêm combo mới
     * @param combo Combo cần thêm
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean addCombo(ComboProduct combo) {
        if (!validateCombo(combo)) {
            return false;
        }
        return comboDAO.addCombo(combo);
    }
    
    /**
     * Cập nhật combo
     * @param combo Combo cần cập nhật
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean updateCombo(ComboProduct combo) {
        if (!validateCombo(combo)) {
            return false;
        }
        return comboDAO.updateCombo(combo);
    }
    
    /**
     * Xóa combo
     * @param comboId ID của combo cần xóa
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean deleteCombo(int comboId) {
        return comboDAO.deleteCombo(comboId);
    }
    
    /**
     * Lấy combo theo ID
     * @param comboId ID của combo
     * @return Đối tượng combo
     */
    public ComboProduct getComboById(int comboId) {
        return comboDAO.getComboById(comboId);
    }
    
    /**
     * Thêm sản phẩm vào combo
     * @param combo Combo cần thêm sản phẩm
     * @param product Sản phẩm cần thêm
     */
    public void addProductToCombo(ComboProduct combo, Coffee product) {
        combo.addProduct(product);
    }
    
    /**
     * Xóa sản phẩm khỏi combo
     * @param combo Combo cần xóa sản phẩm
     * @param product Sản phẩm cần xóa
     */
    public void removeProductFromCombo(ComboProduct combo, Coffee product) {
        combo.removeProduct(product);
    }
    
    /**
     * Validate combo
     * @param combo Combo cần validate
     * @return true nếu hợp lệ, false nếu không hợp lệ
     */
    private boolean validateCombo(ComboProduct combo) {
        // Kiểm tra tên combo
        if (combo.getName() == null || combo.getName().trim().isEmpty()) {
            return false;
        }
        
        // Kiểm tra phần trăm giảm giá (5-15%)
        double discountPercent = combo.getDiscountPercent();
        if (discountPercent < 5.0 || discountPercent > 15.0) {
            return false;
        }
        
        // Kiểm tra danh sách sản phẩm (ít nhất 2 sản phẩm)
        List<Coffee> products = combo.getProducts();
        if (products == null || products.size() < 2) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Thêm combo vào giỏ hàng
     * @param combo Combo cần thêm vào giỏ hàng
     * @param cartManager Đối tượng quản lý giỏ hàng
     */
    public void addComboToCart(ComboProduct combo, helper.CoffeeShop.CartManager cartManager) {
        // Thêm combo vào giỏ hàng với giá đã giảm
        for (Coffee product : combo.getProducts()) {
            // Thêm từng sản phẩm trong combo vào giỏ hàng
            // Sử dụng giá đã giảm cho từng sản phẩm
            double discountedPrice = calculateDiscountedPrice(product.getPrice(), combo.getDiscountPercent());
            cartManager.addToCart(
                product.getId(),
                product.getName() + " (Combo: " + combo.getName() + ")",
                "Regular", // Giả sử size mặc định
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
}