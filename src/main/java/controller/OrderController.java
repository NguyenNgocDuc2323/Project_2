package controller;

import database.OrderDB;
import helper.Alert;
import helper.Navigator;
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

import static helper.Navigator.ORDER_DIALOG;

public class OrderController {
    @FXML
    public TextField userIdField;
    @FXML
    private TableView<Order> orderTable;
    @FXML
    private TableColumn<Order, Integer> idColumn;
    @FXML
    private TableColumn<Order, Integer> userIdColumn;
    @FXML
    private TableColumn<Order, Integer> tableIdColumn;
    @FXML
    private TableColumn<Order, String> tableNameColumn;
    @FXML
    private TableColumn<Order, String> statusColumn;
    @FXML
    private TableColumn<Order, Double> totalPriceColumn;
    @FXML
    private TableColumn<Order, String> paymentMethodColumn;
    private final ObservableList<Order> orderObservableList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        tableIdColumn.setCellValueFactory(new PropertyValueFactory<>("tableId"));
        tableNameColumn.setCellValueFactory(new PropertyValueFactory<>("tableName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        paymentMethodColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        loadOrdersFromDatabase();
    }

    public void loadOrdersFromDatabase() {
        orderObservableList.clear();
        orderObservableList.addAll(OrderDB.getInstance().getAllOrders());
        orderTable.setItems(orderObservableList);
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
            int orderId = selected.getId();
            Order order = OrderDB.getInstance().getOrderById(orderId);
            if (order != null) {
                openDialog("Update Order", order);
                loadOrdersFromDatabase();
            } else {
                Alert.showAlert("Error getting order with id: " + orderId);
            }
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
                Navigator.getInstance().gotoOrderDetail(orderId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alert.showAlert("Please select order to go to order detail");
        }
    }

    @FXML
    public void handleSearchByUserId(ActionEvent actionEvent) {
        try {
            int userId = Integer.parseInt(userIdField.getText());
            orderObservableList.clear();
            orderObservableList.addAll(OrderDB.getInstance().getAllOrdersByUserId(userId));
            orderTable.setItems(orderObservableList);
        } catch (NumberFormatException e) {
            loadOrdersFromDatabase();
        }
    }

    private void openDialog(String title, Order order) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ORDER_DIALOG));
            Parent root = loader.load();
            OrderDialogController controller = loader.getController();
            controller.setOrder(order);
            controller.setTitle(title);
            Stage dialog = new Stage();
            dialog.setTitle(title);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
