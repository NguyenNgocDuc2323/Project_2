package model.Admin;
import java.time.LocalDateTime;

public class CustomerPurchase {
    private final int customerId;
    private final String name;
    private final int orderCount;
    private final double totalSpent;
    private final LocalDateTime lastOrderDate;

    public CustomerPurchase(int customerId, String name, int orderCount, double totalSpent, LocalDateTime lastOrderDate) {
        this.customerId = customerId;
        this.name = name;
        this.orderCount = orderCount;
        this.totalSpent = totalSpent;
        this.lastOrderDate = lastOrderDate;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public LocalDateTime getLastOrderDate() {
        return lastOrderDate;
    }
}