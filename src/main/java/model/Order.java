package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private Integer id;
    private Integer userId;
    private Integer tableId;
    private String tableName;
    private LocalDateTime orderDate;
    private String status;
    private Double totalPrice;
    private String paymentMethod;
    private List<OrderDetail> orderDetails = new ArrayList<>();

    public Order() {}
    public Order(Integer id, Integer userId, Integer tableId, String tableName, LocalDateTime orderDate, String status, Double totalPrice, String paymentMethod) {
        this.id = id;
        this.userId = userId;
        this.tableId = tableId;
        this.tableName = tableName;
        this.orderDate = orderDate;
        this.status = status;
        this.totalPrice = totalPrice;
        this.paymentMethod = paymentMethod;
    }

    public Order(Integer id, Integer userId, Integer tableId, LocalDateTime orderDate, String status, Double totalPrice, String paymentMethod) {
        this.id = id;
        this.userId = userId;
        this.tableId = tableId;
        this.orderDate = orderDate;
        this.status = status;
        this.totalPrice = totalPrice;
        this.paymentMethod = paymentMethod;
    }

    public Order(Integer userId, Integer tableId, String paymentMethod) {
        this.userId = userId;
        this.tableId = tableId;
        this.paymentMethod = paymentMethod;
    }

    public Order(Integer id, String status) {
        this.id = id;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getTableId() {
        return tableId;
    }

    public void setTableId(Integer tableId) {
        this.tableId = tableId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }
}
