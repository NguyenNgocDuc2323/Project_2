package controller.staff;

import database.OrderDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import model.OrderStatistic;

public class OrderStatisticController {
    @FXML
    public Text totalOrderCount;
    @FXML
    public Text totalRevenue;
    @FXML
    private TableView<OrderStatistic> orderMonthlyStatisticTable;
    @FXML
    private TableView<OrderStatistic> orderWeeklyStatisticTable;
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
    private TableColumn<OrderStatistic, Integer> weeklyYearColumn;
    @FXML
    private TableColumn<OrderStatistic, Integer> weeklyMonthColumn;
    @FXML
    private TableColumn<OrderStatistic, Integer> weeklyWeekColumn;
    @FXML
    private TableColumn<OrderStatistic, Integer> weeklyOrderCountColumn;
    @FXML
    private TableColumn<OrderStatistic, Double> weeklyRevenueColumn;
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
    private final ObservableList<OrderStatistic> orderMonthlyStatisticObservableList = FXCollections.observableArrayList();
    private final ObservableList<OrderStatistic> orderWeeklyStatisticObservableList = FXCollections.observableArrayList();
    private final ObservableList<OrderStatistic> orderDailyStatisticObservableList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        monthlyYearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        monthlyMonthColumn.setCellValueFactory(new PropertyValueFactory<>("month"));
        monthlyOrderCountColumn.setCellValueFactory(new PropertyValueFactory<>("orderCount"));
        monthlyRevenueColumn.setCellValueFactory(new PropertyValueFactory<>("revenue"));

        weeklyYearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        weeklyMonthColumn.setCellValueFactory(new PropertyValueFactory<>("month"));
        weeklyWeekColumn.setCellValueFactory(new PropertyValueFactory<>("week"));
        weeklyOrderCountColumn.setCellValueFactory(new PropertyValueFactory<>("orderCount"));
        weeklyRevenueColumn.setCellValueFactory(new PropertyValueFactory<>("revenue"));

        dailyYearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        dailyMonthColumn.setCellValueFactory(new PropertyValueFactory<>("month"));
        dailyDayColumn.setCellValueFactory(new PropertyValueFactory<>("day"));
        dailyOrderCountColumn.setCellValueFactory(new PropertyValueFactory<>("orderCount"));
        dailyRevenueColumn.setCellValueFactory(new PropertyValueFactory<>("revenue"));

        loadOrderStatistic();
        loadTotalOrderCountAndTotalRevenue();
    }

    private void loadOrderStatistic() {
        orderMonthlyStatisticObservableList.setAll(OrderDB.getInstance().getOrderMonthlyStatistic());
        orderMonthlyStatisticTable.setItems(orderMonthlyStatisticObservableList);

        orderWeeklyStatisticObservableList.setAll(OrderDB.getInstance().getOrderWeeklyStatistic());
        orderWeeklyStatisticTable.setItems(orderWeeklyStatisticObservableList);

        orderDailyStatisticObservableList.setAll(OrderDB.getInstance().getOrderDailyStatistic());
        orderDailyStatisticTable.setItems(orderDailyStatisticObservableList);
    }

    private void loadTotalOrderCountAndTotalRevenue() {
        OrderStatistic orderStatistic = OrderDB.getInstance().getTotalOrderCountAndTotalRevenue();
        this.totalOrderCount.setText(String.valueOf(orderStatistic.getOrderCount()));
        this.totalRevenue.setText(String.valueOf(orderStatistic.getRevenue()));
    }
}
