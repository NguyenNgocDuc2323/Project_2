package controller.staff;

import database.OrderDB;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import model.OrderStatistic;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private TableColumn<OrderStatistic, LocalDate> monthlyDateColumn;
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
    private TableColumn<OrderStatistic, LocalDate> dailyDateColumn;
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
    @FXML
    public DatePicker monthlyOrderStatisticDateField;
    @FXML
    public DatePicker dailyOrderStatisticDateField;
    private final ObservableList<OrderStatistic> orderMonthlyOrderStatisticObservableList = FXCollections.observableArrayList();
    private final ObservableList<OrderStatistic> orderDailyOrderStatisticObservableList = FXCollections.observableArrayList();
    private final ObservableList<OrderStatistic> filteredMonthlyOrderStatisticObservableList = FXCollections.observableArrayList();
    private final ObservableList<OrderStatistic> filteredDailyOrderStatisticObservableList = FXCollections.observableArrayList();


    @FXML
    private void initialize() {
        setupMonthlyOrderStatisticTable();
        setupDailyOrderStatisticTable();
        loadOrderStatistic();
        loadTotalOrderCountAndTotalRevenue();
        loadLineChart();
    }

    @FXML
    private void handleMonthlyOrderStatisticFilterByDate(ActionEvent actionEvent) {
        LocalDate selectedDate = monthlyOrderStatisticDateField.getValue();
        if (selectedDate == null) {
            orderMonthlyStatisticTable.setItems(orderMonthlyOrderStatisticObservableList);
        } else {
            int selectedYear = selectedDate.getYear();
            int selectedMonth = selectedDate.getMonthValue();

            filteredMonthlyOrderStatisticObservableList
                    .setAll(orderMonthlyOrderStatisticObservableList
                            .stream()
                            .filter(s -> s.getYear() == selectedYear && s.getMonth() == selectedMonth)
                            .collect(Collectors.toList()));

            orderMonthlyStatisticTable.setItems(filteredMonthlyOrderStatisticObservableList);
        }
    }

    @FXML
    private void handleDailyOrderStatisticFilterByDate(ActionEvent actionEvent) {
        LocalDate selectedDate = dailyOrderStatisticDateField.getValue();
        if (selectedDate == null) {
            orderDailyStatisticTable.setItems(orderDailyOrderStatisticObservableList);
        } else {
            filteredDailyOrderStatisticObservableList
                    .setAll(orderDailyOrderStatisticObservableList
                            .stream()
                            .filter(s -> Objects.equals(s.getDate(), selectedDate))
                            .collect(Collectors.toList()));
            orderDailyStatisticTable.setItems(filteredDailyOrderStatisticObservableList);
        }
    }

    private void setupMonthlyOrderStatisticTable(){
        monthlyDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        monthlyDateColumn.setCellFactory(column -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(formatter));
                }
            }
        });
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
    }

    private void setupDailyOrderStatisticTable(){
        dailyDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
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
    }

    private void loadOrderStatistic() {
        orderMonthlyOrderStatisticObservableList.setAll(OrderDB.getInstance().getOrderMonthlyStatistic());
        orderMonthlyStatisticTable.setItems(orderMonthlyOrderStatisticObservableList);

        orderDailyOrderStatisticObservableList.setAll(OrderDB.getInstance().getOrderDailyStatistic());
        orderDailyStatisticTable.setItems(orderDailyOrderStatisticObservableList);
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
