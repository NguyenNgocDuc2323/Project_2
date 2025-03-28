package model.CoffeeShop;

public class OrderDetailMenu {
    private String productName;
    private String size;
    private int quantity;
    private double unitPrice;
    private double subtotal;

    public OrderDetailMenu(String productName, String size, int quantity, double unitPrice, double subtotal) {
        this.productName = productName;
        this.size = size;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }

    // Getters
    public String getProductName() { return productName; }
    public String getSize() { return size; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public double getSubtotal() { return subtotal; }
}