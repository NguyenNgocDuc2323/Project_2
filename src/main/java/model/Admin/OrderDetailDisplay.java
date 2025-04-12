package model.Admin;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDateTime;

public class OrderDetailDisplay {
    private final SimpleStringProperty category;
    private final SimpleStringProperty productName;
    private final SimpleIntegerProperty quantity;
    private final SimpleDoubleProperty amount;
    private final SimpleObjectProperty<LocalDateTime> date;

    public OrderDetailDisplay(String category, String productName, int quantity, double amount, LocalDateTime date) {
        this.category = new SimpleStringProperty(category);
        this.productName = new SimpleStringProperty(productName);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.amount = new SimpleDoubleProperty(amount);
        this.date = new SimpleObjectProperty<>(date);
    }

    public String getCategory() { return category.get(); }
    public String getProductName() { return productName.get(); }
    public int getQuantity() { return quantity.get(); }
    public double getAmount() { return amount.get(); }
    public LocalDateTime getDate() { return date.get(); }
}
