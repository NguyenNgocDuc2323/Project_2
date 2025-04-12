package controller.staff;

import database.OrderDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import model.OrderStatistic;

import java.time.LocalDate;
import java.util.List;

public class OrderStatisticController {
    @FXML
    public Text totalOrderCount;
    @FXML
    public Text totalRevenue;
    @FXML
    private TableView<OrderStatistic> orderMonthlyStatisticTable;
    @FXML
    private TableView<OrderStatistic> orderDailyStatisticTable;
    @FXML
    private TableColumn<OrderStatistic, Integer> monthlyYearColumn;
    @FXML
    private TableColumn<OrderStatistic, Integer> monthlyMonthColumn;
    @FXML
    private TableColumn<OrderStatistic, Integer> monthlyOrderCountColumn;
    @FXML
    private TableColumn<OrderStatistic, Double> monthlyRevenueColumn;
    @FXML
    private TableColumn<OrderStatistic, Integer> dailyYearColumn;
    @FXML
    private TableColumn<OrderStatistic, Integer> dailyMonthColumn;
    @FXML
    private TableColumn<OrderStatistic, Integer> dailyDayColumn;
    @FXML
    private TableColumn<OrderStatistic, Integer> dailyOrderCountColumn;
    @FXML
    private TableColumn<OrderStatistic, Double> dailyRevenueColumn;
    @FXML
    private LineChart<String, Number> monthlyOrderLineChart;
    @FXML
    private LineChart<String, Number> monthlyRevenueLineChart;
    @FXML
    private LineChart<String, Number> dailyOrderLineChart;
    @FXML
    private LineChart<String, Number> dailyRevenueLineChart;
    @FXML
    private CategoryAxis monthlyOrderXAxis;
    @FXML
    private NumberAxis monthlyOrderYAxis;
    @FXML
    private CategoryAxis monthlyRevenueXAxis;
    @FXML
    private NumberAxis monthlyRevenueYAxis;
    @FXML
    private CategoryAxis dailyOrderXAxis;
    @FXML
    private NumberAxis dailyOrderYAxis;
    @FXML
    private CategoryAxis dailyRevenueXAxis;
    @FXML
    private NumberAxis dailyRevenueYAxis;
    private final ObservableList<OrderStatistic> orderMonthlyStatisticObservableList = FXCollections.observableArrayList();
    private final ObservableList<OrderStatistic> orderDailyStatisticObservableList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        monthlyYearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        monthlyMonthColumn.setCellValueFactory(new PropertyValueFactory<>("month"));
        monthlyOrderCountColumn.setCellValueFactory(new PropertyValueFactory<>("orderCount"));
        monthlyRevenueColumn.setCellValueFactory(new PropertyValueFactory<>("revenue"));
        monthlyRevenueColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.format("%,.0f", item));
                }
            }
        });

        dailyYearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        dailyMonthColumn.setCellValueFactory(new PropertyValueFactory<>("month"));
        dailyDayColumn.setCellValueFactory(new PropertyValueFactory<>("day"));
        dailyOrderCountColumn.setCellValueFactory(new PropertyValueFactory<>("orderCount"));
        dailyRevenueColumn.setCellValueFactory(new PropertyValueFactory<>("revenue"));
        dailyRevenueColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.format("%,.0f", item));
                }
            }
        });

        loadOrderStatistic();
        loadTotalOrderCountAndTotalRevenue();
        loadLineChart();
    }

    private void loadOrderStatistic() {
        orderMonthlyStatisticObservableList.setAll(OrderDB.getInstance().getOrderMonthlyStatistic());
        orderMonthlyStatisticTable.setItems(orderMonthlyStatisticObservableList);

        orderDailyStatisticObservableList.setAll(OrderDB.getInstance().getOrderDailyStatistic());
        orderDailyStatisticTable.setItems(orderDailyStatisticObservableList);
    }

    private void loadTotalOrderCountAndTotalRevenue() {
        OrderStatistic orderStatistic = OrderDB.getInstance().getTotalOrderCountAndTotalRevenue();
        this.totalOrderCount.setText(String.valueOf(orderStatistic.getOrderCount()));
        this.totalRevenue.setText(String.format("%,.0f", orderStatistic.getRevenue()));
    }

    private void loadLineChart() {
        LocalDate currentDate = LocalDate.now();

        XYChart.Series<String, Number> monthlyOrderCountSeries = new XYChart.Series<>();
        monthlyOrderCountSeries.setName("Order Count");

        XYChart.Series<String, Number> monthlyRevenueSeries = new XYChart.Series<>();
        monthlyRevenueSeries.setName("Revenue");

        XYChart.Series<String, Number> dailyOrderCountSeries = new XYChart.Series<>();
        dailyOrderCountSeries.setName("Order Count");

        XYChart.Series<String, Number> dailyRevenueSeries = new XYChart.Series<>();
        dailyRevenueSeries.setName("Revenue");

        List<OrderStatistic> orderMonthlyStatisticList = OrderDB.getInstance().getOrderMonthlyStatistic();
        List<OrderStatistic> orderDailyStatisticList = OrderDB.getInstance().getOrderDailyStatistic();

        for (OrderStatistic stat : orderMonthlyStatisticList) {
            int year = stat.getYear();
            int month = stat.getMonth();
            int orderCount = stat.getOrderCount();
            double revenue = stat.getRevenue();
            String yearMonth = year + "/" + month;

            LocalDate monthDate = LocalDate.of(year, month, 1);
            // Display only the last 12 months in chart
            if (monthDate.isAfter(currentDate.minusMonths(12))) {
                monthlyOrderCountSeries.getData().add(new XYChart.Data<>(yearMonth, orderCount));
                monthlyRevenueSeries.getData().add(new XYChart.Data<>(yearMonth, revenue));
            }
        }

        monthlyOrderLineChart.getData().add(monthlyOrderCountSeries);
        monthlyRevenueLineChart.getData().add(monthlyRevenueSeries);

        for (OrderStatistic stat : orderDailyStatisticList) {
            int year = stat.getYear();
            int month = stat.getMonth();
            int day = stat.getDay();
            int orderCount = stat.getOrderCount();
            double revenue = stat.getRevenue();
            String yearMonthDay = year + "/" + month + "/" + day;

            LocalDate dayDate = LocalDate.of(year, month, day);
            // Display only the last 30 days in chart
            if (dayDate.isAfter(currentDate.minusDays(30))) {
                dailyOrderCountSeries.getData().add(new XYChart.Data<>(yearMonthDay, orderCount));
                dailyRevenueSeries.getData().add(new XYChart.Data<>(yearMonthDay, revenue));
            }
        }

        dailyOrderLineChart.getData().add(dailyOrderCountSeries);
        dailyRevenueLineChart.getData().add(dailyRevenueSeries);
    }
}
