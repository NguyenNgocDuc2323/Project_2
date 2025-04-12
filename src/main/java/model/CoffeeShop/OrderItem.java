package model.CoffeeShop;

public class OrderItem {
    private int id;
    private String orderDate;
    private String status;
    private double totalPrice;
    private String paymentMethod;
    private String tableName;
    private int tableId;

    public OrderItem(int id, String orderDate, String status, double totalPrice, String paymentMethod, String tableName, int tableId) {
        this.id = id;
        this.orderDate = orderDate;
        this.status = status;
        this.totalPrice = totalPrice;
        this.paymentMethod = paymentMethod;
        this.tableName = tableName;
        this.tableId = tableId;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", orderDate='" + orderDate + '\'' +
                ", status='" + status + '\'' +
                ", totalPrice=" + totalPrice +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", tableName='" + tableName + '\'' +
                ", tableId=" + tableId +
                '}';
    }
}