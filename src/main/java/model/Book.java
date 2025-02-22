package model;

import javafx.beans.property.*;

public class Book {
    private final IntegerProperty id;
    private final StringProperty name;
    private final DoubleProperty price;
    private final BooleanProperty borrowed;

    public Book(int id, String name, double price, boolean borrowed) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleDoubleProperty(price);
        this.borrowed = new SimpleBooleanProperty(borrowed);
    }
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }
    public StringProperty nameProperty() {
        return name;
    }
    public DoubleProperty priceProperty() {
        return price;
    }
    public BooleanProperty borrowedProperty() {
        return borrowed;
    }
}
