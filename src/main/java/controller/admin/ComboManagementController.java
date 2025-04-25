package controller.admin;

import helper.Alert;
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
import model.CoffeeShop.ComboProduct;
import service.ComboService;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
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

        // Setup columns
        setupColumns();

        // Add selection listener
        comboTableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    boolean hasSelection = newSelection != null;
                    editButton.setDisable(!hasSelection);
                    deleteButton.setDisable(!hasSelection);
                }
        );

        // Load data
        loadComboData();
    }

    private void setupColumns() {
        // ID Column
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setStyle("-fx-alignment: CENTER;");

        // Name Column
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setStyle("-fx-alignment: CENTER-LEFT;");

        // Original Price Column
        originalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("originalPrice"));
        originalPriceColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText("$" + decimalFormat.format(price));
                }
                setStyle("-fx-alignment: CENTER-RIGHT;");
            }
        });

        // Discount Column
        discountPercentColumn.setCellValueFactory(new PropertyValueFactory<>("discountPercent"));
        discountPercentColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double percent, boolean empty) {
                super.updateItem(percent, empty);
                if (empty || percent == null) {
                    setText(null);
                } else {
                    setText(decimalFormat.format(percent) + "%");
                }
                setStyle("-fx-alignment: CENTER;");
            }
        });

        // Final Price Column
        finalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("finalPrice"));
        finalPriceColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText("$" + decimalFormat.format(price));
                }
                setStyle("-fx-alignment: CENTER-RIGHT;");
            }
        });

        // Status Column
        statusColumn.setCellValueFactory(cellData -> {
            boolean isActive = cellData.getValue().getStatus() == 1;
            String status = isActive ? "Active" : "Inactive";
            return new SimpleStringProperty(status);
        });
        statusColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if ("Active".equals(status)) {
                        setStyle("-fx-text-fill: green; -fx-alignment: CENTER;");
                    } else {
                        setStyle("-fx-text-fill: red; -fx-alignment: CENTER;");
                    }
                }
            }
        });
    }

    private void loadComboData() {
        try {
            ObservableList<ComboProduct> comboList = comboService.getAllCombos();

            // Debug log
            System.out.println("\nLoading combos:");
            for (ComboProduct combo : comboList) {
                System.out.println("Combo: " + combo.getName() +
                        " (ID: " + combo.getId() +
                        ", Original: $" + combo.getOriginalPrice() +
                        ", Discount: " + combo.getDiscountPercent() + "%" +
                        ", Final: $" + combo.getFinalPrice() +
                        ", Status: " + (combo.getStatus() == 1 ? "Active" : "Inactive") + ")");
            }

            comboTableView.getItems().clear();
            comboTableView.setItems(comboList);

        } catch (Exception e) {
            Alert.showAlert("Error loading combos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/manage_account/CoffeeShop/ComboDialog.fxml"));
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
            Alert.showAlert("Unable to open the add combo dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditButton(ActionEvent event) {
        ComboProduct selectedCombo = comboTableView.getSelectionModel().getSelectedItem();
        if (selectedCombo == null) {
            Alert.showAlert("Please select a combo to edit.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/manage_account/CoffeeShop/ComboDialog.fxml"));
            Parent root = loader.load();

            ComboDialogController controller = loader.getController();
            controller.setMode("EDIT");
            controller.setCombo(selectedCombo);

            controller.setOnComboSavedCallback(success -> {
                if (success) loadComboData();
            });

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Edit Combo");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            Alert.showAlert("Unable to open the edit combo dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteButton(ActionEvent event) {
        ComboProduct selectedCombo = comboTableView.getSelectionModel().getSelectedItem();
        if (selectedCombo == null) {
            Alert.showAlert("Please select a combo to delete.");
            return;
        }

        boolean confirmed = helper.Alert.confirm("Are you sure you want to delete the combo? " + selectedCombo.getName() + "?");
        if (confirmed) {
            try {
                boolean deleted = comboService.deleteCombo(selectedCombo.getId());
                if (deleted) {
                    loadComboData();
                    Alert.showSuccess("Combo deleted successfully.");
                } else {
                    Alert.showAlert("Unable to delete the combo.");
                }
            } catch (Exception e) {
                Alert.showAlert("Unable to delete the combo. " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}