module com.example.manage_account {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires jbcrypt;


    opens com.example.manage_account to javafx.graphics;

    // Add this line to open your controller package to JavaFX FXML
    opens controller.CoffeeShop to javafx.fxml;

    exports controller;
    opens controller to javafx.fxml;
    exports controller.admin;
    opens controller.admin to javafx.fxml;
    opens controller.staff to javafx.fxml;
    exports controller.staff to javafx.fxml;
}