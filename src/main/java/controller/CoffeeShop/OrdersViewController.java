package controller.CoffeeShop;

import helper.ConnectDatabase;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.OrderDetailMenu;
import model.OrderItem;

import java.net.URL;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class OrdersViewController implements Initializable {
    @FXML private TableView<OrderItem> ordersTable;
    @FXML private TableColumn<OrderItem, Integer> orderIdColumn;
    @FXML private TableColumn<OrderItem, String> dateColumn;
    @FXML private TableColumn<OrderItem, String> tableColumn;
    @FXML private TableColumn<OrderItem, String> statusColumn;
    @FXML private TableColumn<OrderItem, String> totalColumn;
    @FXML private TableColumn<OrderItem, Void> actionColumn;

    @FXML private ComboBox<String> statusFilter;
    @FXML private DatePicker datePicker;

    @FXML private VBox orderDetailsContainer;
    @FXML private Button closeDetailsBtn;
    @FXML private Label detailsOrderId;
    @FXML private Label detailsTable;
    @FXML private Label detailsDate;
    @FXML private Label detailsStatus;
    @FXML private Label detailsPayment;
    @FXML private Label detailsSubtotal;
    @FXML private Label detailsTax;
    @FXML private Label detailsTotal;

    @FXML private TableView<OrderDetailMenu> orderItemsTable;
    @FXML private TableColumn<OrderDetailMenu, String> itemNameColumn;
    @FXML private TableColumn<OrderDetailMenu, String> itemSizeColumn;
    @FXML private TableColumn<OrderDetailMenu, Integer> itemQuantityColumn;
    @FXML private TableColumn<OrderDetailMenu, String> itemUnitPriceColumn;
    @FXML private TableColumn<OrderDetailMenu, String> itemSubtotalColumn;

    private ObservableList<OrderItem> ordersList = FXCollections.observableArrayList();
    private ObservableList<OrderDetailMenu> orderDetailsListMenu = FXCollections.observableArrayList();
    private DecimalFormat currencyFormat = new DecimalFormat("#,###");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupFilters();
        setupOrdersTable();
        setupOrderDetailsTable();
        setupEventListeners();
        loadOrders();
    }

    private void setupFilters() {

        // Setup status filter
        ObservableList<String> statusOptions = FXCollections.observableArrayList(
                "All", "Pending", "Processing", "Completed", "Cancelled"
        );
        statusFilter.setItems(statusOptions);
        statusFilter.setValue("All");
        statusFilter.setOnAction(e -> applyFilters());

        // Setup date filter
        datePicker.setValue(null);
        datePicker.setOnAction(e -> applyFilters());
    }

    private void setupOrdersTable() {
        ordersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        tableColumn.setCellValueFactory(new PropertyValueFactory<>("tableName"));

        statusColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getStatus()));
        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label statusLabel = new Label(status);
                    statusLabel.getStyleClass().addAll("status-badge", "status-" + status.toLowerCase());
                    setGraphic(statusLabel);
                    setText(null);
                }
            }
        });

        totalColumn.setCellValueFactory(param -> {
            double total = param.getValue().getTotalPrice();
            return new SimpleStringProperty(currencyFormat.format(total) + "đ");
        });

        setupActionColumn();
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button viewBtn = new Button("View");

            {
                viewBtn.getStyleClass().addAll("action-button", "view");
                viewBtn.setOnAction(event -> {
                    OrderItem order = getTableView().getItems().get(getIndex());
                    viewOrderDetails(order);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    HBox container = new HBox(5);
                    container.setAlignment(Pos.CENTER);
                    container.getChildren().add(viewBtn);
                    setGraphic(container);
                }
            }
        });
    }

    private void setupOrderDetailsTable() {
        orderItemsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        itemSizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        itemQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        itemUnitPriceColumn.setCellValueFactory(param -> {
            double unitPrice = param.getValue().getUnitPrice();
            return new SimpleStringProperty(currencyFormat.format(unitPrice) + "đ");
        });

        itemSubtotalColumn.setCellValueFactory(param -> {
            double subtotal = param.getValue().getSubtotal();
            return new SimpleStringProperty(currencyFormat.format(subtotal) + "đ");
        });
    }

    private void setupEventListeners() {
        closeDetailsBtn.setOnAction(e -> orderDetailsContainer.setVisible(false));
    }

    private void loadOrders() {
        ordersList.clear();

        try (Connection conn = ConnectDatabase.getConnection()) {
            String query = "SELECT o.id, o.order_date, o.status, o.total_price, o.payment_method, " +
                    "t.table_name FROM orders o " +
                    "LEFT JOIN tables t ON o.table_id = t.id " +
                    "ORDER BY o.order_date DESC";

            try (PreparedStatement pstmt = conn.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    int id = rs.getInt("id");
                    Timestamp orderDate = rs.getTimestamp("order_date");
                    String status = rs.getString("status");
                    double totalPrice = rs.getDouble("total_price");
                    String paymentMethod = rs.getString("payment_method");
                    String tableName = rs.getString("table_name");

                    if (tableName == null && rs.getInt("table_id") == 0) {
                        tableName = "Takeaway";
                    }

                    OrderItem order = new OrderItem(id, orderDate, status, totalPrice,
                            paymentMethod, tableName);
                    ordersList.add(order);
                }
            }

            ordersTable.setItems(ordersList);

        } catch (SQLException e) {
            showAlert("Error loading orders: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void applyFilters() {
        String selectedStatus = statusFilter.getValue();
        LocalDate selectedDate = datePicker.getValue();

        ObservableList<OrderItem> filteredList = FXCollections.observableArrayList(ordersList);

        if (!"All".equals(selectedStatus)) {
            filteredList = filteredList.filtered(order -> order.getStatus().equals(selectedStatus));
        }

        if (selectedDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String dateStr = selectedDate.format(formatter);

            filteredList = filteredList.filtered(order ->
                    order.getOrderDate().toString().substring(0, 10).equals(dateStr));
        }

        ordersTable.setItems(filteredList);
    }

    private void viewOrderDetails(OrderItem order) {
        // Set order details
        detailsOrderId.setText(String.valueOf(order.getId()));
        detailsTable.setText(order.getTableName());
        detailsDate.setText(order.getOrderDate().toString().replace(".0", ""));

        // Set status with style
        detailsStatus.setText(order.getStatus());
        detailsStatus.getStyleClass().removeAll("status-pending", "status-completed",
                "status-cancelled", "status-processing");
        detailsStatus.getStyleClass().add("status-" + order.getStatus().toLowerCase());

        // Set payment method or "Unpaid"
        String payment = order.getPaymentMethod() != null ? order.getPaymentMethod() : "Unpaid";
        detailsPayment.setText(payment);

        // Load order items
        loadOrderItems(order.getId());

        // Calculate totals
        double subtotal = order.getTotalPrice() / 1.08; // Remove 8% tax for subtotal
        double tax = order.getTotalPrice() - subtotal;

        detailsSubtotal.setText(currencyFormat.format(subtotal) + "đ");
        detailsTax.setText(currencyFormat.format(tax) + "đ");
        detailsTotal.setText(currencyFormat.format(order.getTotalPrice()) + "đ");

        // Show order details container
        orderDetailsContainer.setVisible(true);
        orderDetailsContainer.setManaged(true);
    }

    private void loadOrderItems(int orderId) {
        orderDetailsListMenu.clear();

        try (Connection conn = ConnectDatabase.getConnection()) {
            String query = "SELECT od.quantity, od.unit_price, p.name AS product_name, " +
                    "s.symbol AS size " +
                    "FROM order_detail od " +
                    "JOIN product p ON od.product_id = p.id " +
                    "LEFT JOIN product_sizes ps ON p.id = ps.product_id " +
                    "LEFT JOIN sizes s ON ps.size_id = s.id " +
                    "WHERE od.order_id = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, orderId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String productName = rs.getString("product_name");
                        String size = rs.getString("size");
                        int quantity = rs.getInt("quantity");
                        double unitPrice = rs.getDouble("unit_price");

                        OrderDetailMenu detail = new OrderDetailMenu(
                                productName,
                                size != null ? size : "-",
                                quantity,
                                unitPrice,
                                quantity * unitPrice
                        );

                        orderDetailsListMenu.add(detail);
                    }
                }
            }

            orderItemsTable.setItems(orderDetailsListMenu);

        } catch (SQLException e) {
            showAlert("Error loading order items: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}