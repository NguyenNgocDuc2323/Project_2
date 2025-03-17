package com.example.coffeeshop.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Account {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty email;
    private final StringProperty password;
    private final IntegerProperty type;
    private final BooleanProperty locked;
    private final StringProperty typeProperty;
    private final StringProperty lockedProperty;

    public static final int TYPE_ADMIN = 1;
    public static final int TYPE_STAFF = 2;
    public static final int TYPE_STUDENT = 3;


    public Account(int id,String name,String email, String password, int type, boolean locked) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
        this.password = new SimpleStringProperty(password);
        this.type = new SimpleIntegerProperty(type);
        this.locked = new SimpleBooleanProperty(locked);
        this.typeProperty = new SimpleStringProperty(getTypeAsString(type));
        this.lockedProperty = new SimpleStringProperty(locked ? "Locked" : "Open");
    }

    public Account(String email,String name, String password, int type, boolean locked) {
        this.id = new SimpleIntegerProperty(0);
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
        this.password = new SimpleStringProperty(password);
        this.type = new SimpleIntegerProperty(type);
        this.locked = new SimpleBooleanProperty(locked);
        this.typeProperty = new SimpleStringProperty(getTypeAsString(type));
        this.lockedProperty = new SimpleStringProperty(locked ? "Locked" : "Open");
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

    private String getTypeAsString(int type) {
        return switch (type) {
            case TYPE_ADMIN -> "Admin";
            case TYPE_STAFF -> "Staff";
            case TYPE_STUDENT -> "Student";
            default -> "Unknown";
        };
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public String getPassword() {
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public int getType() {
        return type.get();
    }

    public IntegerProperty typeProperty() {
        return type;
    }

    public void setType(int type) {
        this.type.set(type);
        this.typeProperty.set(getTypeAsString(type));
    }

    public boolean isLocked() {
        return locked.get();
    }

    public BooleanProperty lockedProperty() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked.set(locked);
        this.lockedProperty.set(locked ? "Locked" : "Open");
    }

    public String getTypeAsString() {
        return typeProperty.get();
    }

    public StringProperty typeAsStringProperty() {
        return typeProperty;
    }

    public String getLockedAsString() {
        return lockedProperty.get();
    }

    public StringProperty lockedAsStringProperty() {
        return lockedProperty;
    }

    public void setName(String name) {
        this.name.set(name); // Gán giá trị mới cho thuộc tính name

    }
}
