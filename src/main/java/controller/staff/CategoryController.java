package controller.staff;

import database.CategoryDB;
import database.TableDB;
import helper.Alert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Category;
import model.Table;

import java.io.IOException;

import static helper.Navigator.CATEGORY_DIALOG;
import static helper.Navigator.TABLE_DIALOG;

public class CategoryController {
    @FXML
    private TableView<Category> categoryTable;
    @FXML
    private Button deleteButton;
    @FXML
    private TableColumn<Category, Integer> idColumn;
    @FXML
    private TableColumn<Category, String> nameColumn;
    @FXML
    private TableColumn<Category, String> descriptionColumn;
    private final ObservableList<Category> categoryObservableList = FXCollections.observableArrayList();

    @FXML
    private void initialize(){
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        loadCategoryFromDatabase();
    }

    @FXML
    private void handleCreate() {
        openDialog("Create Category", null);
        loadCategoryFromDatabase();
    }

    @FXML
    private void handleUpdate() {
        Category selected = categoryTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            openDialog("Update Category", selected);
            loadCategoryFromDatabase();
        } else {
            Alert.showAlert("Please select category to update");
        }
    }

    @FXML
    private void handleDelete() {
        Category selected = categoryTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            int categoryId = selected.getId();
            deleteButton.setOnAction(event -> {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Confirmation");
                alert.setHeaderText("Are you sure you want to delete this item?");
                alert.setContentText("This action cannot be reversed");
                alert.showAndWait().ifPresent(response -> {
                    if (response.getText().equals("OK")) {
                        CategoryDB.getInstance().deleteCategory(categoryId);
                        loadCategoryFromDatabase();
                    }
                });
            });
        } else {
            Alert.showAlert("Please select category to delete");
        }
    }

    private void loadCategoryFromDatabase() {
        categoryObservableList.setAll(CategoryDB.getInstance().getAllCategory());
        categoryTable.setItems(categoryObservableList);
    }

    private void openDialog(String title, Category category) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(CATEGORY_DIALOG));
            Parent root = loader.load();
            CategoryDialogController controller = loader.getController();
            controller.setTitle(title);
            controller.setCategory(category);
            Stage dialog = new Stage();
            dialog.setTitle(title);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
