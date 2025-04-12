package controller.CoffeeShop;

import helper.CoffeeShop.CartManager;
import helper.ConnectDatabase;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.CoffeeShop.CartItem;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.Optional;
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

    private ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    private final double TAX_RATE = 0.08; // 8% tax
    private DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Disable row selection column/indicator
        cartTable.setTableMenuButtonVisible(false);
        cartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Initialize the table columns
        setupTableColumns();

        // Set up event handlers
        setupEventHandlers();

        // Load items from cart
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

        // Center align columns
        centerAlignColumn(sizeColumn);
        centerAlignColumn(quantityColumn);

        // Format price columns to display currency
        priceColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(price));
                }
                setAlignment(Pos.CENTER);
            }
        });

        subtotalColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(price));
                }
                setAlignment(Pos.CENTER);
            }
        });

        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button removeButton = new Button("Remove");
            private final Button increaseButton = new Button("+");
            private final Button decreaseButton = new Button("-");
            private final HBox buttonBox = new HBox(5, decreaseButton, increaseButton, removeButton);

            {
                removeButton.getStyleClass().add("remove-button");
                increaseButton.getStyleClass().add("quantity-button");
                decreaseButton.getStyleClass().add("quantity-button");

                buttonBox.setAlignment(Pos.CENTER);

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
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonBox);
                }
                setAlignment(Pos.CENTER);
            }
        });

        // Set the items
        cartTable.setItems(cartItems);
    }

    private <T> void centerAlignColumn(TableColumn<CartItem, T> column) {
        column.setCellFactory(col -> {
            TableCell<CartItem, T> cell = new TableCell<>() {
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

    private void setupEventHandlers() {
        clearCartButton.setOnAction(event -> clearCart());
        checkoutButton.setOnAction(event -> checkout());
        continueShoppingButton.setOnAction(event -> goToMenu());
    }

    private void goToMenu() {
        // Use the DashboardController's navigation to go to the menu view
        DashboardController parentController = (DashboardController)
                cartTable.getScene().getWindow().getUserData();
        if (parentController != null) {
            parentController.loadView("/com/example/manage_account/CoffeeShop/MenuView.fxml");
        }
    }

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
        subtotalLabel.setText(currencyFormat.format(subtotal));
        taxLabel.setText(currencyFormat.format(tax));
        totalLabel.setText(currencyFormat.format(total));

        // Disable checkout if cart is empty
        checkoutButton.setDisable(cartItems.isEmpty());
    }

    private void removeFromCart(CartItem item) {
        // Remove from cart manager first
        CartManager.getInstance().removeFromCart(item.getProductId(), item.getSize());

        // Then reload cart items from the updated cart
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
            CartManager.getInstance().updateItemQuantity(item.getProductId(), item.getSize(), item.getQuantity() - 1);
        } else {
            removeFromCart(item);
        }
        loadCartItems();
    }

    private void clearCart() {
        CartManager.getInstance().clearCart();
        loadCartItems();
    }

    public void refreshCart() {
        loadCartItems();
    }

    private void checkout() {
        if (cartItems.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Your cart is empty. Please add items before checking out.");
            return;
        }

        // Show checkout dialog to select table and payment method
        showCheckoutDialog();
    }

    private void showCheckoutDialog() {
        // Create a custom dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Checkout");
        dialog.setHeaderText("Complete your order");

        // Set the button types
        ButtonType confirmButtonType = new ButtonType("Confirm Order", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        // Create a VBox to hold the form controls
        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER_LEFT);

        // Add table selection (optional)
        ComboBox<String> tableComboBox = new ComboBox<>();
        tableComboBox.setPromptText("Select a table (optional)");
        tableComboBox.getItems().add("Takeaway");

        // Load tables from database
        loadTables(tableComboBox);

        // Add payment method selection (optional)
        ComboBox<String> paymentMethodComboBox = new ComboBox<>();
        paymentMethodComboBox.setPromptText("Select payment method (optional)");
        paymentMethodComboBox.getItems().addAll("Cash", "Card", "Mobile Payment");

        // Calculate the totals
        double subtotal = cartItems.stream().mapToDouble(CartItem::getSubtotal).sum();
        double tax = subtotal * TAX_RATE;
        double total = subtotal + tax;

        // Add total summary
        Label totalLabel = new Label("Total amount: " + currencyFormat.format(total));
        totalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        content.getChildren().addAll(
                new Label("Table (optional):"),
                tableComboBox,
                new Label("Payment Method (optional):"),
                paymentMethodComboBox,
                new Separator(),
                totalLabel
        );

        // Set the content
        dialog.getDialogPane().setContent(content);

        // Process results
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == confirmButtonType) {
            // Get the selected table and payment method (can be null)
            String selectedTable = tableComboBox.getValue();
            String paymentMethod = paymentMethodComboBox.getValue();

            // Process the order - both fields can be null
            processOrder(selectedTable, paymentMethod, total);
        }
    }

    private void loadTables(ComboBox<String> tableComboBox) {
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id, table_name FROM tables WHERE status = 'available'");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String tableName = rs.getString("table_name");
                tableComboBox.getItems().add(tableName);
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error loading tables: " + e.getMessage());
        }
    }

    private void processOrder(String selectedTable, String paymentMethod, double total) {
        Connection conn = null;
        try {
            conn = ConnectDatabase.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Insert into orders table
            int orderId;
            Integer tableId = null; // Use Integer to allow null values

            // Get table ID only if a specific table was selected (not takeaway or null)
            if (selectedTable != null && !selectedTable.equals("Takeaway")) {
                String tableQuery = "SELECT id FROM tables WHERE table_name = ?";
                try (PreparedStatement tableStmt = conn.prepareStatement(tableQuery)) {
                    tableStmt.setString(1, selectedTable);
                    ResultSet rs = tableStmt.executeQuery();
                    if (rs.next()) {
                        tableId = rs.getInt("id");
                    }
                }
            }

            // Insert the order
            String orderQuery = "INSERT INTO orders (user_id, table_id, status, total_price, payment_method) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement orderStmt = conn.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS)) {
                // Using user ID 3 as default employee
                orderStmt.setInt(1, 3);

                // Handle table_id (can be null)
                if (tableId != null) {
                    orderStmt.setInt(2, tableId);
                } else {
                    orderStmt.setNull(2, Types.INTEGER);
                }

                orderStmt.setString(3, "Pending");
                orderStmt.setDouble(4, total);

                // Handle payment_method (can be null/"Unknown")
                if (paymentMethod != null) {
                    orderStmt.setString(5, paymentMethod);
                } else {
                    orderStmt.setString(5, "Unknown");
                }

                orderStmt.executeUpdate();

                // Get the generated order ID
                ResultSet generatedKeys = orderStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    orderId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }

            // Insert order details
            String detailQuery = "INSERT INTO order_detail (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
            try (PreparedStatement detailStmt = conn.prepareStatement(detailQuery)) {
                for (CartItem item : cartItems) {
                    detailStmt.setInt(1, orderId);
                    detailStmt.setInt(2, item.getProductId());
                    detailStmt.setInt(3, item.getQuantity());
                    detailStmt.setDouble(4, item.getUnitPrice());
                    detailStmt.addBatch();
                }
                detailStmt.executeBatch();
            }

            // Update table status only if a specific table was selected
            if (tableId != null) {
                String updateTableQuery = "UPDATE tables SET status = 'occupied' WHERE id = ?";
                try (PreparedStatement tableUpdateStmt = conn.prepareStatement(updateTableQuery)) {
                    tableUpdateStmt.setInt(1, tableId);
                    tableUpdateStmt.executeUpdate();
                }
            }

            // Commit the transaction
            conn.commit();

            // Clear the cart after successful order
            CartManager.getInstance().clearCart();
            loadCartItems();

            // Show success message with order ID
            showAlert(Alert.AlertType.INFORMATION, "Order #" + orderId + " has been placed successfully!");

            // Navigate to orders view
            DashboardController parentController = (DashboardController)
                    cartTable.getScene().getWindow().getUserData();
            if (parentController != null) {
                parentController.loadView("/com/example/manage_account/CoffeeShop/OrdersView.fxml");
            }

        } catch (SQLException e) {
            // Rollback in case of error
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            showAlert(Alert.AlertType.ERROR, "Error processing order: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Reset auto-commit and close connection
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertType == Alert.AlertType.INFORMATION ? "Success" : "Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}