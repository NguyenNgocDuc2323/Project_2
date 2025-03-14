module com.example.coffeeshop {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires jbcrypt;
    requires static lombok;


    opens com.example.coffeeshop to javafx.graphics;

    // Add this line to open your com.example.coffeeshopop.controller package to JavaFX FXML
    opens com.example.coffeeshop.controller.CoffeeShop to javafx.fxml;

    exports com.example.coffeeshop.controller;
    opens com.example.coffeeshop.controller to javafx.fxml;
    exports com.example.coffeeshop.controller.admin;
    opens com.example.coffeeshop.controller.admin to javafx.fxml;
    opens com.example.coffeeshop.controller.staff to javafx.fxml;
    exports com.example.coffeeshop.controller.staff to javafx.fxml;
}