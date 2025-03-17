package model;

public class CartItem {
    private int productId;
    private String name;
    private String size;
    private int quantity;
    private double unitPrice;

    public CartItem(int productId, String name, String size, int quantity, double unitPrice) {
        this.productId = productId;
        this.name = name;
        this.size = size;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getSubtotal() {
        return unitPrice * quantity;
    }
}