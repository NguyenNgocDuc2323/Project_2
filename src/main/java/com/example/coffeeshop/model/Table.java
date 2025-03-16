package com.example.coffeeshop.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;

@Getter
@Setter
@RequiredArgsConstructor
public class Table {
    private Integer id;
    private String tableName;
    private Integer capacity;
    private String status;
    private Integer floorNumber;

    public Table(Integer id, String tableName, Integer capacity, String status, Integer floorNumber) {
        this.id = id;
        this.tableName = tableName;
        this.capacity = capacity;
        this.status = status;
        this.floorNumber = floorNumber;
    }

    public Table(String tableName, Integer capacity, String status, Integer floorNumber) {
        this.tableName = tableName;
        this.capacity = capacity;
        this.status = status;
        this.floorNumber = floorNumber;
    }

    public IntegerProperty idProperty() {
        return new SimpleIntegerProperty(id);
    }

    public StringProperty tableNameProperty() {
        return new SimpleStringProperty(tableName);
    }

    public IntegerProperty capacityProperty() {
        return new SimpleIntegerProperty(capacity);
    }

    public StringProperty statusProperty() {
        return new SimpleStringProperty(status);
    }

    public IntegerProperty floorNumberProperty() {
        return new SimpleIntegerProperty(floorNumber);
    }
}
