    package controller.admin;

    import helper.DB_Helper.Account_DB_Helper;
    import helper.DB_Helper.Order_DB_Helper;
    import helper.DB_Helper.Product_DB_Helper;
    import helper.DB_Helper.Table_DB_Helper;
    import javafx.collections.FXCollections;
    import javafx.collections.ObservableList;
    import javafx.fxml.FXML;

    import javafx.geometry.Pos;
    import javafx.scene.control.*;
    import javafx.scene.control.cell.PropertyValueFactory;
    import javafx.scene.layout.HBox;
    import javafx.scene.layout.StackPane;
    import javafx.scene.layout.VBox;
    import javafx.scene.paint.Color;
    import model.Admin.CustomerPurchase;
    import model.Admin.OrderDetailDisplay;
    import model.Admin.OrderSale;
    import model.Admin.ProductSale;
    import model.Order;
    import model.OrderDetail;

    import java.awt.event.KeyEvent;
    import java.sql.SQLException;
    import java.sql.Timestamp;
    import java.text.NumberFormat;
    import java.text.SimpleDateFormat;
    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.time.format.DateTimeFormatter;
    import java.util.*;

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

        @FXML private StackPane popupRevenue;
        @FXML private StackPane popupCustomers;
        @FXML private StackPane popupProducts;

        @FXML private Label revenueInfo;
        @FXML private Label customerInfo;
        @FXML private Label productInfo;
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



        @FXML private TableColumn<OrderSale, Integer> orderIdCol;
        @FXML private TableColumn<OrderSale, String> orderDateCol;
        @FXML private TableColumn<OrderSale, String> customerNameCol;

        @FXML private TableView<CustomerPurchase> customerTable;
        @FXML private TableColumn<CustomerPurchase, Integer> customerIdCol;
        @FXML private TableColumn<CustomerPurchase, Integer> orderCountCol;
        @FXML private TableColumn<CustomerPurchase, Double> totalSpentCol;

        @FXML private TableView<ProductSale> productTable;
        @FXML private TableColumn<ProductSale, Integer> productIdCol;
        @FXML private TableColumn<ProductSale, String> productNameCol;
        @FXML private TableColumn<ProductSale, String> productCategoryCol;
        @FXML private TableColumn<ProductSale, Integer> quantitySoldCol;
        @FXML private TableColumn<ProductSale, Double> productRevenueCol;

        @FXML private ComboBox<String> revenueSortField;
        @FXML private ComboBox<String> revenueSortOrder;
        @FXML private ComboBox<String> customerSortField;
        @FXML private ComboBox<String> customerSortOrder;
        @FXML private ComboBox<String> productSortField;
        @FXML private ComboBox<String> productSortOrder;

        @FXML
        private TableView<OrderDetailDisplay> revenueTablePopup;
        @FXML
        private TableColumn<OrderDetailDisplay, String> categoryColPopup;
        @FXML
        private TableColumn<OrderDetailDisplay, String> productColPopup;
        @FXML
        private TableColumn<OrderDetailDisplay, Integer> quantityColPopup;
        @FXML
        private TableColumn<OrderDetailDisplay, Double> amountColPopup;
        @FXML
        private TableColumn<OrderDetailDisplay, LocalDateTime> dateColPopup;
        @FXML
        private HBox sortBoxProductHidden;

        @FXML
        private HBox sortBoxCustomerHidden;

        @FXML
        private HBox sortBoxRevenueHidden;

        public void hideSortProductBox() {
            sortBoxProductHidden.setVisible(false);
            sortBoxProductHidden.setManaged(false);
        }

        public void hideSortCustomerBox() {
            sortBoxCustomerHidden.setVisible(false);
            sortBoxCustomerHidden.setManaged(false);
        }

        public void hideSortRevenueBox() {
            sortBoxRevenueHidden.setVisible(false);
            sortBoxRevenueHidden.setManaged(false);
        }
        private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        private NumberFormat numberFormat = NumberFormat.getNumberInstance();

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
            initializeSortOptions();
            initializeRevenueTable();
            initializeCustomerTable();
            initializeProductTable();
            loadSummaryData();
            hideSortProductBox();
            hideSortCustomerBox();
            hideSortRevenueBox();
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
        private void initializeSortOptions() {
            revenueSortField.getItems().addAll("order_date", "total_amount", "name");
            revenueSortField.setValue("order_date");
            revenueSortOrder.getItems().addAll("ASC", "DESC");
            revenueSortOrder.setValue("DESC");

            customerSortField.getItems().addAll("name", "order_count", "total_spent");
            customerSortField.setValue("total_spent");
            customerSortOrder.getItems().addAll("ASC", "DESC");
            customerSortOrder.setValue("DESC");

            productSortField.getItems().addAll("name", "category", "total_sold", "total_revenue");
            productSortField.setValue("total_sold");
            productSortOrder.getItems().addAll("ASC", "DESC");
            productSortOrder.setValue("DESC");
        }

        private void initializeRevenueTable() {
            // Configure revenueTable (main view)
            categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
            productCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
            quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
            dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

            // Configure revenueTablePopup (popup view)
            categoryColPopup.setCellValueFactory(new PropertyValueFactory<>("category"));
            productColPopup.setCellValueFactory(new PropertyValueFactory<>("productName"));
            quantityColPopup.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            amountColPopup.setCellValueFactory(new PropertyValueFactory<>("amount"));
            dateColPopup.setCellValueFactory(new PropertyValueFactory<>("date"));

            // Amount column formatting (shared for both tables)
            TableColumn<OrderDetailDisplay, Double>[] amountColumns = new TableColumn[]{amountCol, amountColPopup};
            for (TableColumn<OrderDetailDisplay, Double> col : amountColumns) {
                col.setCellFactory(tc -> new TableCell<OrderDetailDisplay, Double>() {
                    @Override
                    protected void updateItem(Double amount, boolean empty) {
                        super.updateItem(amount, empty);
                        if (empty || amount == null) {
                            setText(null);
                        } else {
                            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                            setText(currencyFormat.format(amount));
                        }
                    }
                });
            }

            // Date column formatting (shared for both tables)
            TableColumn<OrderDetailDisplay, LocalDateTime>[] dateColumns = new TableColumn[]{dateCol, dateColPopup};
            for (TableColumn<OrderDetailDisplay, LocalDateTime> col : dateColumns) {
                col.setCellFactory(tc -> new TableCell<OrderDetailDisplay, LocalDateTime>() {
                    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                    @Override
                    protected void updateItem(LocalDateTime date, boolean empty) {
                        super.updateItem(date, empty);
                        if (empty || date == null) {
                            setText(null);
                        } else {
                            setText(dateFormat.format(Timestamp.valueOf(date)));
                        }
                    }
                });
            }
        }

        private void initializeCustomerTable() {
            customerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
            customerNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            orderCountCol.setCellValueFactory(new PropertyValueFactory<>("orderCount"));
            totalSpentCol.setCellValueFactory(new PropertyValueFactory<>("totalSpent"));

            totalSpentCol.setCellFactory(tc -> new TableCell<CustomerPurchase, Double>() {
                @Override
                protected void updateItem(Double amount, boolean empty) {
                    super.updateItem(amount, empty);
                    if (empty || amount == null) {
                        setText(null);
                    } else {
                        setText(currencyFormat.format(amount));
                    }
                }
            });
        }

        private void initializeProductTable() {
            productIdCol.setCellValueFactory(new PropertyValueFactory<>("productId"));
            productNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            productCategoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
            quantitySoldCol.setCellValueFactory(new PropertyValueFactory<>("totalSold"));
            productRevenueCol.setCellValueFactory(new PropertyValueFactory<>("totalRevenue"));

            quantitySoldCol.setCellFactory(tc -> new TableCell<ProductSale, Integer>() {
                @Override
                protected void updateItem(Integer quantity, boolean empty) {
                    super.updateItem(quantity, empty);
                    if (empty || quantity == null) {
                        setText(null);
                    } else {
                        setText(numberFormat.format(quantity));
                    }
                }
            });

            productRevenueCol.setCellFactory(tc -> new TableCell<ProductSale, Double>() {
                @Override
                protected void updateItem(Double amount, boolean empty) {
                    super.updateItem(amount, empty);
                    if (empty || amount == null) {
                        setText(null);
                    } else {
                        setText(currencyFormat.format(amount));
                    }
                }
            });
        }

        private void loadSummaryData() {
            try {
                double totalRevenue = Order_DB_Helper.getTotalRevenue();
                int totalCustomers = Account_DB_Helper.getTotalCustomers();
                int totalProductsSold = Order_DB_Helper.getTotalProductsSold();

                revenueInfo.setText("Total revenue:" + currencyFormat.format(totalRevenue));
                customerInfo.setText("Total number of customers:" + numberFormat.format(totalCustomers));
                productInfo.setText("Total products sold:" + numberFormat.format(totalProductsSold));
            } catch (SQLException e) {
                showAlert("Error", "Unable to load overview data.", e.getMessage());
            }
        }


        @FXML
        private void showRevenueLayout() {
            try {
                String sortBy = revenueSortField.getValue();
                String sortOrder = revenueSortOrder.getValue();

                ObservableList<OrderDetailDisplay> details = FXCollections.observableArrayList(
                        Order_DB_Helper.getRevenueData(sortBy, sortOrder)
                );
                revenueTablePopup.setItems(details);

                popupRevenue.setVisible(true);
                popupRevenue.toFront();
            } catch (SQLException e) {
                showAlert("Error", "Unable to load overview data.", e.getMessage());
            }
        }

        @FXML
        private void showCustomerLayout() {
            try {
                String sortBy = customerSortField.getValue();
                String sortOrder = customerSortOrder.getValue();

                ObservableList<CustomerPurchase> customers = FXCollections.observableArrayList(
                        Account_DB_Helper.getCustomerData(sortBy, sortOrder)
                );
                customerTable.setItems(customers);

                TableColumn<CustomerPurchase, Number> idColumn = new TableColumn<>("Customer ID");
                idColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));

                TableColumn<CustomerPurchase, String> nameColumn = new TableColumn<>("Name");
                nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

                TableColumn<CustomerPurchase, Number> orderCountColumn = new TableColumn<>("Order Count");
                orderCountColumn.setCellValueFactory(new PropertyValueFactory<>("orderCount"));

                TableColumn<CustomerPurchase, Number> totalSpentColumn = new TableColumn<>("Total Spent");
                totalSpentColumn.setCellValueFactory(new PropertyValueFactory<>("totalSpent"));

                TableColumn<CustomerPurchase, LocalDateTime> dateColumn = new TableColumn<>("Last Order Date");
                dateColumn.setCellValueFactory(new PropertyValueFactory<>("lastOrderDate"));

                dateColumn.setCellFactory(column -> new TableCell<>() {
                    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    @Override
                    protected void updateItem(LocalDateTime item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(formatter.format(item));
                        }
                    }
                });

                customerTable.getColumns().setAll(idColumn, nameColumn, orderCountColumn, totalSpentColumn, dateColumn);

                popupCustomers.setVisible(true);
                popupCustomers.toFront();
            } catch (SQLException e) {
                showAlert("Error", "Unable to load customer data.", e.getMessage());
            }
        }
        @FXML
        private void showProductLayout() {
            try {
                String sortBy = productSortField.getValue();
                String sortOrder = productSortOrder.getValue();

                ObservableList<ProductSale> products = FXCollections.observableArrayList(
                        Product_DB_Helper.getProductData(sortBy, sortOrder)
                );
                productTable.setItems(products);

                popupProducts.setVisible(true);
                popupProducts.toFront();
            } catch (SQLException e) {
                showAlert("Error", "Unable to load product data.", e.getMessage());
            }
        }

        @FXML
        private void closePopupRevenue() {
            popupRevenue.setVisible(false);
        }

        @FXML
        private void closePopupCustomers() {
            popupCustomers.setVisible(false);
        }

        @FXML
        private void closePopupProducts() {
            popupProducts.setVisible(false);
        }

        private void showAlert(String title, String header, String content) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        }
    }
