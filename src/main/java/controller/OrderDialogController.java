package controller;

import database.OrderDB;
import database.TableDB;
import helper.Alert;
import model.Order;
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

public class OrderDialogController implements Initializable {
    @FXML
    public Text title;
    @FXML
    private TextField userIdField;
    @FXML
    private ComboBox<String> tableBox;
    @FXML
    private ComboBox<String> statusBox;
    @FXML
    private ComboBox<String> paymentMethodBox;
    @FXML
    public Button cancelButton;
    private Order order;
    private final ObservableList<String> tables = FXCollections.observableArrayList();

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setOrder(Order order) {
        this.order = order;
        if (order != null) {
            userIdField.setText(String.valueOf(order.getUserId()));
            tableBox.setValue(TableDB.getInstance().getTableById(order.getTableId()).getTableName());
            statusBox.setValue(order.getStatus());
            paymentMethodBox.setValue(order.getPaymentMethod());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tables.addAll(TableDB.getInstance().getAllTablesName());
        tableBox.setItems(tables);
    }

    @FXML
    private void handleSubmit() {
        int userId;
        try {
            userId = Integer.parseInt(userIdField.getText());
            if (userId < 1) {
                Alert.showAlert("User id must greater than 0");
                return;
            }
        } catch (NumberFormatException e) {
            Alert.showAlert("User id must be a number");
            return;
        }
        int tableId = TableDB.getInstance().getTableByName(tableBox.getValue()).getId();
        String status = statusBox.getValue();
        if (status == null || status.isBlank()) {
            Alert.showAlert("Status must not blank");
            return;
        }
        String paymentMethod = paymentMethodBox.getValue();
        if (paymentMethod == null || paymentMethod.isBlank()) {
            Alert.showAlert("Payment method must not blank");
            return;
        }
        if (order == null) {
            OrderDB.getInstance().createOrder(new Order(userId, tableId, paymentMethod));
            handleCloseDialog();
        } else {
            OrderDB.getInstance().updateOrderStatus(new Order(order.getId(), status));
            handleCloseDialog();
        }
    }

    @FXML
    private void handleCloseDialog() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
