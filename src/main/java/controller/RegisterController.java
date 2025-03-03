package controller;

import helper.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.io.IOException;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    @FXML
    public void goLogin(ActionEvent event) {
        try {
            Navigator.getInstance().gotoLogin();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
