package model.Admin;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class ProductSale {
    private final ObjectProperty<LocalDateTime> time = new SimpleObjectProperty<>(); // Changed to LocalDateTime
    private final IntegerProperty productId = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty category = new SimpleStringProperty();
    private final IntegerProperty totalSold = new SimpleIntegerProperty();
    private final DoubleProperty totalRevenue = new SimpleDoubleProperty();

    public ProductSale(LocalDateTime time, int productId, String name, String category, int totalSold, double totalRevenue) {
        this.time.set(time);
        this.productId.set(productId);
        this.name.set(name != null ? name : "Unknown");
        this.category.set(category != null ? category : "Unknown");
        this.totalSold.set(Math.max(0, totalSold));
        this.totalRevenue.set(Math.max(0.0, totalRevenue));
    }

    // Getter for time
    public ObjectProperty<LocalDateTime> timeProperty() { return time; }
    public LocalDateTime getTime() { return time.get(); }

    // Other getters remain the same
    public IntegerProperty productIdProperty() { return productId; }
    public StringProperty nameProperty() { return name; }
    public StringProperty categoryProperty() { return category; }
    public IntegerProperty totalSoldProperty() { return totalSold; }
    public DoubleProperty totalRevenueProperty() { return totalRevenue; }

    public int getProductId() { return productId.get(); }
    public String getName() { return name.get(); }
    public String getCategory() { return category.get(); }
    public int getTotalSold() { return totalSold.get(); }
    public double getTotalRevenue() { return totalRevenue.get(); }
}