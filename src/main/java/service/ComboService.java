package service;

import database.ComboDAO;
import helper.Alert;
import javafx.collections.ObservableList;
import model.CoffeeShop.Coffee;
import model.CoffeeShop.ComboProduct;

import java.util.List;

public class ComboService {
    private final ComboDAO comboDAO;
    
    public ComboService() {
        comboDAO = new ComboDAO();
    }
    
    /**
     * Lấy tất cả combo sản phẩm
     * @return Danh sách combo
     */
    public ObservableList<ComboProduct> getAllCombos() {
        try {
            ObservableList<ComboProduct> combos = comboDAO.getAllCombos();
            System.out.println("\nLoaded " + combos.size() + " combos from database");
            return combos;
        } catch (Exception e) {
            System.err.println("Error getting all combos: " + e.getMessage());
            e.printStackTrace();
            Alert.showAlert("Error loading combos: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Lấy danh sách combo đang active
     * @return Danh sách combo active
     */
    public ObservableList<ComboProduct> getActiveCombos() {
        try {
            ObservableList<ComboProduct> activeCombos = comboDAO.getActiveCombos();
            System.out.println("\nLoaded " + activeCombos.size() + " active combos");
            return activeCombos;
        } catch (Exception e) {
            System.err.println("Error getting active combos: " + e.getMessage());
            e.printStackTrace();
            Alert.showAlert("Error loading active combos: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Thêm combo mới
     * @param combo Combo cần thêm
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean addCombo(ComboProduct combo) {
        try {
            System.out.println("\nAttempting to add new combo:");
            System.out.println("Name: " + combo.getName());
            System.out.println("Discount: " + combo.getDiscountPercent() + "%");
            System.out.println("Products count: " + combo.getProducts().size());

            if (!validateCombo(combo)) {
                System.out.println("Combo validation failed");
                return false;
            }

            boolean success = comboDAO.addCombo(combo);
            System.out.println("Add combo result: " + (success ? "Success" : "Failed"));
            return success;
        } catch (Exception e) {
            System.err.println("Error adding combo: " + e.getMessage());
            e.printStackTrace();
            Alert.showAlert("Error adding combo: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Cập nhật combo
     * @param combo Combo cần cập nhật
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean updateCombo(ComboProduct combo) {
        try {
            System.out.println("\nAttempting to update combo ID " + combo.getId());
            
            if (!validateCombo(combo)) {
                System.out.println("Combo validation failed");
                return false;
            }

            boolean success = comboDAO.updateCombo(combo);
            System.out.println("Update combo result: " + (success ? "Success" : "Failed"));
            return success;
        } catch (Exception e) {
            System.err.println("Error updating combo: " + e.getMessage());
            e.printStackTrace();
            Alert.showAlert("Error updating combo: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Xóa combo
     * @param comboId ID của combo cần xóa
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean deleteCombo(int comboId) {
        try {
            System.out.println("\nAttempting to delete combo ID " + comboId);
            boolean success = comboDAO.deleteCombo(comboId);
            System.out.println("Delete combo result: " + (success ? "Success" : "Failed"));
            return success;
        } catch (Exception e) {
            System.err.println("Error deleting combo: " + e.getMessage());
            e.printStackTrace();
            Alert.showAlert("Error deleting combo: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Lấy combo theo ID
     * @param comboId ID của combo
     * @return Đối tượng combo
     */
    public ComboProduct getComboById(int comboId) {
        try {
            System.out.println("\nGetting combo by ID: " + comboId);
            ComboProduct combo = comboDAO.getComboById(comboId);
            if (combo != null) {
                System.out.println("Found combo: " + combo.getName());
            }
            return combo;
        } catch (Exception e) {
            System.err.println("Error getting combo by ID: " + e.getMessage());
            e.printStackTrace();
            Alert.showAlert("Error getting combo: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Validate combo
     * @param combo Combo cần validate
     * @return true nếu hợp lệ, false nếu không hợp lệ
     */
    private boolean validateCombo(ComboProduct combo) {
        try {
            System.out.println("\nValidating combo:");
            
            // Kiểm tra tên combo
            if (combo.getName() == null || combo.getName().trim().isEmpty()) {
                System.out.println("Invalid: Empty name");
                Alert.showAlert("Combo name cannot be empty");
                return false;
            }
            
            // Kiểm tra phần trăm giảm giá (5-15%)
            double discountPercent = combo.getDiscountPercent();
            if (discountPercent < 5.0 || discountPercent > 15.0) {
                System.out.println("Invalid: Discount percent out of range: " + discountPercent);
                Alert.showAlert("Discount must be between 5% and 15%");
                return false;
            }
            
            // Kiểm tra danh sách sản phẩm (ít nhất 2 sản phẩm)
            List<Coffee> products = combo.getProducts();
            if (products == null || products.size() < 2) {
                System.out.println("Invalid: Insufficient products count: " + 
                    (products == null ? 0 : products.size()));
                Alert.showAlert("Combo must contain at least 2 products");
                return false;
            }
            
            System.out.println("Combo validation passed");
            return true;
        } catch (Exception e) {
            System.err.println("Error validating combo: " + e.getMessage());
            e.printStackTrace();
            Alert.showAlert("Error validating combo: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Thêm combo vào giỏ hàng
     * @param combo Combo cần thêm vào giỏ hàng
     * @param cartManager Đối tượng quản lý giỏ hàng
     */
    public void addComboToCart(ComboProduct combo, helper.CoffeeShop.CartManager cartManager) {
        try {
            System.out.println("\nAdding combo to cart:");
            System.out.println("Combo ID: " + combo.getId());
            System.out.println("Combo Name: " + combo.getName());
            
            if (!combo.isActive()) {
                System.out.println("Cannot add inactive combo to cart");
                Alert.showAlert("This combo is currently not available");
                return;
            }

            // Tính tổng giá gốc
            double originalPrice = combo.getProducts().stream()
                                     .mapToDouble(Coffee::getPrice)
                                     .sum();
            
            // Tính giá sau giảm giá
            double finalPrice = calculateDiscountedPrice(originalPrice, combo.getDiscountPercent());
            
            System.out.println("Original Price: $" + originalPrice);
            System.out.println("Discount: " + combo.getDiscountPercent() + "%");
            System.out.println("Final Price: $" + finalPrice);

            // Thêm combo như một item duy nhất
            cartManager.addComboToCart(combo);
            
            System.out.println("Successfully added combo to cart");
            
        } catch (Exception e) {
            System.err.println("Error adding combo to cart: " + e.getMessage());
            e.printStackTrace();
            Alert.showAlert("Error adding combo to cart: " + e.getMessage());
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