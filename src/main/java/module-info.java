module com.example.manage_account {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires jbcrypt;

    // This opens your entire module to JavaFX base
    opens com.example.manage_account to javafx.base, javafx.graphics, javafx.fxml;

    // These are still needed for specific controllers
    opens controller to javafx.fxml;
    opens controller.admin to javafx.fxml;
    opens controller.staff to javafx.fxml;
    opens controller.CoffeeShop to javafx.base, javafx.fxml;

    // Add this line to open the model package to JavaFX base
    opens model to javafx.base;

    exports controller;
    exports controller.admin;
    exports controller.staff to javafx.fxml;
    opens model to javafx.base;
}