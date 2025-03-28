package model.CoffeeShop;

import java.sql.Timestamp;

public class OrderItem {
    private int id;
    private Timestamp orderDate;
    private String status;
    private double totalPrice;
    private String paymentMethod;
    private String tableName;

    public OrderItem(int id, Timestamp orderDate, String status, double totalPrice,
                     String paymentMethod, String tableName) {
        this.id = id;
        this.orderDate = orderDate;
        this.status = status;
        this.totalPrice = totalPrice;
        this.paymentMethod = paymentMethod;
        this.tableName = tableName;
    }

    // Getters
    public int getId() { return id; }
    public Timestamp getOrderDate() { return orderDate; }
    public String getStatus() { return status; }
    public double getTotalPrice() { return totalPrice; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getTableName() { return tableName; }
}