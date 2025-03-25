package controller.CoffeeShop;

import helper.CoffeeShop.CartManager;
import helper.ConnectDatabase;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.CartItem;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CartViewController implements Initializable {
    @FXML private TableView<CartItem> cartTable;
    @FXML private TableColumn<CartItem, String> itemColumn;
    @FXML private TableColumn<CartItem, String> sizeColumn;
    @FXML private TableColumn<CartItem, Integer> quantityColumn;
    @FXML private TableColumn<CartItem, Double> priceColumn;
    @FXML private TableColumn<CartItem, Double> subtotalColumn;
    @FXML private TableColumn<CartItem, Void> actionColumn;

    @FXML private Label totalItemsLabel;
    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label totalLabel;
    @FXML private Button checkoutButton;
    @FXML private Button clearCartButton;
    @FXML private Button continueShoppingButton;
    @FXML private ComboBox<String> tableComboBox;

    private ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    private final double TAX_RATE = 0.08; // 8% tax

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Disable row selection column/indicator
        cartTable.setTableMenuButtonVisible(false);

        cartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Initialize the table columns
        setupTableColumns();

        // Load available tables
        loadTables();

        // Set up event handlers
        setupEventHandlers();

        // Load items from cart (in a real app, this would come from a CartManager)
        loadCartItems();

        // Update summary
        updateCartSummary();
    }

    private void setupTableColumns() {
        itemColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        sizeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSize()));
        quantityColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        priceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getUnitPrice()).asObject());
        subtotalColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getSubtotal()).asObject());

        // Format price columns to display currency
        priceColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.2f", price));
                }
            }
        });

        subtotalColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double subtotal, boolean empty) {
                super.updateItem(subtotal, empty);
                if (empty || subtotal == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.2f", subtotal));
                }
            }
        });

        // Add action buttons to the table
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button removeButton = new Button("Remove");
            private final Button increaseButton = new Button("+");
            private final Button decreaseButton = new Button("-");

            {
                removeButton.getStyleClass().add("remove-button");
                increaseButton.getStyleClass().add("quantity-button");
                decreaseButton.getStyleClass().add("quantity-button");

                HBox buttonBox = new HBox(5, decreaseButton, increaseButton, removeButton);

                removeButton.setOnAction(event -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    removeFromCart(item);
                });

                increaseButton.setOnAction(event -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    increaseQuantity(item);
                });

                decreaseButton.setOnAction(event -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    decreaseQuantity(item);
                });

                setGraphic(buttonBox);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : getGraphic());
            }
        });

        // Set the items
        cartTable.setItems(cartItems);
    }

    private void loadTables() {
        ObservableList<String> tableNames = FXCollections.observableArrayList();

        try (Connection connection = ConnectDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT table_name, status FROM tables");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String tableName = resultSet.getString("table_name");
                String status = resultSet.getString("status");
                tableNames.add(tableName + " (" + status + ")");
            }

            tableComboBox.setItems(tableNames);
            if (!tableNames.isEmpty()) {
                tableComboBox.getSelectionModel().selectFirst();
            }

        } catch (SQLException e) {
            System.err.println("Error loading tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupEventHandlers() {
        clearCartButton.setOnAction(event -> clearCart());
        checkoutButton.setOnAction(event -> checkout());

        // You can use this to navigate back to menu
        continueShoppingButton.setOnAction(event -> {
            // This would typically use a Navigator pattern to switch views
            System.out.println("Continue shopping clicked");
        });
    }

    // This would be replaced with actual cart data in a real app
    private void loadCartItems() {
        // Clear previous items
        cartItems.clear();

        // Get cart items from CartManager
        cartItems.addAll(CartManager.getInstance().getCartItems());

        // Update the cart table
        cartTable.setItems(cartItems);

        // Update summary
        updateCartSummary();
    }

    private void updateCartSummary() {
        int totalItems = cartItems.stream().mapToInt(CartItem::getQuantity).sum();
        double subtotal = cartItems.stream().mapToDouble(CartItem::getSubtotal).sum();
        double tax = subtotal * TAX_RATE;
        double total = subtotal + tax;

        totalItemsLabel.setText(String.format("%d", totalItems));
        subtotalLabel.setText(String.format("%,.2f", subtotal));
        taxLabel.setText(String.format("%,.2f", tax));
        totalLabel.setText(String.format("%,.2f", total));

        // Disable checkout if cart is empty
        checkoutButton.setDisable(cartItems.isEmpty());
    }

    private void removeFromCart(CartItem item) {
        cartItems.remove(item);
        updateCartSummary();
    }

    private void increaseQuantity(CartItem item) {
        item.setQuantity(item.getQuantity() + 1);
        cartTable.refresh();
        updateCartSummary();
    }

    private void decreaseQuantity(CartItem item) {
        if (item.getQuantity() > 1) {
            item.setQuantity(item.getQuantity() - 1);
            cartTable.refresh();
            updateCartSummary();
        } else {
            removeFromCart(item);
        }
    }

    private void clearCart() {
        CartManager.getInstance().clearCart();
        loadCartItems();
    }

    public void refreshCart() {
        loadCartItems();
    }

    private void checkout() {
        if (tableComboBox.getValue() == null) {
            showAlert("Please select a table before checking out.");
            return;
        }

        if (cartItems.isEmpty()) {
            showAlert("Your cart is empty. Please add items before checking out.");
            return;
        }

        String selectedTable = tableComboBox.getValue();
        String tableName = extractTableName(selectedTable);
        String tableStatus = extractTableStatus(selectedTable);

        int tableId = getTableId(tableName);
        if (tableId == -1) {
            showAlert("Error finding table in database.");
            return;
        }

        if ("Occupied".equals(tableStatus)) {
            boolean addToExisting = showConfirmationDialog(
                    "Add to Existing Order",
                    "Table " + tableName + " already has an active order. Would you like to add these items to the existing order?"
            );

            if (!addToExisting) {
                return;
            }
            // Add to existing order
            addToExistingOrder(tableId);
        } else if ("Unavailable".equals(tableStatus)) {
            boolean placeAsTakeaway = showConfirmationDialog(
                    "Table Unavailable",
                    "Table " + tableName + " is currently unavailable. Would you like to place this as a takeaway order instead?"
            );

            if (!placeAsTakeaway) {
                return;
            }
            // Process as takeaway
            processTakeawayOrder();
        } else {
            // Process as new order
            processNewOrder(tableId);
        }
    }

    private int getTableId(String tableName) {
        try (Connection connection = ConnectDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id FROM tables WHERE table_name = ?")) {

            statement.setString(1, tableName);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (SQLException e) {
            System.err.println("Error getting table ID: " + e.getMessage());
        }
        return -1;
    }

    private void processNewOrder(int tableId) {
        try (Connection connection = ConnectDatabase.getConnection()) {
            // Turn off auto-commit
            connection.setAutoCommit(false);

            try {
                // Insert into orders table
                int orderId;
                try (PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO orders (user_id, table_id, status, total_price, payment_method) VALUES (?, ?, ?, ?, ?)",
                        PreparedStatement.RETURN_GENERATED_KEYS)) {

                    // Assuming user ID 1 for now, in a real app this would be the logged-in user
                    stmt.setInt(1, 1);
                    stmt.setInt(2, tableId);
                    stmt.setString(3, "Pending");

                    // Calculate total
                    double total = cartItems.stream().mapToDouble(CartItem::getSubtotal).sum();
                    stmt.setDouble(4, total);

                    stmt.setString(5, "Cash"); // Default payment method

                    stmt.executeUpdate();

                    // Get the generated order ID
                    ResultSet rs = stmt.getGeneratedKeys();
                    if (rs.next()) {
                        orderId = rs.getInt(1);
                    } else {
                        throw new SQLException("Creating order failed, no ID obtained.");
                    }
                }

                // Insert order details
                try (PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO order_detail (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)")) {

                    for (CartItem item : cartItems) {
                        stmt.setInt(1, orderId);
                        stmt.setInt(2, item.getProductId());
                        stmt.setInt(3, item.getQuantity());
                        stmt.setDouble(4, item.getUnitPrice());
                        stmt.addBatch();
                    }

                    stmt.executeBatch();
                }

                // Update table status to Occupied
                try (PreparedStatement stmt = connection.prepareStatement(
                        "UPDATE tables SET status = 'Occupied' WHERE id = ?")) {

                    stmt.setInt(1, tableId);
                    stmt.executeUpdate();
                }

                // Commit the transaction
                connection.commit();

                // Show success message
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Order Placed");
                alert.setHeaderText("Your order has been placed successfully!");
                alert.setContentText("Your order will be served to selected table.");
                alert.showAndWait();

                // Clear the cart
                CartManager.getInstance().clearCart();
                loadCartItems();

            } catch (SQLException e) {
                // If there is any error, roll back the transaction
                connection.rollback();
                System.err.println("Error processing order: " + e.getMessage());
                e.printStackTrace();

                showAlert("Error processing order: " + e.getMessage());
            } finally {
                // Restore auto-commit to true
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            e.printStackTrace();

            showAlert("Database connection error: " + e.getMessage());
        }
    }

    private void addToExistingOrder(int tableId) {
        try (Connection connection = ConnectDatabase.getConnection()) {
            // Turn off auto-commit
            connection.setAutoCommit(false);

            try {
                // Find the existing order for this table
                int orderId;
                try (PreparedStatement stmt = connection.prepareStatement(
                        "SELECT id FROM orders WHERE table_id = ? AND status = 'Pending'")) {

                    stmt.setInt(1, tableId);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        orderId = rs.getInt("id");
                    } else {
                        // No existing order found, create a new one
                        processNewOrder(tableId);
                        return;
                    }
                }

                // Add items to existing order
                try (PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO order_detail (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)")) {

                    double totalAddition = 0;

                    for (CartItem item : cartItems) {
                        stmt.setInt(1, orderId);
                        stmt.setInt(2, item.getProductId());
                        stmt.setInt(3, item.getQuantity());
                        stmt.setDouble(4, item.getUnitPrice());
                        stmt.addBatch();

                        totalAddition += item.getSubtotal();
                    }

                    stmt.executeBatch();

                    // Update the order total
                    try (PreparedStatement updateStmt = connection.prepareStatement(
                            "UPDATE orders SET total_price = total_price + ? WHERE id = ?")) {

                        updateStmt.setDouble(1, totalAddition);
                        updateStmt.setInt(2, orderId);
                        updateStmt.executeUpdate();
                    }
                }

                // Commit the transaction
                connection.commit();

                // Show success message
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Order Updated");
                alert.setHeaderText("Your items have been added to the existing order!");
                alert.setContentText("Your additional items will be served to the table.");
                alert.showAndWait();

                // Clear the cart
                CartManager.getInstance().clearCart();
                loadCartItems();

            } catch (SQLException e) {
                // If there is any error, roll back the transaction
                connection.rollback();
                System.err.println("Error adding to existing order: " + e.getMessage());
                e.printStackTrace();

                showAlert("Error adding to existing order: " + e.getMessage());
            } finally {
                // Restore auto-commit to true
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            e.printStackTrace();

            showAlert("Database connection error: " + e.getMessage());
        }
    }

    private void processTakeawayOrder() {
        try (Connection connection = ConnectDatabase.getConnection()) {
            // Turn off auto-commit
            connection.setAutoCommit(false);

            try {
                // Insert into orders table with special table_id for takeaway
                int orderId;
                try (PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO orders (user_id, table_id, status, total_price, payment_method) VALUES (?, 0, ?, ?, ?)",
                        PreparedStatement.RETURN_GENERATED_KEYS)) {

                    // Assuming user ID 1 for now
                    stmt.setInt(1, 1);
                    stmt.setString(2, "Takeaway");

                    // Calculate total
                    double total = cartItems.stream().mapToDouble(CartItem::getSubtotal).sum();
                    stmt.setDouble(3, total);

                    stmt.setString(4, "Cash"); // Default payment method

                    stmt.executeUpdate();

                    // Get the generated order ID
                    ResultSet rs = stmt.getGeneratedKeys();
                    if (rs.next()) {
                        orderId = rs.getInt(1);
                    } else {
                        throw new SQLException("Creating takeaway order failed, no ID obtained.");
                    }
                }

                // Insert order details
                try (PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO order_detail (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)")) {

                    for (CartItem item : cartItems) {
                        stmt.setInt(1, orderId);
                        stmt.setInt(2, item.getProductId());
                        stmt.setInt(3, item.getQuantity());
                        stmt.setDouble(4, item.getUnitPrice());
                        stmt.addBatch();
                    }

                    stmt.executeBatch();
                }

                // Commit the transaction
                connection.commit();

                // Show success message
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Takeaway Order Placed");
                alert.setHeaderText("Your takeaway order has been placed successfully!");
                alert.setContentText("Your order will be prepared for pickup.");
                alert.showAndWait();

                // Clear the cart
                CartManager.getInstance().clearCart();
                loadCartItems();

            } catch (SQLException e) {
                // If there is any error, roll back the transaction
                connection.rollback();
                System.err.println("Error processing takeaway order: " + e.getMessage());
                e.printStackTrace();

                showAlert("Error processing takeaway order: " + e.getMessage());
            } finally {
                // Restore auto-commit to true
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            e.printStackTrace();

            showAlert("Database connection error: " + e.getMessage());
        }
    }

    private String extractTableName(String tableWithStatus) {
        // Extract name from string like "Table 1 (Available)"
        int endIndex = tableWithStatus.lastIndexOf(" (");
        if (endIndex != -1) {
            return tableWithStatus.substring(0, endIndex);
        }
        return tableWithStatus; // Default if format doesn't match
    }

    private String extractTableStatus(String tableWithStatus) {
        // Extract status from string like "Table 1 (Available)"
        int startIndex = tableWithStatus.lastIndexOf("(");
        int endIndex = tableWithStatus.lastIndexOf(")");

        if (startIndex != -1 && endIndex != -1) {
            return tableWithStatus.substring(startIndex + 1, endIndex);
        }
        return "Unknown"; // Default if format doesn't match
    }

    private boolean showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");

        alert.getButtonTypes().setAll(yesButton, noButton);

        return alert.showAndWait().orElse(noButton) == yesButton;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}