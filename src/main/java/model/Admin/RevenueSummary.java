package model.Admin;

public class RevenueSummary {
    private String timeKey;
    private double totalRevenue;
    private int totalQuantitySold;
    private int uniqueCustomers;

    public RevenueSummary(String timeKey, double totalRevenue, int totalQuantitySold, int uniqueCustomers) {
        this.timeKey = timeKey;
        this.totalRevenue = totalRevenue;
        this.totalQuantitySold = totalQuantitySold;
        this.uniqueCustomers = uniqueCustomers;
    }

    public String getTimeKey() { return timeKey; }
    public double getTotalRevenue() { return totalRevenue; }
    public int getTotalQuantitySold() { return totalQuantitySold; }
    public int getUniqueCustomers() { return uniqueCustomers; }
}