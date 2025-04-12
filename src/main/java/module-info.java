module com.example.manage_account {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires jbcrypt;

    opens com.example.manage_account to javafx.base, javafx.graphics, javafx.fxml;

    opens controller to javafx.fxml;
    opens controller.admin to javafx.fxml;
    opens controller.staff to javafx.fxml;
    opens controller.CoffeeShop to javafx.base, javafx.fxml;

    opens model to javafx.base;
    opens model.CoffeeShop to javafx.base;

    // ✅ Thêm dòng này để fix lỗi PropertyValueFactory
    opens model.Admin to javafx.base;

    exports controller;
    exports controller.admin;
    exports controller.staff;

    exports model;
}
