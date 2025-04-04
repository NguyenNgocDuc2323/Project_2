package controller.staff;

import database.CategoryDB;
import database.TableDB;
import helper.Alert;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Category;
import model.Table;

public class CategoryDialogController {
    @FXML
    public TextField nameField;
    @FXML
    public TextArea descriptionField;
    @FXML
    public Button cancelButton;
    @FXML
    private Text title;
    private Category category;

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setCategory(Category category) {
        this.category = category;
        if (category != null) {
            nameField.setText(category.getName());
            descriptionField.setText(String.valueOf(category.getDescription()));
        }
    }

    @FXML
    private void handleSubmit() {
        String categoryName = nameField.getText();
        if (categoryName.isBlank()) {
            Alert.showAlert("Category name must not blank");
            return;
        }
        String description = descriptionField.getText();
        if (description.isBlank()) {
            Alert.showAlert("Status must not blank");
            return;
        }
        Category category = new Category();
        if (this.category == null) {
            category.setName(categoryName);
            category.setDescription(description);
            CategoryDB.getInstance().createCategory(category);
        } else {
            category.setId(this.category.getId());
            category.setName(categoryName);
            category.setDescription(description);
            CategoryDB.getInstance().updateCategory(category);
        }
    }

    @FXML
    private void handleCloseDialog() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
