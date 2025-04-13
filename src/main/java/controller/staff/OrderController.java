package controller.staff;

import database.OrderDB;
import helper.Alert;
import helper.Navigator;
import javafx.scene.control.DatePicker;
import model.Order;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Collectors;

import static helper.Navigator.ORDER_DIALOG;

public class OrderController {
    @FXML
    public TextField orderIdField;
    @FXML
    public TextField userIdField;
    @FXML
    public DatePicker orderDateField;
    @FXML
    private TableView<Order> orderTable;
    @FXML
    private TableColumn<Order, Integer> orderIdColumn;
    @FXML
    private TableColumn<Order, Integer> userIdColumn;
    @FXML
    private TableColumn<Order, String> tableNameColumn;
    @FXML
    private TableColumn<Order, LocalDateTime> orderDateColumn;
    @FXML
    private TableColumn<Order, String> statusColumn;
    @FXML
    private TableColumn<Order, Double> totalPriceColumn;
    @FXML
    private TableColumn<Order, String> paymentMethodColumn;
    private final ObservableList<Order> orderObservableList = FXCollections.observableArrayList();
    private final ObservableList<Order> filteredOrderObservableList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        setupOrderTable();
        loadOrdersFromDatabase();
    }

    @FXML
    private void handleCreateOrder() {
        openDialog("Create Order", null);
        loadOrdersFromDatabase();
    }

    @FXML
    private void handleUpdateOrder() {
        Order selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            openDialog("Update Order", selected);
            loadOrdersFromDatabase();
        } else {
            Alert.showAlert("Please select order to update");
        }
    }

    @FXML
    private void handleGoToOrderDetail() throws IOException {
        Order selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            int orderId = selected.getId();
            try {
                Navigator.getInstance().gotoOrderDetailManagement(orderId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alert.showAlert("Please select order to go to order detail");
        }
    }

    @FXML
    private void handleFilterByOrderId(ActionEvent actionEvent) {
        try {
            int orderId = Integer.parseInt(orderIdField.getText());
            filteredOrderObservableList.setAll(orderObservableList.stream().filter(order -> Objects.equals(order.getId(), orderId)).collect(Collectors.toList()));
            orderTable.setItems(filteredOrderObservableList);
        } catch (NumberFormatException e) {
            orderTable.setItems(orderObservableList);
        }
    }

    @FXML
    private void handleFilterByUserId(ActionEvent actionEvent) {
        try {
            int userId = Integer.parseInt(userIdField.getText());
            filteredOrderObservableList.setAll(orderObservableList.stream().filter(order -> Objects.equals(order.getUserId(), userId)).collect(Collectors.toList()));
            orderTable.setItems(filteredOrderObservableList);
        } catch (NumberFormatException e) {
            orderTable.setItems(orderObservableList);
        }
    }

    @FXML
    private void handleFilterByOrderDate(ActionEvent actionEvent) {
        LocalDate selectedOrderDate = orderDateField.getValue();
        if (selectedOrderDate == null) {
            orderTable.setItems(orderObservableList);
        } else {
            filteredOrderObservableList.setAll(orderObservableList.stream().filter(order -> Objects.equals(order.getOrderDate().toLocalDate(), selectedOrderDate)).collect(Collectors.toList()));
            orderTable.setItems(filteredOrderObservableList);
        }
    }

    private void setupOrderTable() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        tableNameColumn.setCellValueFactory(new PropertyValueFactory<>("tableName"));
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        paymentMethodColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
    }

    private void loadOrdersFromDatabase() {
        orderObservableList.setAll(OrderDB.getInstance().getAllOrders());
        orderTable.setItems(orderObservableList);
    }

    private void openDialog(String title, Order order) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ORDER_DIALOG));
            Parent root = loader.load();
            OrderDialogController controller = loader.getController();
            controller.setTitle(title);
            controller.setOrder(order);
            Stage dialog = new Stage();
            dialog.setTitle(title);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
