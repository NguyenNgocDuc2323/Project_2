package model.Admin;

import java.sql.Timestamp;

public class OrderSale {
    private int orderId;
    private Timestamp orderDate;
    private double totalAmount;
    private String customerName;

    public OrderSale(int orderId, Timestamp orderDate, double totalAmount, String customerName) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.customerName = customerName;
    }

    public int getOrderId() { return orderId; }
    public Timestamp getOrderDate() { return orderDate; }
    public double getTotalAmount() { return totalAmount; }
    public String getCustomerName() { return customerName; }
}