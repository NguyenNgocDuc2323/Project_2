package controller.admin;

import helper.DB_Helper.Account_DB_Helper;
import helper.DB_Helper.Order_DB_Helper;
import helper.DB_Helper.Product_DB_Helper;
import helper.DB_Helper.Table_DB_Helper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.Admin.CustomerPurchase;
import model.Admin.OrderDetailDisplay;
import model.Admin.ProductSale;
import model.Order;
import model.OrderDetail;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class HomeController {
    @FXML private Label total_user, total_order, total_product, total_table;
    @FXML private TextField searchField;
    @FXML private ComboBox<Integer> dayCombo, monthCombo, yearCombo;
    @FXML private ComboBox<String> revenueGroupByCombo, customerGroupByCombo, productGroupByCombo;
    @FXML private StackPane popupRevenue, popupCustomers, popupProducts;
    @FXML private Label revenueInfo, customerInfo, productInfo;
    @FXML private TableView<OrderDetailDisplay> revenueTable, revenueTablePopup;
    @FXML private TableColumn<OrderDetailDisplay, String> categoryCol, productCol, categoryColPopup;
    @FXML private TableColumn<OrderDetailDisplay, Integer> quantityCol;
    @FXML private TableColumn<OrderDetailDisplay, Double> amountCol, amountColPopup;
    @FXML private TableColumn<OrderDetailDisplay, LocalDateTime> dateCol, dateColPopup;
    @FXML private TableView<CustomerPurchase> customerTable;
    @FXML private TableColumn<CustomerPurchase, Integer> customerIdCol;
    @FXML private TableColumn<CustomerPurchase, String> customerNameCol;
    @FXML private TableColumn<CustomerPurchase, Integer> orderCountCol;
    @FXML private TableColumn<CustomerPurchase, Double> totalSpentCol;
    @FXML private TableColumn<CustomerPurchase, LocalDateTime> lastOrderDateCol;
    @FXML private TableView<ProductSale> productTable;
    @FXML private TableColumn<ProductSale, LocalDateTime> timeCol;
    @FXML private TableColumn<ProductSale, Integer> productIdCol;
    @FXML private TableColumn<ProductSale, String> productNameCol;
    @FXML private TableColumn<ProductSale, String> productCategoryCol;
    @FXML private TableColumn<ProductSale, Integer> quantitySoldCol;
    @FXML private TableColumn<ProductSale, Double> productRevenueCol;
    @FXML private ComboBox<String> revenueGroupByPopupCombo, customerGroupByPopupCombo, productGroupByPopupCombo; // Added
    @FXML private VBox productContainer, customerContainer;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private final NumberFormat numberFormat = NumberFormat.getNumberInstance();

    @FXML
    public void initialize() {
        // Khởi tạo thống kê tổng quan
        int userCount = Account_DB_Helper.countAcc();
        int orderCount = Order_DB_Helper.countOrder();
        int productCount = Product_DB_Helper.countProducts();
        int tableCount = Table_DB_Helper.countTable();

        total_user.setText(String.valueOf(userCount));
        total_order.setText(String.valueOf(orderCount));
        total_product.setText(String.valueOf(productCount));
        total_table.setText(String.valueOf(tableCount));

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

        // Khởi tạo groupByCombo
        revenueGroupByCombo.setItems(FXCollections.observableArrayList("Day", "Month", "Year"));
        customerGroupByCombo.setItems(FXCollections.observableArrayList("Day", "Month", "Year"));
        productGroupByCombo.setItems(FXCollections.observableArrayList("Day", "Month", "Year"));
        revenueGroupByCombo.setValue("Day");
        customerGroupByCombo.setValue("Day");
        productGroupByCombo.setValue("Day");

        // Khởi tạo popup ComboBoxes
        revenueGroupByPopupCombo.setItems(FXCollections.observableArrayList("Day", "Month", "Year"));
        revenueGroupByPopupCombo.setValue("Day");
        customerGroupByPopupCombo.setItems(FXCollections.observableArrayList("Day", "Month", "Year"));
        customerGroupByPopupCombo.setValue("Day");
        productGroupByPopupCombo.setItems(FXCollections.observableArrayList("Day", "Month", "Year")); // Added
        productGroupByPopupCombo.setValue("Day"); // Added

        // Khởi tạo TableView và các cột
        initializeRevenueTable();
        initializeCustomerTable();
        initializeProductTable();
        loadSummaryData();

        // Tải dữ liệu revenue mặc định
        loadRevenueData(null, null, null, null);

        // Listener để tự động làm mới khi thay đổi groupBy trong popup
        revenueGroupByPopupCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (popupRevenue.isVisible()) {
                showRevenueLayout();
            }
        });
        customerGroupByPopupCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (popupCustomers.isVisible()) {
                showCustomerLayout();
            }
        });
        productGroupByPopupCombo.valueProperty().addListener((obs, oldVal, newVal) -> { // Added
            if (popupProducts.isVisible()) {
                showProductLayout();
            }
        });
        revenueGroupByCombo.setVisible(false);
        customerGroupByCombo.setVisible(false);
        productGroupByCombo.setVisible(false);

        revenueGroupByCombo.setManaged(false);
        customerGroupByCombo.setManaged(false);
        productGroupByCombo.setManaged(false);
    }

    private void initializeRevenueTable() {
        // revenueTable (chính)
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        productCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        // revenueTablePopup
        categoryColPopup.setCellValueFactory(new PropertyValueFactory<>("category"));
        amountColPopup.setCellValueFactory(new PropertyValueFactory<>("amount"));
        dateColPopup.setCellValueFactory(new PropertyValueFactory<>("date"));

        // Định dạng cột amount
        TableColumn<OrderDetailDisplay, Double>[] amountColumns = new TableColumn[]{amountCol, amountColPopup};
        for (TableColumn<OrderDetailDisplay, Double> col : amountColumns) {
            col.setCellFactory(tc -> new TableCell<>() {
                @Override
                protected void updateItem(Double amount, boolean empty) {
                    super.updateItem(amount, empty);
                    setText(empty || amount == null ? null : currencyFormat.format(amount));
                }
            });
        }

        // Định dạng cột date
        TableColumn<OrderDetailDisplay, LocalDateTime>[] dateColumns = new TableColumn[]{dateCol, dateColPopup};
        for (TableColumn<OrderDetailDisplay, LocalDateTime> col : dateColumns) {
            col.setCellFactory(tc -> new TableCell<>() {
                @Override
                protected void updateItem(LocalDateTime date, boolean empty) {
                    super.updateItem(date, empty);
                    if (empty || date == null) {
                        setText(null);
                        return;
                    }
                    String groupBy = revenueGroupByPopupCombo.getValue() != null ? revenueGroupByPopupCombo.getValue().toLowerCase() : "day";
                    SimpleDateFormat dateFormat;
                    switch (groupBy) {
                        case "month":
                            dateFormat = new SimpleDateFormat("MM/yyyy");
                            break;
                        case "year":
                            dateFormat = new SimpleDateFormat("yyyy");
                            break;
                        default:
                            dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    }
                    setText(dateFormat.format(Timestamp.valueOf(date)));
                }
            });
        }
    }

    private void initializeCustomerTable() {
        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        orderCountCol.setCellValueFactory(new PropertyValueFactory<>("orderCount"));
        totalSpentCol.setCellValueFactory(new PropertyValueFactory<>("totalSpent"));
        lastOrderDateCol.setCellValueFactory(new PropertyValueFactory<>("lastOrderDate"));

        totalSpentCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                setText(empty || amount == null ? null : currencyFormat.format(amount));
            }
        });

        lastOrderDateCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                    return;
                }
                String groupBy = customerGroupByPopupCombo.getValue() != null ? customerGroupByPopupCombo.getValue().toLowerCase() : "day";
                SimpleDateFormat dateFormat;
                switch (groupBy) {
                    case "month":
                        dateFormat = new SimpleDateFormat("MM/yyyy");
                        break;
                    case "year":
                        dateFormat = new SimpleDateFormat("yyyy");
                        break;
                    default:
                        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                }
                setText(dateFormat.format(Timestamp.valueOf(date)));
            }
        });
    }

    private void initializeProductTable() {
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        productIdCol.setCellValueFactory(new PropertyValueFactory<>("productId"));
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        productCategoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        quantitySoldCol.setCellValueFactory(new PropertyValueFactory<>("totalSold"));
        productRevenueCol.setCellValueFactory(new PropertyValueFactory<>("totalRevenue"));

        quantitySoldCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Integer quantity, boolean empty) {
                super.updateItem(quantity, empty);
                setText(empty || quantity == null ? null : numberFormat.format(quantity));
            }
        });

        productRevenueCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                setText(empty || amount == null ? null : currencyFormat.format(amount));
            }
        });

        // Explicitly specify the generic types for TableCell
        timeCol.setCellFactory(tc -> new TableCell<ProductSale, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime time, boolean empty) {
                super.updateItem(time, empty);
                if (empty || time == null) {
                    setText(null);
                    return;
                }
                String groupBy = productGroupByPopupCombo.getValue() != null ? productGroupByPopupCombo.getValue().toLowerCase() : "day";
                SimpleDateFormat dateFormat;
                switch (groupBy) {
                    case "month":
                        dateFormat = new SimpleDateFormat("MM/yyyy");
                        break;
                    case "year":
                        dateFormat = new SimpleDateFormat("yyyy");
                        break;
                    default:
                        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                }
                setText(dateFormat.format(Timestamp.valueOf(time)));
            }
        });
    }

    private void loadSummaryData() {
        try {
            double totalRevenue = Order_DB_Helper.getTotalRevenue();
            int totalCustomers = Account_DB_Helper.getTotalCustomers();
            int totalProductsSold = Order_DB_Helper.getTotalProductsSold();

            revenueInfo.setText("Tổng doanh thu: " + currencyFormat.format(totalRevenue));
            customerInfo.setText("Số người mua: " + numberFormat.format(totalCustomers));
            productInfo.setText("Sản phẩm bán được: " + numberFormat.format(totalProductsSold));
        } catch (SQLException e) {
            showAlert("Error", "Không thể tải dữ liệu tổng quan.", e.getMessage());
        }
    }

    @FXML
    private void handdleSearch() {
        Integer day = dayCombo.getValue();
        Integer month = monthCombo.getValue();
        Integer year = yearCombo.getValue();
        String searchTerm = searchField.getText();

        // Tải lại dữ liệu cho revenueTable
        loadRevenueData(day, month, year, searchTerm);

        // Tải lại dữ liệu cho các popup nếu đang mở
        if (popupRevenue.isVisible()) {
            showRevenueLayout();
        }
        if (popupCustomers.isVisible()) {
            showCustomerLayout();
        }
        if (popupProducts.isVisible()) {
            showProductLayout();
        }
    }

    private void loadRevenueData(Integer day, Integer month, Integer year, String searchTerm) {
        List<Order> orders = Order_DB_Helper.getAllOrdersWithDetails();
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

                    if (!productName.contains(lowerTerm) &&
                            !categoryName.contains(lowerTerm) &&
                            !quantityStr.contains(lowerTerm) &&
                            !amountStr.contains(lowerTerm) &&
                            !dateStr.contains(lowerTerm)) {
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
        revenueTable.setItems(FXCollections.observableArrayList(displayList));
    }

    @FXML
    private void onSearchFieldKeyReleased(javafx.scene.input.KeyEvent keyEvent) {
        handdleSearch();
    }

    @FXML
    private void showRevenueLayout() {
        try {
            String groupBy = revenueGroupByPopupCombo.getValue() != null ? revenueGroupByPopupCombo.getValue().toLowerCase() : "day";
            Integer day = dayCombo.getValue();
            Integer month = monthCombo.getValue();
            Integer year = yearCombo.getValue();

            List<OrderDetailDisplay> details = Order_DB_Helper.getRevenueData(groupBy, day, month, year);

            if (details.isEmpty()) {
                showAlert("Warning", "Không có dữ liệu doanh thu cho khoảng thời gian đã chọn.", "");
            }

            double totalRevenue = details.stream().mapToDouble(OrderDetailDisplay::getAmount).sum();
            revenueInfo.setText("Tổng doanh thu: " + currencyFormat.format(totalRevenue));

            revenueTablePopup.setItems(FXCollections.observableArrayList(details));

            popupRevenue.setVisible(true);
            popupRevenue.toFront();
        } catch (SQLException e) {
            showAlert("Error", "Không thể tải dữ liệu doanh thu.", e.getMessage());
        }
    }

    @FXML
    private void showCustomerLayout() {
        try {
            String groupBy = customerGroupByPopupCombo.getValue() != null ? customerGroupByPopupCombo.getValue().toLowerCase() : "day";
            Integer day = dayCombo.getValue();
            Integer month = monthCombo.getValue();
            Integer year = yearCombo.getValue();

            List<CustomerPurchase> customers = Account_DB_Helper.getCustomerData(groupBy, day, month, year);

            if (customers.isEmpty()) {
                showAlert("Warning", "Không có dữ liệu khách hàng cho khoảng thời gian đã chọn.", "");
            }

            double totalSpent = customers.stream().mapToDouble(CustomerPurchase::getTotalSpent).sum();
            customerInfo.setText("Số người mua: " + numberFormat.format(customers.size()) + " | Tổng chi tiêu: " + currencyFormat.format(totalSpent));

            customerTable.setItems(FXCollections.observableArrayList(customers));

            popupCustomers.setVisible(true);
            popupCustomers.toFront();
        } catch (SQLException e) {
            showAlert("Error", "Không thể tải dữ liệu khách hàng.", e.getMessage());
        }
    }

    @FXML
    private void showProductLayout() {
        try {
            String groupBy = productGroupByPopupCombo.getValue() != null ? productGroupByPopupCombo.getValue().toLowerCase() : "day";
            Integer day = dayCombo.getValue();
            Integer month = monthCombo.getValue();
            Integer year = yearCombo.getValue();

            List<ProductSale> products = Product_DB_Helper.getProductData(groupBy, day, month, year);

            if (products.isEmpty()) {
                showAlert("Warning", "Không có dữ liệu sản phẩm cho khoảng thời gian đã chọn.", "");
            }

            int totalProductsSold = products.stream().mapToInt(ProductSale::getTotalSold).sum();
            double totalRevenue = products.stream().mapToDouble(ProductSale::getTotalRevenue).sum();
            productInfo.setText("Sản phẩm bán được: " + numberFormat.format(totalProductsSold) + " | Tổng doanh thu: " + currencyFormat.format(totalRevenue));

            productTable.setItems(FXCollections.observableArrayList(products));

            popupProducts.setVisible(true);
            popupProducts.toFront();
        } catch (SQLException e) {
            showAlert("Error", "Không thể tải dữ liệu sản phẩm.", e.getMessage());
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
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}