package controller.admin;

import database.CoffeeDAO;
import database.ComboDAO;
import helper.Alert;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.CoffeeShop.Coffee;
import model.CoffeeShop.ComboProduct;
import service.ComboService;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Optional;
import java.util.ResourceBundle;

public class ComboManagementController implements Initializable {

    @FXML
    private TableView<ComboProduct> comboTableView;

    @FXML
    private TableColumn<ComboProduct, Integer> idColumn;

    @FXML
    private TableColumn<ComboProduct, String> nameColumn;

    @FXML
    private TableColumn<ComboProduct, Double> originalPriceColumn;

    @FXML
    private TableColumn<ComboProduct, Double> discountPercentColumn;

    @FXML
    private TableColumn<ComboProduct, Double> finalPriceColumn;

    @FXML
    private TableColumn<ComboProduct, String> statusColumn;

    @FXML
    private Button addButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    private ComboService comboService;
    private DecimalFormat decimalFormat;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        comboService = new ComboService();
        decimalFormat = new DecimalFormat("#,##0.00");

        // Thiết lập các cột cho bảng
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        // Hiển thị giá gốc với định dạng tiền tệ
        originalPriceColumn.setCellValueFactory(cellData -> cellData.getValue().originalPriceProperty().asObject());
        originalPriceColumn.setCellFactory(column -> new TableCell<ComboProduct, Double>() {
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
        
        // Hiển thị phần trăm giảm giá
        discountPercentColumn.setCellValueFactory(cellData -> cellData.getValue().discountPercentProperty().asObject());
        discountPercentColumn.setCellFactory(column -> new TableCell<ComboProduct, Double>() {
            @Override
            protected void updateItem(Double percent, boolean empty) {
                super.updateItem(percent, empty);
                if (empty || percent == null) {
                    setText(null);
                } else {
                    setText(decimalFormat.format(percent) + "%");
                }
            }
        });
        
        // Hiển thị giá cuối cùng với định dạng tiền tệ
        finalPriceColumn.setCellValueFactory(cellData -> cellData.getValue().finalPriceProperty().asObject());
        finalPriceColumn.setCellFactory(column -> new TableCell<ComboProduct, Double>() {
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
        
        // Hiển thị trạng thái
        statusColumn.setCellValueFactory(cellData -> {
            int status = cellData.getValue().getStatus();
            return new SimpleStringProperty(status == 1 ? "Active" : "Inactive");
        });

        // Tải dữ liệu combo
        loadComboData();
    }

    private void loadComboData() {
        try {
            ObservableList<ComboProduct> comboList = comboService.getAllCombos();
            comboTableView.setItems(comboList);
        } catch (Exception e) {
            Alert.showAlert("Database Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/manage_account/CoffeeShop/combo_dialog.fxml"));
            Parent root = loader.load();

            ComboDialogController controller = loader.getController();
            controller.setMode("ADD");

            controller.setOnComboSavedCallback(success -> {
                if (success) loadComboData();
            });

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Thêm Combo Mới");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            Alert.showAlert("Không thể mở dialog thêm combo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditButton(ActionEvent event) {
        ComboProduct selectedCombo = comboTableView.getSelectionModel().getSelectedItem();
        if (selectedCombo == null) {
            Alert.showAlert("Vui lòng chọn một combo để chỉnh sửa");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/manage_account/CoffeeShop/combo_dialog.fxml"));
            Parent root = loader.load();

            ComboDialogController controller = loader.getController();
            controller.setMode("EDIT");
            controller.setCombo(selectedCombo);

            controller.setOnComboSavedCallback(success -> {
                if (success) loadComboData();
            });

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Chỉnh Sửa Combo");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            Alert.showAlert("Không thể mở dialog chỉnh sửa combo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteButton(ActionEvent event) {
        ComboProduct selectedCombo = comboTableView.getSelectionModel().getSelectedItem();
        if (selectedCombo == null) {
            Alert.showAlert("Vui lòng chọn một combo để xóa");
            return;
        }

        boolean confirmed = helper.Alert.confirm("Bạn có chắc chắn muốn xóa combo " + selectedCombo.getName() + "?");
        if (confirmed) {
            try {
                boolean deleted = comboService.deleteCombo(selectedCombo.getId());
                if (deleted) {
                    loadComboData();
                    Alert.showSuccess("Xóa combo thành công");
                } else {
                    Alert.showAlert("Không thể xóa combo");
                }
            } catch (Exception e) {
                Alert.showAlert("Lỗi khi xóa combo: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}