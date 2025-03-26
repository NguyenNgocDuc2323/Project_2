package controller;

import database.OrderDetailDB;
import helper.Alert;
import helper.Navigator;
import model.OrderDetail;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class OrderDetailController implements Initializable {
    @FXML
    private TableView<OrderDetail> orderDetailTable;
    @FXML
    private TableColumn<OrderDetail, Integer> idColumn;
    @FXML
    private TableColumn<OrderDetail, Integer> orderIdColumn;
    @FXML
    private TableColumn<OrderDetail, Integer> productIdColumn;
    @FXML
    private TableColumn<OrderDetail, String> productNameColumn;
    @FXML
    private TableColumn<OrderDetail, Integer> quantityColumn;
    @FXML
    private TableColumn<OrderDetail, Double> unitPriceColumn;
    private int orderId;
    private final ObservableList<OrderDetail> orderDetailObservableList = FXCollections.observableArrayList();

    public void setOrderId(int orderId) {
        this.orderId = orderId;
        loadOrderDetailFromDatabase();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        unitPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
    }

    public void loadOrderDetailFromDatabase() {
        orderDetailObservableList.clear();
        orderDetailObservableList.addAll(OrderDetailDB.getInstance().getAllOrderDetailByOrderId(this.orderId));
        orderDetailTable.setItems(orderDetailObservableList);
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
            int orderDetailId = selected.getId();
            OrderDetail orderDetail = OrderDetailDB.getInstance().getOrderDetailById(orderDetailId);
            if (orderDetail != null) {
                openDialog("Update Order Detail", orderDetail);
                loadOrderDetailFromDatabase();
            } else {
                Alert.showAlert("Error getting order detail with id: " + orderDetailId);
            }
        } else {
            Alert.showAlert("Please select order detail to update");
        }
    }

    @FXML
    private void handleBackToOrder() throws IOException {
        try {
            Navigator.getInstance().gotoOrder();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openDialog(String title, OrderDetail orderDetail) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/manage_account/OrderDetailDialog.fxml"));
            Parent root = loader.load();
            OrderDetailDialogController controller = loader.getController();
            controller.setOrderId(this.orderId);
            controller.setOrderDetail(orderDetail);
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
