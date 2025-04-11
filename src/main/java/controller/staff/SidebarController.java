package controller.staff;

import helper.Navigator;
import javafx.fxml.FXML;

import java.io.IOException;

public class SidebarController {
    @FXML
    private void handleGoToOrderStatistic() throws IOException {
        try {
            Navigator.getInstance().gotoOrderStatistic();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGoToOrderManagement() throws IOException {
        try {
            Navigator.getInstance().gotoOrderManagement();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGoToTableManagement() throws IOException {
        try {
            Navigator.getInstance().gotoTableManagement();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGoToCategoryManagement() throws IOException {
        try {
            Navigator.getInstance().gotoCategoryManagement();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
