package controller.staff;

import helper.AccountManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Book;
import helper.BookManager;

import java.net.URL;
import java.util.ResourceBundle;

public class StaffController implements Initializable {
    @FXML
    private TableView<Book> tbl_book;

    @FXML
    private TableColumn<Book, Integer> tbl_id;

    @FXML
    private TableColumn<Book, Boolean> tbl_is_borrowed;

    @FXML
    private TableColumn<Book, String> tbl_name;

    @FXML
    private TableColumn<Book, Double> tbl_price;

    private ObservableList<Book> bookList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bookList = FXCollections.observableArrayList(BookManager.getInstance().getBooks());
        tbl_book.setItems(bookList);
        tbl_id.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        tbl_name.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        tbl_price.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        tbl_is_borrowed.setCellValueFactory(cellData -> cellData.getValue().borrowedProperty().asObject());
    }

}
