package model.CoffeeShop;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class ComboProduct {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty description;
    private final DoubleProperty originalPrice; // Tổng giá gốc của các sản phẩm
    private final DoubleProperty discountPercent; // Phần trăm giảm giá (5-15%)
    private final DoubleProperty finalPrice; // Giá sau khi giảm
    private final IntegerProperty status; // 1 = active, 0 = inactive
    private final ObjectProperty<ObservableList<Coffee>> products; // Danh sách sản phẩm trong combo

    public ComboProduct(int id, String name, String description, double discountPercent, int status) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.originalPrice = new SimpleDoubleProperty(0.0);
        this.discountPercent = new SimpleDoubleProperty(discountPercent);
        this.finalPrice = new SimpleDoubleProperty(0.0);
        this.status = new SimpleIntegerProperty(status);
        this.products = new SimpleObjectProperty<>(FXCollections.observableArrayList());
    }

    // Constructor không tham số
    public ComboProduct() {
        this(0, "", "", 5.0, 1);
    }

    // Getters và Setters cho properties
    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty descriptionProperty() { return description; }
    public DoubleProperty originalPriceProperty() { return originalPrice; }
    public DoubleProperty discountPercentProperty() { return discountPercent; }
    public DoubleProperty finalPriceProperty() { return finalPrice; }
    public IntegerProperty statusProperty() { return status; }
    public ObjectProperty<ObservableList<Coffee>> productsProperty() { return products; }

    // Getters và Setters cho values
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }

    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }

    public String getDescription() { return description.get(); }
    public void setDescription(String description) { this.description.set(description); }

    public double getOriginalPrice() { return originalPrice.get(); }
    public void setOriginalPrice(double originalPrice) { this.originalPrice.set(originalPrice); }

    public double getDiscountPercent() { return discountPercent.get(); }
    public void setDiscountPercent(double discountPercent) {
        // Đảm bảo giảm giá nằm trong khoảng 5-15%
        if (discountPercent < 5.0) {
            this.discountPercent.set(5.0);
        } else if (discountPercent > 15.0) {
            this.discountPercent.set(15.0);
        } else {
            this.discountPercent.set(discountPercent);
        }
        updateFinalPrice();
    }

    public double getFinalPrice() { return finalPrice.get(); }
    private void updateFinalPrice() {
        double discount = originalPrice.get() * (discountPercent.get() / 100.0);
        finalPrice.set(originalPrice.get() - discount);
    }

    public int getStatus() { return status.get(); }
    public void setStatus(int status) { this.status.set(status); }
    public boolean isActive() { return status.get() == 1; }

    public ObservableList<Coffee> getProducts() { return products.get(); }
    
    // Thêm sản phẩm vào combo và cập nhật giá
    public void addProduct(Coffee product) {
        products.get().add(product);
        recalculateOriginalPrice();
    }
    
    // Xóa sản phẩm khỏi combo và cập nhật giá
    public void removeProduct(Coffee product) {
        products.get().removeIf(p -> p.getId() == product.getId());
        recalculateOriginalPrice();
    }
    
    // Cập nhật lại giá gốc dựa trên các sản phẩm trong combo
    private void recalculateOriginalPrice() {
        double total = 0.0;
        for (Coffee product : products.get()) {
            total += product.getPrice();
        }
        setOriginalPrice(total);
        updateFinalPrice();
    }
    
    // Thiết lập danh sách sản phẩm mới
    public void setProducts(List<Coffee> productList) {
        products.get().clear();
        products.get().addAll(productList);
        recalculateOriginalPrice();
    }
    
    @Override
    public String toString() {
        return name.get();
    }
}