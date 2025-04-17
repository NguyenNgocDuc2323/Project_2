package controller.admin;

import database.CoffeeDAO;
import helper.Navigator;
import javafx.beans.property.SimpleStringProperty;
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
import model.Category;
import model.CoffeeShop.Coffee;
import database.CategoryDAO;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class CoffeeAdminController implements Initializable {

    @FXML
    private TableView<Coffee> coffeeTableView;

    @FXML
    private TableColumn<Coffee, Integer> idColumn;

    @FXML
    private TableColumn<Coffee, String> nameColumn;

    @FXML
    private TableColumn<Coffee, String> categoryColumn;

    @FXML
    private TableColumn<Coffee, Double> priceColumn;

    @FXML
    private TableColumn<Coffee, String> statusColumn;

    @FXML
    private Button addButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    private CoffeeDAO coffeeDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        coffeeDAO = new CoffeeDAO();
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        // Hiển thị tên category thay vì số
        categoryColumn.setCellValueFactory(cellData -> {
            int categoryId = cellData.getValue().getCategoryId();
            CategoryDAO categoryDAO = new CategoryDAO();
            Category category = categoryDAO.getCategoryById(categoryId);
            return new SimpleStringProperty(category != null ? category.getName() : "");
        });
        
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        // Hiển thị status thay vì quantity
        statusColumn.setCellValueFactory(cellData -> {
            int status = cellData.getValue().getStatus();
            return new SimpleStringProperty(status == 1 ? "Active" : "Inactive");
        });

        loadCoffeeData();
    }

    private void loadCoffeeData() {
        try {
            ObservableList<Coffee> coffeeList = coffeeDAO.getAllCoffee();
            coffeeTableView.setItems(coffeeList);
        } catch (Exception e) {
            helper.Alert.showAlert("Database Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/manage_account/CoffeeShop/add_edit_coffee.fxml"));
            Parent root = loader.load();

            CoffeeDialogController controller = loader.getController();
            controller.setMode("ADD");

            controller.setOnCoffeeSavedCallback(success -> {
                if (success) loadCoffeeData();
            });

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add New Coffee");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            helper.Alert.showAlert("Could not open add coffee dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditButton(ActionEvent event) {
        Coffee selectedCoffee = coffeeTableView.getSelectionModel().getSelectedItem();
        if (selectedCoffee == null) {
            helper.Alert.showAlert("Selection Required" );
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/manage_account/CoffeeShop/add_edit_coffee.fxml"));
            Parent root = loader.load();

            CoffeeDialogController controller = loader.getController();
            controller.setMode("EDIT");
            controller.setCoffee(selectedCoffee);

            controller.setOnCoffeeSavedCallback(success -> {
                if (success) loadCoffeeData();
            });

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Edit Coffee");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            helper.Alert.showAlert("Could not open edit coffee dialog" + e.getMessage());
            e.printStackTrace();
        }
    }

    // Đã xóa phương thức onSwitchToAccountManage vì đã xóa nút tương ứng trong giao diện

    @FXML
    private void handleDeleteButton(ActionEvent event) {
        Coffee selectedCoffee = coffeeTableView.getSelectionModel().getSelectedItem();
        if (selectedCoffee == null) {
            helper.Alert.showAlert("Please select a coffee to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Coffee Item");
        alert.setContentText("Are you sure you want to delete " + selectedCoffee.getName() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean deleted = coffeeDAO.deleteCoffee(selectedCoffee.getId());
                if (deleted) {
                    loadCoffeeData();
                    helper.Alert.showSuccess("Coffee deleted successfully");
                } else {
                    helper.Alert.showAlert("Failed to delete coffee");
                }
            } catch (Exception e) {
                helper.Alert.showAlert("Failed to delete coffee"+  e.getMessage());
                e.printStackTrace();
            }
        }
    }
}