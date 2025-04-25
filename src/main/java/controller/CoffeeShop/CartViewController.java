package controller.CoffeeShop;

import helper.Alert;
import helper.CoffeeShop.CartManager;
import helper.ConnectDatabase;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.CoffeeShop.CartItem;

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
        cartTable.setTableMenuButtonVisible(false);
        cartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        setupTableColumns();
        setupEventHandlers();
        loadCartItems();
        updateCartSummary();
    }

    private void setupTableColumns() {
        itemColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        sizeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSize()));
        quantityColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        priceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getUnitPrice()).asObject());
        subtotalColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getSubtotal()).asObject());

        // Special formatting for size column to highlight combos
        sizeColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String size, boolean empty) {
                super.updateItem(size, empty);
                if (empty || size == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(size);
                    if (size.equals("COMBO")) {
                        setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                    setAlignment(Pos.CENTER);
                }
            }
        });

        centerAlignColumn(quantityColumn);

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
                setGraphic(empty ? null : buttonBox);
                setAlignment(Pos.CENTER);
            }
        });

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

    private void loadCartItems() {
        cartItems.clear();
        cartItems.addAll(CartManager.getInstance().getCartItems());
        cartItems.addAll(CartManager.getInstance().getComboItems());
        cartTable.setItems(cartItems);
        updateCartSummary();
    }

    private void updateCartSummary() {
        int totalItems = cartItems.stream().mapToInt(CartItem::getQuantity).sum();
        int comboCount = cartItems.stream()
                .filter(item -> item.getSize().equals("COMBO"))
                .mapToInt(CartItem::getQuantity)
                .sum();

        double subtotal = cartItems.stream().mapToDouble(CartItem::getSubtotal).sum();
        double tax = subtotal * TAX_RATE;
        double total = subtotal + tax;

        totalItemsLabel.setText(String.format("%d (including %d combos)", totalItems, comboCount));
        subtotalLabel.setText(currencyFormat.format(subtotal));
        taxLabel.setText(currencyFormat.format(tax));
        totalLabel.setText(currencyFormat.format(total));

        checkoutButton.setDisable(cartItems.isEmpty());
    }

    private void removeFromCart(CartItem item) {
        if (item.getSize().equals("COMBO")) {
            CartManager.getInstance().removeComboFromCart(item.getProductId());
        } else {
            CartManager.getInstance().removeFromCart(item.getProductId(), item.getSize());
        }
        loadCartItems();
    }

    private void increaseQuantity(CartItem item) {
        if (item.getSize().equals("COMBO")) {
            CartManager.getInstance().updateComboQuantity(item.getProductId(), item.getQuantity() + 1);
        } else {
            CartManager.getInstance().updateItemQuantity(item.getProductId(), item.getSize(), item.getQuantity() + 1);
        }
        loadCartItems();
    }

    private void decreaseQuantity(CartItem item) {
        if (item.getQuantity() > 1) {
            if (item.getSize().equals("COMBO")) {
                CartManager.getInstance().updateComboQuantity(item.getProductId(), item.getQuantity() - 1);
            } else {
                CartManager.getInstance().updateItemQuantity(item.getProductId(), item.getSize(), item.getQuantity() - 1);
            }
        } else {
            removeFromCart(item);
        }
        loadCartItems();
    }

    private void setupEventHandlers() {
        clearCartButton.setOnAction(event -> clearCart());
        checkoutButton.setOnAction(event -> checkout());
        continueShoppingButton.setOnAction(event -> goToMenu());
    }

    private void clearCart() {
        CartManager.getInstance().clearCart();
        loadCartItems();
    }

    private void goToMenu() {
        DashboardController parentController = (DashboardController)
                cartTable.getScene().getWindow().getUserData();
        if (parentController != null) {
            parentController.loadView("/com/example/manage_account/CoffeeShop/MenuView.fxml");
        }
    }

    public void refreshCart() {
        loadCartItems();
    }

    private void checkout() {
        if (cartItems.isEmpty()) {
            Alert.showAlert("Your cart is empty. Please add items before checking out.");
            return;
        }
        showCheckoutDialog();
    }

    private void showCheckoutDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Checkout");
        dialog.setHeaderText("Complete your order");

        ButtonType confirmButtonType = new ButtonType("Confirm Order", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> tableComboBox = new ComboBox<>();
        tableComboBox.setPromptText("Select a table (optional)");
        tableComboBox.getItems().add("Takeaway");
        loadTables(tableComboBox);

        ComboBox<String> paymentMethodComboBox = new ComboBox<>();
        paymentMethodComboBox.setPromptText("Select payment method (optional)");
        paymentMethodComboBox.getItems().addAll("Cash", "Card", "Mobile Payment");

        double subtotal = cartItems.stream().mapToDouble(CartItem::getSubtotal).sum();
        double tax = subtotal * TAX_RATE;
        double total = subtotal + tax;

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

        dialog.getDialogPane().setContent(content);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == confirmButtonType) {
            String selectedTable = tableComboBox.getValue();
            String paymentMethod = paymentMethodComboBox.getValue();
            processOrder(selectedTable, paymentMethod, total);
        }
    }

    private void loadTables(ComboBox<String> tableComboBox) {
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT id, table_name FROM tables WHERE status = 'available'");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String tableName = rs.getString("table_name");
                tableComboBox.getItems().add(tableName);
            }

        } catch (SQLException e) {
            Alert.showAlert("Error loading tables: " + e.getMessage());
        }
    }

    private void processOrder(String selectedTable, String paymentMethod, double total) {
        Connection conn = null;
        try {
            conn = ConnectDatabase.getConnection();
            conn.setAutoCommit(false);

            int orderId;
            Integer tableId = null;

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

            String orderQuery = "INSERT INTO orders (user_id, table_id, status, total_price, payment_method) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement orderStmt = conn.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS)) {
                orderStmt.setInt(1, 3); // Using user ID 3 as default employee
                orderStmt.setObject(2, tableId, Types.INTEGER);
                orderStmt.setString(3, "Pending");
                orderStmt.setDouble(4, total);
                orderStmt.setString(5, paymentMethod != null ? paymentMethod : "Unknown");

                orderStmt.executeUpdate();

                ResultSet generatedKeys = orderStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    orderId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }

            String detailQuery = "INSERT INTO order_detail (order_id, product_id, quantity, unit_price, is_combo) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement detailStmt = conn.prepareStatement(detailQuery)) {
                for (CartItem item : cartItems) {
                    detailStmt.setInt(1, orderId);
                    detailStmt.setInt(2, item.getProductId());
                    detailStmt.setInt(3, item.getQuantity());
                    detailStmt.setDouble(4, item.getUnitPrice());
                    detailStmt.setBoolean(5, item.getSize().equals("COMBO"));
                    detailStmt.addBatch();
                }
                detailStmt.executeBatch();
            }

            if (tableId != null) {
                String updateTableQuery = "UPDATE tables SET status = 'occupied' WHERE id = ?";
                try (PreparedStatement tableUpdateStmt = conn.prepareStatement(updateTableQuery)) {
                    tableUpdateStmt.setInt(1, tableId);
                    tableUpdateStmt.executeUpdate();
                }
            }

            conn.commit();
            CartManager.getInstance().clearCart();
            loadCartItems();

            Alert.showSuccess("Order #" + orderId + " has been placed successfully!");

            DashboardController parentController = (DashboardController)
                    cartTable.getScene().getWindow().getUserData();
            if (parentController != null) {
                parentController.loadView("/com/example/manage_account/CoffeeShop/OrdersView.fxml");
            }

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            Alert.showAlert("Error processing order: " + e.getMessage());
            e.printStackTrace();
        } finally {
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
}