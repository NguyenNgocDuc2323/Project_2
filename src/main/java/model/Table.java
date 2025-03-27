package model;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(Integer floorNumber) {
        this.floorNumber = floorNumber;
    }
}
