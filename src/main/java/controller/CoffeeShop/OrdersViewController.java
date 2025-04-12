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
import model.CoffeeShop.OrderDetailMenu;
import model.CoffeeShop.OrderItem;

import java.net.URL;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
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
    @FXML private ComboBox<String> detailsTableCombo;
    @FXML private Label detailsDate;
    @FXML private ComboBox<String> detailsStatusCombo;
    @FXML private ComboBox<String> detailsPaymentCombo;
    @FXML private Label detailsSubtotal;
    @FXML private Label detailsTax;
    @FXML private Label detailsTotal;
    @FXML private Button saveChangesBtn;

    @FXML private TableView<OrderDetailMenu> orderItemsTable;
    @FXML private TableColumn<OrderDetailMenu, String> itemNameColumn;
    @FXML private TableColumn<OrderDetailMenu, String> itemSizeColumn;
    @FXML private TableColumn<OrderDetailMenu, Integer> itemQuantityColumn;
    @FXML private TableColumn<OrderDetailMenu, String> itemUnitPriceColumn;
    @FXML private TableColumn<OrderDetailMenu, String> itemSubtotalColumn;

    private ObservableList<OrderItem> ordersList = FXCollections.observableArrayList();
    private ObservableList<OrderDetailMenu> orderDetailsListMenu = FXCollections.observableArrayList();
    private DecimalFormat currencyFormat;
    private OrderItem currentOrder;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupCurrencyFormatter();
        setupFilters();
        setupOrdersTable();
        setupEventListeners();
        loadOrders();
        setupEditableControls();

        // Set fixed height for the main orders table to show exactly 6 rows
        ordersTable.setFixedCellSize(40);
        double mainTableHeight = ordersTable.getFixedCellSize() * 6 + 30;
        ordersTable.setPrefHeight(mainTableHeight);
        ordersTable.setMinHeight(mainTableHeight);
        ordersTable.setMaxHeight(mainTableHeight);

        // Set fixed height for the order details table to show exactly 6 rows
        orderItemsTable.setFixedCellSize(40);
        double detailsTableHeight = orderItemsTable.getFixedCellSize() * 6 + 30;
        orderItemsTable.setPrefHeight(detailsTableHeight);
        orderItemsTable.setMinHeight(detailsTableHeight);
        orderItemsTable.setMaxHeight(detailsTableHeight);
    }

    private void setupCurrencyFormatter() {
        currencyFormat = new DecimalFormat("$#,##0.00");
    }

    private void setupFilters() {
        // Setup status filter
        ObservableList<String> statusOptions = FXCollections.observableArrayList(
                "All", "Pending", "Processing", "Completed", "Cancelled");
        statusFilter.setItems(statusOptions);
        statusFilter.setValue("All");
        statusFilter.setOnAction(e -> applyFilters());

        // Setup date filter
        datePicker.setValue(null);
        datePicker.setOnAction(e -> applyFilters());
    }

    private void setupEditableControls() {
        // Setup status dropdown
        detailsStatusCombo.setItems(FXCollections.observableArrayList(
                "Pending", "Processing", "Completed", "Cancelled"));

        // Setup payment method dropdown
        detailsPaymentCombo.setItems(FXCollections.observableArrayList(
                "Cash", "Card", "Mobile Payment", "Unpaid"));

        // Load available tables
        loadAvailableTables();

        // Set up save button
        saveChangesBtn.setOnAction(e -> saveOrderChanges());
    }

    private void loadAvailableTables() {
        ObservableList<String> tables = FXCollections.observableArrayList();
        tables.add("Takeaway"); // Add takeaway option

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT table_name FROM tables ORDER BY table_name")) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tables.add(rs.getString("table_name"));
            }

            detailsTableCombo.setItems(tables);

        } catch (SQLException e) {
            showAlert("Error loading tables: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setupOrdersTable() {
        ordersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        tableColumn.setCellValueFactory(new PropertyValueFactory<>("tableName"));

        statusColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getStatus()));
        totalColumn.setCellValueFactory(param ->
                new SimpleStringProperty(currencyFormat.format(param.getValue().getTotalPrice())));

        // Center alignment for all columns
        centerAlignColumn(orderIdColumn);
        centerAlignColumn(dateColumn);
        centerAlignColumn(tableColumn);
        centerAlignColumn(totalColumn);

        // Custom cell factory for status column with color-coded badges
        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    Label statusLabel = new Label(status);
                    statusLabel.getStyleClass().add("status-badge");
                    statusLabel.getStyleClass().add("status-" + status.toLowerCase());

                    HBox centeredBox = new HBox(statusLabel);
                    centeredBox.setAlignment(Pos.CENTER);

                    setGraphic(centeredBox);
                    setText(null);
                }
            }
        });

        setupActionColumn();
        setupOrderDetailsTableCentered();
    }

    // Center align columns
    private <T> void centerAlignColumn(TableColumn<OrderItem, T> column) {
        column.setCellFactory(col -> {
            TableCell<OrderItem, T> cell = new TableCell<>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.toString());
                    }
                    setAlignment(Pos.CENTER);
                }
            };
            return cell;
        });
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button viewBtn = new Button("View / Edit");

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
                    HBox container = new HBox(viewBtn);
                    container.setAlignment(Pos.CENTER);
                    setGraphic(container);
                }
            }
        });
    }

    private void setupOrderDetailsTableCentered() {
        orderItemsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        itemSizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        itemQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        itemUnitPriceColumn.setCellValueFactory(param ->
                new SimpleStringProperty(currencyFormat.format(param.getValue().getUnitPrice())));

        itemSubtotalColumn.setCellValueFactory(param ->
                new SimpleStringProperty(currencyFormat.format(param.getValue().getSubtotal())));

        // Center align all detail table columns
        centerAlignDetailColumn(itemNameColumn);
        centerAlignDetailColumn(itemSizeColumn);
        centerAlignDetailColumn(itemQuantityColumn);
        centerAlignDetailColumn(itemUnitPriceColumn);
        centerAlignDetailColumn(itemSubtotalColumn);
    }

    private <T> void centerAlignDetailColumn(TableColumn<OrderDetailMenu, T> column) {
        column.setCellFactory(col -> {
            TableCell<OrderDetailMenu, T> cell = new TableCell<>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.toString());
                    }
                    setAlignment(Pos.CENTER);
                }
            };
            return cell;
        });
    }

    private void setupEventListeners() {
        closeDetailsBtn.setOnAction(e -> {
            orderDetailsContainer.setVisible(false);
            orderDetailsContainer.setManaged(false);
        });
    }

    private void loadOrders() {
        ordersList.clear();

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT o.id, o.order_date, o.status, o.total_price, o.payment_method, " +
                             "IFNULL(t.table_name, 'Takeaway') AS table_name, IFNULL(t.id, -1) AS table_id " +
                             "FROM orders o " +
                             "LEFT JOIN tables t ON o.table_id = t.id " +
                             "ORDER BY o.order_date DESC")) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                OrderItem order = new OrderItem(
                        rs.getInt("id"),
                        rs.getString("order_date"),
                        rs.getString("status"),
                        rs.getDouble("total_price"),
                        rs.getString("payment_method"),
                        rs.getString("table_name"),
                        rs.getInt("table_id")
                );
                ordersList.add(order);
            }

            ordersTable.setItems(ordersList);

        } catch (SQLException e) {
            showAlert("Error loading orders: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void applyFilters() {
        String selectedStatus = statusFilter.getValue();
        LocalDate selectedDate = datePicker.getValue();

        ObservableList<OrderItem> filteredList = FXCollections.observableArrayList(ordersList);

        if (!"All".equals(selectedStatus)) {
            filteredList = filteredList.filtered(order ->
                    order.getStatus().equalsIgnoreCase(selectedStatus));
        }

        if (selectedDate != null) {
            String dateStr = selectedDate.toString();
            filteredList = filteredList.filtered(order ->
                    order.getOrderDate().startsWith(dateStr));
        }

        ordersTable.setItems(filteredList);
    }

    private void viewOrderDetails(OrderItem order) {
        currentOrder = order;

        // Set order details
        detailsOrderId.setText(String.valueOf(order.getId()));
        detailsTableCombo.setValue(order.getTableName());
        detailsDate.setText(order.getOrderDate().replace(".0", ""));
        detailsStatusCombo.setValue(order.getStatus());

        // Set payment method or "Unpaid"
        String payment = order.getPaymentMethod() != null ? order.getPaymentMethod() : "Unpaid";
        detailsPaymentCombo.setValue(payment);

        // Load order items
        loadOrderItems(order.getId());

        // Calculate totals
        double subtotal = order.getTotalPrice() / 1.08; // Remove 8% tax for subtotal
        double tax = order.getTotalPrice() - subtotal;

        detailsSubtotal.setText(currencyFormat.format(subtotal));
        detailsTax.setText(currencyFormat.format(tax));
        detailsTotal.setText(currencyFormat.format(order.getTotalPrice()));

        // Show order details container
        orderDetailsContainer.setVisible(true);
        orderDetailsContainer.setManaged(true);
    }

    private void loadOrderItems(int orderId) {
        orderDetailsListMenu.clear();

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT od.id, od.product_id, p.name AS product_name, " +
                             "od.quantity, od.unit_price, " +
                             "IFNULL(s.symbol, '') AS size " +
                             "FROM order_detail od " +
                             "JOIN product p ON od.product_id = p.id " +
                             "LEFT JOIN sizes s ON s.id = SUBSTRING_INDEX(p.name, '-', -1) " +
                             "WHERE od.order_id = ?")) {

            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String productName = rs.getString("product_name");
                String size = rs.getString("size");
                int quantity = rs.getInt("quantity");
                double unitPrice = rs.getDouble("unit_price");
                double subtotal = quantity * unitPrice;

                OrderDetailMenu item = new OrderDetailMenu(
                        productName,
                        size,
                        quantity,
                        unitPrice,
                        subtotal
                );
                orderDetailsListMenu.add(item);
            }

            orderItemsTable.setItems(orderDetailsListMenu);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error loading order details: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void saveOrderChanges() {
        if (currentOrder == null) return;

        String selectedTable = detailsTableCombo.getValue();
        String selectedStatus = detailsStatusCombo.getValue();
        String selectedPayment = detailsPaymentCombo.getValue();

        if (selectedStatus == null || selectedPayment == null) {
            showAlert("Please select both status and payment method", Alert.AlertType.WARNING);
            return;
        }

        if ("Unpaid".equals(selectedPayment)) {
            selectedPayment = null; // Store as NULL in database
        }

        try (Connection conn = ConnectDatabase.getConnection()) {
            // Get table ID from name
            Integer tableId = null;
            if (!"Takeaway".equals(selectedTable)) {
                String tableQuery = "SELECT id FROM tables WHERE table_name = ?";
                try (PreparedStatement tableStmt = conn.prepareStatement(tableQuery)) {
                    tableStmt.setString(1, selectedTable);
                    ResultSet rs = tableStmt.executeQuery();
                    if (rs.next()) {
                        tableId = rs.getInt("id");
                    }
                }
            }

            // Update order
            String updateQuery = "UPDATE orders SET table_id = ?, status = ?, payment_method = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                if (tableId != null) {
                    stmt.setInt(1, tableId);
                } else {
                    stmt.setNull(1, Types.INTEGER);
                }

                stmt.setString(2, selectedStatus);
                stmt.setString(3, selectedPayment);
                stmt.setInt(4, currentOrder.getId());

                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    // Update table status depending on action
                    updateTablesStatus(conn, currentOrder.getTableId(), tableId, selectedStatus);

                    // Show success message
                    showAlert("Order #" + currentOrder.getId() + " updated successfully", Alert.AlertType.INFORMATION);

                    // Reload orders to reflect changes
                    loadOrders();

                    // Close details view
                    orderDetailsContainer.setVisible(false);
                    orderDetailsContainer.setManaged(false);
                }
            }
        } catch (SQLException e) {
            showAlert("Error updating order: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void updateTablesStatus(Connection conn, int oldTableId, Integer newTableId, String orderStatus)
            throws SQLException {
        // Release old table if it exists and is different from new table
        if (oldTableId > 0 && (newTableId == null || oldTableId != newTableId)) {
            String updateOldTable = "UPDATE tables SET status = 'available' WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateOldTable)) {
                stmt.setInt(1, oldTableId);
                stmt.executeUpdate();
            }
        }

        // Update new table status if exists and order is not completed or cancelled
        if (newTableId != null && !("Completed".equals(orderStatus) || "Cancelled".equals(orderStatus))) {
            String updateNewTable = "UPDATE tables SET status = 'occupied' WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateNewTable)) {
                stmt.setInt(1, newTableId);
                stmt.executeUpdate();
            }
        } else if (newTableId != null) {
            // Free table if order is completed or cancelled
            String updateNewTable = "UPDATE tables SET status = 'available' WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateNewTable)) {
                stmt.setInt(1, newTableId);
                stmt.executeUpdate();
            }
        }
    }

    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertType == Alert.AlertType.INFORMATION ? "Success" : "Message");
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Apply styling to the dialog
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStyleClass().add("custom-alert");

        // Style based on the alert type
        switch (alertType) {
            case INFORMATION:
                dialogPane.getStyleClass().add("info-dialog");
                break;
            case WARNING:
                dialogPane.getStyleClass().add("warning-dialog");
                break;
            case ERROR:
                dialogPane.getStyleClass().add("error-dialog");
                break;
            default:
                break;
        }

        // Get the stylesheet from the orders.css file
        String cssPath = getClass().getResource("/assets/styles/orders.css").toExternalForm();
        dialogPane.getStylesheets().add(cssPath);

        alert.showAndWait();
    }
}