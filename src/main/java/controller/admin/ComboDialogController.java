package controller.admin;

import database.CoffeeDAO;
import helper.Alert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.CoffeeShop.Coffee;
import model.CoffeeShop.ComboProduct;
import service.ComboService;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class ComboDialogController implements Initializable {

    @FXML
    private TextField nameField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextField discountPercentField;

    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    private TableView<Coffee> availableProductsTable;

    @FXML
    private TableColumn<Coffee, String> availableNameColumn;

    @FXML
    private TableColumn<Coffee, Double> availablePriceColumn;

    @FXML
    private TableView<Coffee> selectedProductsTable;

    @FXML
    private TableColumn<Coffee, String> selectedNameColumn;

    @FXML
    private TableColumn<Coffee, Double> selectedPriceColumn;

    @FXML
    private Button addProductButton;

    @FXML
    private Button removeProductButton;

    @FXML
    private Label originalPriceLabel;

    @FXML
    private Label finalPriceLabel;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private String mode;
    private ComboProduct combo;
    private CoffeeDAO coffeeDAO;
    private ComboService comboService;
    private Consumer<Boolean> onSavedCallback;
    private DecimalFormat decimalFormat;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        coffeeDAO = new CoffeeDAO();
        comboService = new ComboService();
        decimalFormat = new DecimalFormat("#,##0.00");

        // Thiết lập validation cho trường phần trăm giảm giá
        discountPercentField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                discountPercentField.setText(oldValue);
            }
        });

        // Thiết lập combo box trạng thái
        statusComboBox.setItems(FXCollections.observableArrayList("Active", "Inactive"));
        statusComboBox.getSelectionModel().selectFirst();

        // Thiết lập các cột cho bảng sản phẩm có sẵn
        availableNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        availablePriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        availablePriceColumn.setCellFactory(column -> new TableCell<Coffee, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText("$" + decimalFormat.format(price));
                }
            }
        });

        // Thiết lập các cột cho bảng sản phẩm đã chọn
        selectedNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        selectedPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        selectedPriceColumn.setCellFactory(column -> new TableCell<Coffee, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText("$" + decimalFormat.format(price));
                }
            }
        });

        // Tải danh sách sản phẩm có sẵn
        loadAvailableProducts();

        // Thiết lập sự kiện cho các nút
        setupEventHandlers();
    }

    private void loadAvailableProducts() {
        try {
            ObservableList<Coffee> coffeeList = coffeeDAO.getAllCoffee();
            // Chỉ hiển thị các sản phẩm đang active
            coffeeList = coffeeList.filtered(coffee -> coffee.getStatus() == 1);
            availableProductsTable.setItems(coffeeList);
        } catch (Exception e) {
            Alert.showAlert("Lỗi khi tải danh sách sản phẩm: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupEventHandlers() {
        // Sự kiện khi nhấn nút thêm sản phẩm
        addProductButton.setOnAction(event -> {
            Coffee selectedCoffee = availableProductsTable.getSelectionModel().getSelectedItem();
            if (selectedCoffee != null) {
                // Kiểm tra xem sản phẩm đã được thêm vào combo chưa
                boolean alreadyAdded = false;
                for (Coffee coffee : selectedProductsTable.getItems()) {
                    if (coffee.getId() == selectedCoffee.getId()) {
                        alreadyAdded = true;
                        break;
                    }
                }

                if (!alreadyAdded) {
                    selectedProductsTable.getItems().add(selectedCoffee);
                    updatePriceLabels();
                } else {
                    Alert.showAlert("Sản phẩm này đã được thêm vào combo");
                }
            } else {
                Alert.showAlert("Vui lòng chọn một sản phẩm để thêm vào combo");
            }
        });

        // Sự kiện khi nhấn nút xóa sản phẩm
        removeProductButton.setOnAction(event -> {
            Coffee selectedCoffee = selectedProductsTable.getSelectionModel().getSelectedItem();
            if (selectedCoffee != null) {
                selectedProductsTable.getItems().remove(selectedCoffee);
                updatePriceLabels();
            } else {
                Alert.showAlert("Vui lòng chọn một sản phẩm để xóa khỏi combo");
            }
        });

        // Sự kiện khi thay đổi phần trăm giảm giá
        discountPercentField.textProperty().addListener((observable, oldValue, newValue) -> {
            updatePriceLabels();
        });
    }

    private void updatePriceLabels() {
        double originalPrice = 0.0;
        for (Coffee coffee : selectedProductsTable.getItems()) {
            originalPrice += coffee.getPrice();
        }

        double discountPercent = 5.0; // Mặc định 5%
        try {
            discountPercent = Double.parseDouble(discountPercentField.getText());
            // Đảm bảo giảm giá nằm trong khoảng 5-15%
            if (discountPercent < 5.0) discountPercent = 5.0;
            if (discountPercent > 15.0) discountPercent = 15.0;
        } catch (NumberFormatException e) {
            // Giữ giá trị mặc định
        }

        double finalPrice = originalPrice * (1 - discountPercent / 100.0);

        originalPriceLabel.setText("$" + decimalFormat.format(originalPrice));
        finalPriceLabel.setText("$" + decimalFormat.format(finalPrice));
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setCombo(ComboProduct combo) {
        this.combo = combo;
        populateFields();
    }

    public void setOnComboSavedCallback(Consumer<Boolean> callback) {
        this.onSavedCallback = callback;
    }

    private void populateFields() {
        if (combo != null) {
            nameField.setText(combo.getName());
            descriptionArea.setText(combo.getDescription());
            discountPercentField.setText(String.valueOf(combo.getDiscountPercent()));
            statusComboBox.setValue(combo.getStatus() == 1 ? "Active" : "Inactive");

            // Tải danh sách sản phẩm đã chọn
            selectedProductsTable.setItems(FXCollections.observableArrayList(combo.getProducts()));
            updatePriceLabels();
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        // Lấy dữ liệu từ các trường nhập liệu
        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();
        double discountPercent = Double.parseDouble(discountPercentField.getText());
        int status = statusComboBox.getValue().equals("Active") ? 1 : 0;

        boolean success = false;

        if ("ADD".equals(mode)) {
            ComboProduct newCombo = new ComboProduct(0, name, description, discountPercent, status);
            newCombo.setProducts(selectedProductsTable.getItems());

            success = comboService.addCombo(newCombo);

            if (success) {
                Alert.showSuccess("Thêm combo thành công");
            } else {
                Alert.showAlert("Lỗi khi thêm combo");
                return;
            }
        } else if ("EDIT".equals(mode)) {
            combo.setName(name);
            combo.setDescription(description);
            combo.setDiscountPercent(discountPercent);
            combo.setStatus(status);
            combo.setProducts(selectedProductsTable.getItems());

            success = comboService.updateCombo(combo);

            if (success) {
                Alert.showSuccess("Cập nhật combo thành công");
            } else {
                Alert.showAlert("Lỗi khi cập nhật combo");
                return;
            }
        }

        if (onSavedCallback != null) {
            onSavedCallback.accept(success);
        }

        // Đóng dialog
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private boolean validateInputs() {
        StringBuilder errorMessage = new StringBuilder();

        if (nameField.getText().trim().isEmpty()) {
            errorMessage.append("Tên combo không được để trống.\n");
        }

        try {
            double discountPercent = Double.parseDouble(discountPercentField.getText());
            if (discountPercent < 5.0 || discountPercent > 15.0) {
                errorMessage.append("Phần trăm giảm giá phải từ 5% đến 15%.\n");
            }
        } catch (NumberFormatException e) {
            errorMessage.append("Phần trăm giảm giá không hợp lệ.\n");
        }

        if (selectedProductsTable.getItems().size() < 2) {
            errorMessage.append("Combo phải có ít nhất 2 sản phẩm.\n");
        }

        if (errorMessage.length() > 0) {
            Alert.showAlert("Lỗi: " + errorMessage.toString());
            return false;
        }

        return true;
    }
}