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
            Locale us = new Locale("en", "US");
            Translator.setLocale(us);
            Navigator.getInstance().setState(stage);
            Navigator.getInstance().gotoLogin();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("The error when loading FXML! Please check the path.");
        }
    }

    public static void main(String[] args) {
        launch();
    }
}