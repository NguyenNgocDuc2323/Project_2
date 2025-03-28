package model.CoffeeShop;
import javafx.beans.property.*;

public class Coffee {
    private final IntegerProperty id;
    private final StringProperty name ;
    private final IntegerProperty categoryId;
    private final DoubleProperty price;
    private final IntegerProperty quantity;
    private final StringProperty image ;
    private final IntegerProperty unitId;
    private final StringProperty description ;

    public Coffee(int id, String name, int categoryId, double price, int quantity, String image, int unitId, String description) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.categoryId = new SimpleIntegerProperty(categoryId);
        this.price = new SimpleDoubleProperty(price);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.image = new SimpleStringProperty(image);
        this.unitId = new SimpleIntegerProperty(unitId);
        this.description = new SimpleStringProperty(description);
    }




    // Getters for properties (JavaFX binding)
    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public IntegerProperty categoryIdProperty() { return categoryId; }
    public DoubleProperty priceProperty() { return price; }
    public IntegerProperty quantityProperty() { return quantity; }
    public StringProperty imageProperty() { return image; }
    public IntegerProperty unitIdProperty() { return unitId; }
    public StringProperty descriptionProperty() { return description; }

    // Getters and Setters for values
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }

    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }

    public int getCategoryId() { return categoryId.get(); }
    public void setCategoryId(int categoryId) { this.categoryId.set(categoryId); }

    public double getPrice() { return price.get(); }
    public void setPrice(double price) { this.price.set(price); }

    public int getQuantity() { return quantity.get(); }
    public void setQuantity(int quantity) { this.quantity.set(quantity); }

    public String getImage() { return image.get(); }
    public void setImage(String image) { this.image.set(image); }

    public int getUnitId() { return unitId.get(); }
    public void setUnitId(int unitId) { this.unitId.set(unitId); }

    public String getDescription() { return description.get(); }
    public void setDescription(String description) { this.description.set(description); }

}
