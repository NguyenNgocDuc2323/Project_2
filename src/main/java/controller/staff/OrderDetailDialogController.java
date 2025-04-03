package controller.staff;

import database.OrderDetailDB;
import database.ProductDB;
import helper.Alert;
import model.OrderDetail;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class OrderDetailDialogController {
    @FXML
    public Text title;
    @FXML
    private TextField orderIdField;
    @FXML
    private ComboBox<String> productBox;
    @FXML
    private TextField quantityField;
    @FXML
    public Button cancelButton;
    private OrderDetail orderDetail;
    private final ObservableList<String> products = FXCollections.observableArrayList();

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setOrderDetail(OrderDetail orderDetail) {
        this.orderDetail = orderDetail;
        if (orderDetail != null) {
            productBox.setValue(ProductDB.getInstance().getProductById(orderDetail.getProductId()).getName());
            quantityField.setText(String.valueOf(orderDetail.getQuantity()));
        }
    }

    public void setOrderId(int orderId) {
        this.orderIdField.setText(String.valueOf(orderId));
    }

    @FXML
    public void initialize() {
        products.setAll(ProductDB.getInstance().getAllProductsName());
        productBox.setItems(products);
    }

    @FXML
    private void handleSubmit() {
        int orderId;
        try {
            orderId = Integer.parseInt(orderIdField.getText());
            if (orderId < 1) {
                Alert.showAlert("Order id must greater than 0");
                return;
            }
        } catch (NumberFormatException e) {
            Alert.showAlert("Order id must be a number");
            return;
        }
        String productName = productBox.getValue();
        if (productName == null || productName.isBlank()) {
            Alert.showAlert("Product must not blank");
            return;
        }
        int productId = ProductDB.getInstance().getProductByName(productName).getId();
        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText());
            if (quantity < 1) {
                Alert.showAlert("Quantity must greater than 0");
                return;
            }
        } catch (NumberFormatException e) {
            Alert.showAlert("Quantity must be a number");
            return;
        }
        if (this.orderDetail == null) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setProductId(productId);
            orderDetail.setQuantity(quantity);
            OrderDetailDB.getInstance().createOrderDetailAndUpdateOrder(orderDetail);
        } else {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setId(this.orderDetail.getId());
            orderDetail.setOrderId(orderId);
            orderDetail.setProductId(productId);
            orderDetail.setQuantity(quantity);
            OrderDetailDB.getInstance().updateOrderDetailAndOrder(orderDetail);
        }
    }

    @FXML
    private void handleCloseDialog() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
