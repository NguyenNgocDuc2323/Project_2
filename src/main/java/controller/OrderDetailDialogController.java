package controller;

import database.OrderDetailDB;
import database.ProductDB;
import helper.Alert;
import model.OrderDetail;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class OrderDetailDialogController implements Initializable {
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        products.addAll(ProductDB.getInstance().getAllProductsName());
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
        int productId = ProductDB.getInstance().getProductByName(productBox.getValue()).getId();
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
        if (orderDetail == null) {
            OrderDetailDB.getInstance().createOrderDetailAndUpdateOrder(new OrderDetail(orderId, productId, quantity));
            handleCloseDialog();
        } else {
            OrderDetailDB.getInstance().updateOrderDetailAndOrder(new OrderDetail(orderDetail.getId(), orderId, productId, quantity));
            handleCloseDialog();
        }
    }

    @FXML
    private void handleCloseDialog() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
