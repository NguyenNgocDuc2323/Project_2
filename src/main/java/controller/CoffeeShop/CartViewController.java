package controller.CoffeeShop;

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
                     "SELECT table_name FROM tables WHERE status = 'Available'");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                tableNames.add(resultSet.getString("table_name"));
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
    private void
    loadCartItems() {
        // Sample data
        cartItems.add(new CartItem(1, "Espresso", "M", 1, 50000));
        cartItems.add(new CartItem(2, "Cappuccino", "L", 2, 60000));
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
        cartItems.clear();
        updateCartSummary();
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

        // In a real application, this would create an order in the database
        // and transition to a confirmation or payment screen
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Order Placed");
        alert.setHeaderText("Your order has been placed successfully!");
        alert.setContentText("Your order will be served to table: " + tableComboBox.getValue());
        alert.showAndWait();

        // Clear the cart after successful checkout
        clearCart();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}