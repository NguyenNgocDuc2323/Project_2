package controller.staff;

import database.OrderDetailDB;
import helper.Alert;
import helper.Navigator;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import model.OrderDetail;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;

import static helper.Navigator.ORDER_DETAIL_DIALOG;

public class OrderDetailController {
    @FXML
    private TextField orderDetailIdField;
    @FXML
    private TableView<OrderDetail> orderDetailTable;
    @FXML
    private TableColumn<OrderDetail, Integer> orderDetailIdColumn;
    @FXML
    private TableColumn<OrderDetail, Integer> orderIdColumn;
    @FXML
    private TableColumn<OrderDetail, String> productNameColumn;
    @FXML
    private TableColumn<OrderDetail, Integer> quantityColumn;
    @FXML
    private TableColumn<OrderDetail, Double> unitPriceColumn;
    private int orderId;
    private final ObservableList<OrderDetail> orderDetailObservableList = FXCollections.observableArrayList();
    private final ObservableList<OrderDetail> filteredOrderDetailObservableList = FXCollections.observableArrayList();

    public void setOrderId(int orderId) {
        this.orderId = orderId;
        loadOrderDetailFromDatabase();
    }

    @FXML
    private void initialize() {
        orderDetailIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        unitPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
    }

    @FXML
    private void handleCreateOrderDetail() {
        openDialog("Create Order Detail", null);
        loadOrderDetailFromDatabase();
    }

    @FXML
    private void handleUpdateOrderDetail() {
        OrderDetail selected = orderDetailTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            openDialog("Update Order Detail", selected);
            loadOrderDetailFromDatabase();
        } else {
            Alert.showAlert("Please select order detail to update");
        }
    }

    @FXML
    private void handleFilterByOrderDetailId(ActionEvent actionEvent) {
        try {
            int orderDetailId = Integer.parseInt(orderDetailIdField.getText());
            filteredOrderDetailObservableList.setAll(orderDetailObservableList.stream().filter(order -> Objects.equals(order.getId(), orderDetailId)).collect(Collectors.toList()));
            orderDetailTable.setItems(filteredOrderDetailObservableList);
        } catch (NumberFormatException e) {
            orderDetailTable.setItems(orderDetailObservableList);
        }
    }

    public void loadOrderDetailFromDatabase() {
        orderDetailObservableList.setAll(OrderDetailDB.getInstance().getAllOrderDetailByOrderId(this.orderId));
        orderDetailTable.setItems(orderDetailObservableList);
    }

    private void openDialog(String title, OrderDetail orderDetail) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ORDER_DETAIL_DIALOG));
            Parent root = loader.load();
            OrderDetailDialogController controller = loader.getController();
            controller.setTitle(title);
            controller.setOrderId(this.orderId);
            controller.setOrderDetail(orderDetail);
            Stage dialog = new Stage();
            dialog.setTitle(title);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
