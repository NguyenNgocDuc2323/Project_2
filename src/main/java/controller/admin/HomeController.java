package controller.admin;

import helper.DB_Helper.Account_DB_Helper;
import helper.DB_Helper.Order_DB_Helper;
import helper.DB_Helper.Product_DB_Helper;
import helper.DB_Helper.Table_DB_Helper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import model.Admin.OrderDetailDisplay;
import model.Order;
import model.OrderDetail;

import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
public class HomeController {
    @FXML
    private Label total_user;
    @FXML
    private Label total_order;
    @FXML
    private Label total_product;
    @FXML
    private Label total_table;

    @FXML
    private VBox orderContainer;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<Integer> dayCombo;
    @FXML
    private ComboBox<Integer> monthCombo;
    @FXML
    private ComboBox<Integer> yearCombo;

    @FXML
    private TableView<OrderDetailDisplay> revenueTable;
    @FXML
    private TableColumn<OrderDetailDisplay, String> categoryCol;
    @FXML
    private TableColumn<OrderDetailDisplay, String> productCol;
    @FXML
    private TableColumn<OrderDetailDisplay, Integer> quantityCol;
    @FXML
    private TableColumn<OrderDetailDisplay, Double> amountCol;
    @FXML
    private TableColumn<OrderDetailDisplay, LocalDateTime> dateCol;

    @FXML
    public void initialize() {
        int userCount = Account_DB_Helper.countAcc();
        int orderCount = Order_DB_Helper.countOrder();
        int productCount = Product_DB_Helper.countProducts();
        int tableCount = Table_DB_Helper.countTable();

        total_user.setText(String.valueOf(userCount));
        total_order.setText(String.valueOf(orderCount));
        total_product.setText(String.valueOf(productCount));
        total_table.setText(String.valueOf(tableCount));

        List<Order> orders = Order_DB_Helper.getAllOrdersWithDetails();
        for (Order order : orders) {
            HBox row = createOrderRow(order);
            orderContainer.getChildren().add(row);
        }

        for (int i = 1; i <= 31; i++) {
            dayCombo.getItems().add(i);
        }
        for (int i = 1; i <= 12; i++) {
            monthCombo.getItems().add(i);
        }
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear - 5; i <= currentYear; i++) {
            yearCombo.getItems().add(i);
        }

        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        productCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        loadRevenueData(null, null, null, null);
    }

    private HBox createOrderRow(Order order) {
        HBox hbox = new HBox();
        hbox.setSpacing(80);
        hbox.setAlignment(Pos.CENTER);
        hbox.setPrefHeight(55);
        hbox.setPrefWidth(980);

        Label idLabel = new Label(String.valueOf(order.getId()));
        Label nameLabel = new Label(order.getTableName());
        Label dateLabel = new Label(order.getOrderDate().toString());
        Label amountLabel = new Label(String.format("%.2f", order.getTotalPrice()));
        Label statusLabel = new Label(order.getStatus());

        idLabel.setTextFill(Color.web("#333333"));
        nameLabel.setTextFill(Color.web("#333333"));
        dateLabel.setTextFill(Color.web("#333333"));
        amountLabel.setTextFill(Color.web("#333333"));
        statusLabel.setTextFill(Color.web("#333333"));

        hbox.getChildren().addAll(idLabel, nameLabel, dateLabel, amountLabel, new Label(), statusLabel);
        return hbox;
    }

    @FXML
    private void handleFilter(javafx.event.ActionEvent event) {
        Integer day = dayCombo.getValue();
        Integer month = monthCombo.getValue();
        Integer year = yearCombo.getValue();
        String searchTerm = searchField.getText();
        loadRevenueData(day, month, year, searchTerm);
    }

    private void loadRevenueData(Integer day, Integer month, Integer year, String searchTerm) {
        List<Order> orders = Order_DB_Helper.getAllOrdersWithDetails();

        orders.sort(Comparator.comparing(Order::getTotalPrice).reversed());

        List<OrderDetailDisplay> displayList = new ArrayList<>();

        for (Order order : orders) {
            LocalDateTime date = order.getOrderDate();
            if ((day != null && date.getDayOfMonth() != day) ||
                    (month != null && date.getMonthValue() != month) ||
                    (year != null && date.getYear() != year)) {
                continue;
            }

            for (OrderDetail detail : order.getOrderDetails()) {
                if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                    String lowerTerm = searchTerm.toLowerCase();

                    String productName = detail.getProductName().toLowerCase();
                    String categoryName = detail.getCategoryName() != null ? detail.getCategoryName().toLowerCase() : "";
                    String quantityStr = String.valueOf(detail.getQuantity());
                    String amountStr = String.format("%.2f", detail.getUnitPrice() * detail.getQuantity());
                    String dateStr = date.toLocalDate().toString();

                    if (!productName.contains(lowerTerm)
                            && !categoryName.contains(lowerTerm)
                            && !quantityStr.contains(lowerTerm)
                            && !amountStr.contains(lowerTerm)
                            && !dateStr.contains(lowerTerm)) {
                        continue;
                    }
                }

                displayList.add(new OrderDetailDisplay(
                        detail.getCategoryName(),
                        detail.getProductName(),
                        detail.getQuantity(),
                        detail.getUnitPrice() * detail.getQuantity(),
                        date
                ));
            }
        }

        displayList.sort(Comparator.comparing(OrderDetailDisplay::getAmount).reversed());

        revenueTable.getItems().clear();
        revenueTable.setItems(FXCollections.observableArrayList(displayList));
    }

    public void handdleSearch(javafx.event.ActionEvent event) {
        handleFilter(event);
    }


    public void onSearchFieldKeyReleased(javafx.scene.input.KeyEvent keyEvent) {
        String keyword = searchField.getText();
        Integer day = dayCombo.getValue();
        Integer month = monthCombo.getValue();
        Integer year = yearCombo.getValue();

        loadRevenueData(day, month, year, keyword);
    }
}
