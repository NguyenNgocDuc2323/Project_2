package controller.CoffeeShop;

import helper.CoffeeShop.CoffeeItemManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import model.Coffee;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MenuViewController implements Initializable {
    @FXML private FlowPane coffeeItemsContainer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadCoffeeItems();
    }

    private void loadCoffeeItems() {

//        String[] coffeeNames = {
//                "Caramel Macchiato", "Espresso", "Cappuccino", "Flat White",
//                "Vietnamese Coffee", "Cold Brew", "Americano", "Mocha"
//        };

        List<Coffee> coffeeNames = new ArrayList<>();
        coffeeNames = CoffeeItemManager.getInstance().getAllCoffeeItems();

        for (Coffee coffee : coffeeNames) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/manage_account/CoffeeShop/CoffeeItem.fxml"));
                Node coffeeItem = loader.load();

                // You can set properties for each coffee item if you have a controller for it
                // CoffeeItemController controller = loader.getController();
                // controller.setCoffeeName(coffeeName);

                // Get the controller and set the coffee item data
                CoffeeItemController controller = loader.getController();
                controller.setCoffee(coffee);

                coffeeItemsContainer.getChildren().add(coffeeItem);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Could not load coffee item: " + e.getMessage());
            }
        }
    }
}