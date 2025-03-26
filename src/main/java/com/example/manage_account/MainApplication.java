package com.example.manage_account;

import helper.Navigator;
import helper.Translator;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        try {
            Locale vietnam = new Locale("vn", "VN");
            Translator.setLocale(vietnam);
            Navigator.getInstance().setState(stage);
            Navigator.getInstance().gotoOrder();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi load FXML! Kiểm tra đường dẫn.");
        }
    }

    public static void main(String[] args) {
        launch();
    }
}