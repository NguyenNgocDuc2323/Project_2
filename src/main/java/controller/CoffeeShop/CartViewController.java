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
import model.CoffeeShop.CartItem;

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
    @FXML private ComboBox<String> paymentMethodComboBox;

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

        // Initialize payment method dropdown
        paymentMethodComboBox.setItems(FXCollections.observableArrayList(
                "Cash", "Credit Card", "Mobile Payment", "Voucher"
        ));
        paymentMethodComboBox.getSelectionModel().selectFirst(); // Default to Cash

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

        // In your CartViewController.java, modify the updateItem method in the actionColumn.setCellFactory:

        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button removeButton = new Button("Remove");
            private final Button increaseButton = new Button("+");
            private final Button decreaseButton = new Button("-");
            private final HBox buttonBox = new HBox(5, decreaseButton, increaseButton, removeButton);

            {
                removeButton.getStyleClass().add("remove-button");
                increaseButton.getStyleClass().add("quantity-button");
                decreaseButton.getStyleClass().add("quantity-button");

                removeButton.setOnAction(event -> {
                    if (getTableRow() != null && getTableRow().getItem() != null) {
                        CartItem item = getTableRow().getItem();
                        removeFromCart(item);
                    }
                });

                increaseButton.setOnAction(event -> {
                    if (getTableRow() != null && getTableRow().getItem() != null) {
                        CartItem item = getTableRow().getItem();
                        increaseQuantity(item);
                    }
                });

                decreaseButton.setOnAction(event -> {
                    if (getTableRow() != null && getTableRow().getItem() != null) {
                        CartItem item = getTableRow().getItem();
                        decreaseQuantity(item);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonBox);
                }
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
        // Remove from cart manager first
        CartManager.getInstance().removeFromCart(item.getProductId(), item.getSize());

        // Then reload cart items from the updated XML
        loadCartItems();
    }

    private void increaseQuantity(CartItem item) {
        // Update in cart manager
        CartManager.getInstance().updateItemQuantity(item.getProductId(), item.getSize(), item.getQuantity() + 1);

        // Reload cart items
        loadCartItems();
    }

    private void decreaseQuantity(CartItem item) {
        if (item.getQuantity() > 1) {
            // Update in cart manager
            CartManager.getInstance().updateItemQuantity(item.getProductId(), item.getSize(), item.getQuantity() - 1);

            // Reload cart items
            loadCartItems();
        } else {
            // Remove item if quantity becomes 0
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
                // Get selected payment method
                String paymentMethod = paymentMethodComboBox.getValue();
                if (paymentMethod == null) {
                    paymentMethod = "Cash"; // Default if none selected
                }

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
                    double subtotal = cartItems.stream().mapToDouble(CartItem::getSubtotal).sum();
                    double tax = subtotal * TAX_RATE;
                    double total = subtotal + tax;
                    stmt.setDouble(4, total);

                    // Set the selected payment method
                    stmt.setString(5, paymentMethod);

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
                alert.setContentText("Order placed for table with payment method: " + paymentMethod);
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
                // Get selected payment method
                String paymentMethod = paymentMethodComboBox.getValue();
                if (paymentMethod == null) {
                    paymentMethod = "Cash"; // Default if none selected
                }

                // Find the existing order for this table
                int orderId;
                double currentTotal = 0;

                try (PreparedStatement stmt = connection.prepareStatement(
                        "SELECT id, total_price FROM orders WHERE table_id = ? AND status = 'Pending'")) {
                    stmt.setInt(1, tableId);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        orderId = rs.getInt("id");
                        currentTotal = rs.getDouble("total_price");
                    } else {
                        throw new SQLException("No existing pending order found for this table");
                    }
                }

                // Insert new order details
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

                // Update the total price and payment method
                double newItemsTotal = cartItems.stream().mapToDouble(CartItem::getSubtotal).sum();
                double tax = newItemsTotal * TAX_RATE;
                double newTotal = currentTotal + newItemsTotal + tax;

                try (PreparedStatement stmt = connection.prepareStatement(
                        "UPDATE orders SET total_price = ?, payment_method = ? WHERE id = ?")) {
                    stmt.setDouble(1, newTotal);
                    stmt.setString(2, paymentMethod);
                    stmt.setInt(3, orderId);
                    stmt.executeUpdate();
                }

                // Commit the transaction
                connection.commit();

                // Show success message
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Order Updated");
                alert.setHeaderText("Items added to existing order!");
                alert.setContentText("Order #" + orderId + " updated with payment method: " + paymentMethod);
                alert.showAndWait();

                // Clear the cart
                CartManager.getInstance().clearCart();
                loadCartItems();

            } catch (SQLException e) {
                // Roll back the transaction in case of error
                connection.rollback();
                System.err.println("Error adding to existing order: " + e.getMessage());
                e.printStackTrace();

                showAlert("Error adding to existing order: " + e.getMessage());
            } finally {
                // Reset auto-commit to default
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
                // Get selected payment method
                String paymentMethod = paymentMethodComboBox.getValue();
                if (paymentMethod == null) {
                    paymentMethod = "Cash"; // Default if none selected
                }

                // Insert into orders table with special table_id for takeaway (0)
                int orderId;
                try (PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO orders (user_id, table_id, status, total_price, payment_method) VALUES (?, 0, ?, ?, ?)",
                        PreparedStatement.RETURN_GENERATED_KEYS)) {

                    // Assuming user ID 1 for now, in a real app this would be the logged-in user
                    stmt.setInt(1, 1);
                    stmt.setString(2, "Takeaway");

                    // Calculate total
                    double total = cartItems.stream().mapToDouble(CartItem::getSubtotal).sum();
                    stmt.setDouble(3, total);

                    stmt.setString(4, paymentMethod); // Use selected payment method

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
                alert.setContentText("Order #" + orderId + " - Please wait for pickup.");
                alert.showAndWait();

                // Clear the cart
                CartManager.getInstance().clearCart();
                loadCartItems();

            } catch (SQLException e) {
                // If there's an error, roll back the transaction
                connection.rollback();
                System.err.println("Error creating takeaway order: " + e.getMessage());
                e.printStackTrace();

                // Show error message
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to place takeaway order");
                alert.setContentText("Database error: " + e.getMessage());
                alert.showAndWait();
            } finally {
                // Reset auto-commit to default
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Connection error: " + e.getMessage());
            e.printStackTrace();

            // Show error message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setHeaderText("Failed to connect to database");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
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