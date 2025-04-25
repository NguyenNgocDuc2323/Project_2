package model.CoffeeShop;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;

public class ComboProduct {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty description;
    private final DoubleProperty originalPrice;
    private final DoubleProperty discountPercent;
    private final DoubleProperty finalPrice;
    private final IntegerProperty status;
    private final StringProperty comboType;
    private final ObjectProperty<ObservableList<Coffee>> products;

    public ComboProduct(int id, String name, String description, double discountPercent, int status) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.originalPrice = new SimpleDoubleProperty(0.0);
        this.discountPercent = new SimpleDoubleProperty(discountPercent);
        this.finalPrice = new SimpleDoubleProperty(0.0);
        this.status = new SimpleIntegerProperty(status);
        this.comboType = new SimpleStringProperty("");
        this.products = new SimpleObjectProperty<>(FXCollections.observableArrayList());

        // Add listener to automatically update prices when products change
        this.products.get().addListener((javafx.collections.ListChangeListener<Coffee>) change -> {
            while (change.next()) {
                recalculateOriginalPrice();
            }
        });
    }

    // Constructor không tham số
    public ComboProduct() {
        this(0, "", "", 5.0, 1);
    }

    // Properties
    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty descriptionProperty() { return description; }
    public DoubleProperty originalPriceProperty() { return originalPrice; }
    public DoubleProperty discountPercentProperty() { return discountPercent; }
    public DoubleProperty finalPriceProperty() { return finalPrice; }
    public IntegerProperty statusProperty() { return status; }
    public StringProperty comboTypeProperty() { return comboType; }
    public ObjectProperty<ObservableList<Coffee>> productsProperty() { return products; }

    // Getters và Setters
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }

    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }

    public String getDescription() { return description.get(); }
    public void setDescription(String description) { this.description.set(description); }

    public double getOriginalPrice() { return originalPrice.get(); }
    public void setOriginalPrice(double originalPrice) {
        System.out.println("Setting original price to: " + originalPrice);
        this.originalPrice.set(originalPrice);
        updateFinalPrice();
    }

    public String getComboType() { return comboType.get(); }
    public void setComboType(String comboType) { this.comboType.set(comboType); }

    public double getDiscountPercent() { return discountPercent.get(); }
    public void setDiscountPercent(double discountPercent) {
        double validDiscount = Math.min(Math.max(discountPercent, 5.0), 15.0);
        System.out.println("Setting discount percent to: " + validDiscount);
        this.discountPercent.set(validDiscount);
        updateFinalPrice();
    }

    public double getFinalPrice() { return finalPrice.get(); }
    private void updateFinalPrice() {
        double originalPrice = this.originalPrice.get();
        double discount = originalPrice * (discountPercent.get() / 100.0);
        double finalPrice = originalPrice - discount;
        System.out.println("Calculating final price:");
        System.out.println("Original price: " + originalPrice);
        System.out.println("Discount: " + discount);
        System.out.println("Final price: " + finalPrice);
        this.finalPrice.set(finalPrice);
    }

    public int getStatus() { return status.get(); }
    public void setStatus(int status) { this.status.set(status); }
    public boolean isActive() { return status.get() == 1; }

    public ObservableList<Coffee> getProducts() { return products.get(); }

    public void addProduct(Coffee product) {
        if (product != null) {
            System.out.println("Adding product: " + product.getName() + " (Price: " + product.getPrice() + ")");
            products.get().add(product);
            recalculateOriginalPrice();
        }
    }

    public void removeProduct(Coffee product) {
        if (product != null) {
            System.out.println("Removing product: " + product.getName());
            products.get().removeIf(p -> p.getId() == product.getId());
            recalculateOriginalPrice();
        }
    }

    public void setProducts(List<Coffee> productList) {
        if (productList == null) {
            System.err.println("Warning: Attempting to set null product list");
            return;
        }

        System.out.println("\nSetting products for combo: " + getName());
        System.out.println("Number of products: " + productList.size());

        // Clear and add new products
        products.get().clear();
        products.get().addAll(productList);

        // Calculate total price
        double total = 0.0;
        for (Coffee product : productList) {
            System.out.println("Product: " + product.getName() +
                    " - Price: $" + product.getPrice());
            total += product.getPrice();
        }

        System.out.println("Total original price: $" + total);
        setOriginalPrice(total);

        // Calculate final price with discount
        double discount = total * (getDiscountPercent() / 100.0);
        double finalPrice = total - discount;
        System.out.println("Discount amount: $" + discount);
        System.out.println("Final price: $" + finalPrice);

        this.finalPrice.set(finalPrice);
    }

    private void recalculateOriginalPrice() {
        double total = 0.0;
        System.out.println("\nRecalculating price for combo: " + getName());
        System.out.println("Number of products: " + products.get().size());

        for (Coffee product : products.get()) {
            System.out.println("Product: " + product.getName() +
                    " - Price: $" + product.getPrice());
            total += product.getPrice();
        }

        System.out.println("Total original price: $" + total);
        setOriginalPrice(total);

        double discount = total * (getDiscountPercent() / 100.0);
        double finalPrice = total - discount;
        System.out.println("Discount amount: $" + discount);
        System.out.println("Final price: $" + finalPrice);

        this.finalPrice.set(finalPrice);
    }

    public void validatePrices() {
        System.out.println("\nValidating prices for combo: " + getName());

        double calculatedTotal = 0.0;
        for (Coffee product : getProducts()) {
            System.out.println("Product: " + product.getName() +
                    " - Price: $" + product.getPrice());
            calculatedTotal += product.getPrice();
        }

        System.out.println("Current original price: $" + getOriginalPrice());
        System.out.println("Calculated total: $" + calculatedTotal);

        if (Math.abs(calculatedTotal - getOriginalPrice()) > 0.01) {
            System.out.println("Price mismatch detected - updating prices");
            setOriginalPrice(calculatedTotal);

            double discount = calculatedTotal * (getDiscountPercent() / 100.0);
            double finalPrice = calculatedTotal - discount;
            System.out.println("New final price: $" + finalPrice);
            this.finalPrice.set(finalPrice);
        }
    }
    public void setFinalPrice(double finalPrice) {
        this.finalPrice.set(finalPrice);
    }

    @Override
    public String toString() {
        return String.format("%s (Original: %.2f, Discount: %.0f%%, Final: %.2f)",
                name.get(), originalPrice.get(), discountPercent.get(), finalPrice.get());
    }
}