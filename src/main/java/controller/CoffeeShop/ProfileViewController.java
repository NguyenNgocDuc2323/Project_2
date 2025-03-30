package controller.CoffeeShop;

import helper.Alert;
import helper.DB_Helper.Account;
import helper.Navigator;
import helper.REGEX;
import helper.Session;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ProfileViewController implements Initializable {
    @FXML private Label userNameLabel;
    @FXML private Label emailLabel;
    @FXML private Label accountTypeLabel;
    @FXML private Label statusLabel;
    @FXML private Label welcomeLabel;
    @FXML private Label initialsLabel;
    @FXML private Circle profilePicCircle;
    @FXML private Circle statusIndicator;
    @FXML private Button signOutButton;

    @FXML private VBox passwordChangeForm;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button updatePasswordButton;
    @FXML private Button cancelButton;

    private model.Account currentAccount;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up button handlers
        signOutButton.setOnAction(event -> handleSignOut());

        // Set up password change functionality
        updatePasswordButton.setOnAction(event -> updatePassword());
        cancelButton.setOnAction(event -> clearPasswordFields());

        // Load and display current user data
        loadUserProfile();
    }

    private void loadUserProfile() {
        // Get the current user from session
        currentAccount = Session.getInstance().getCurrentUser();

        if (currentAccount == null) {
            Alert.showAlert("No user session found. Please log in again.");
            try {
                Navigator.getInstance().gotoLogin();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        // Set user information
        userNameLabel.setText(currentAccount.getName());
        emailLabel.setText(currentAccount.getEmail());
        accountTypeLabel.setText(currentAccount.getTypeAsString());

        // Set status
        boolean isLocked = currentAccount.isLocked();
        statusLabel.setText(isLocked ? "Locked" : "Active");
        statusIndicator.setFill(isLocked ? Color.RED : Color.GREEN);

        // Set welcome message
        welcomeLabel.setText("Welcome back!");

        // Set profile picture with initials
        String initials = getInitials(currentAccount.getName());
        initialsLabel.setText(initials);

        // Set profile color based on name
        Color profileColor = getColorFromName(currentAccount.getName());
        profilePicCircle.setFill(profileColor);
    }

    private String getInitials(String fullName) {
        if (fullName == null || fullName.isEmpty()) {
            return "?";
        }

        String[] names = fullName.split(" ");
        StringBuilder initials = new StringBuilder();

        for (String name : names) {
            if (!name.isEmpty()) {
                initials.append(name.charAt(0));
            }
        }

        return initials.toString().toUpperCase();
    }

    private Color getColorFromName(String name) {
        // Return fixed color regardless of input name
        return Color.rgb(184, 92, 56);
    }

    private void clearPasswordFields() {
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }

    private void updatePassword() {
        if (currentAccount == null) {
            Alert.showAlert("No account information available");
            return;
        }

        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Check if fields are empty
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Alert.showAlert("All password fields are required");
            return;
        }

        // Validate current password
        if (!BCrypt.checkpw(currentPassword, currentAccount.getPassword())) {
            Alert.showAlert("Current password is incorrect");
            return;
        }

        // Validate new password format
        if (!REGEX.isValidPassword(newPassword)) {
            Alert.showAlert("Password must be at least 8 characters long and contain at least one letter, one number, and one special character");
            return;
        }

        // Confirm passwords match
        if (!newPassword.equals(confirmPassword)) {
            Alert.showAlert("New passwords do not match");
            return;
        }

        try {
            // Hash the new password
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

            // Update password in database
            boolean success = Account.resetPassword(currentAccount.getId(), hashedPassword);

            if (success) {
                // Update the current account object
                currentAccount.setPassword(hashedPassword);

                // Update the session
                Session.getInstance().setCurrentUser(currentAccount);

                // Show success feedback
                helper.Alert.showSuccess("Password updated successfully");

                // Clear password fields
                clearPasswordFields();

                // Visual success indicator
                updatePasswordButton.setText("✓ Updated");
                updatePasswordButton.setStyle("-fx-background-color: #4CAF50;");

                // Reset button after 2 seconds
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        javafx.application.Platform.runLater(() -> {
                            updatePasswordButton.setText("Update Password");
                            updatePasswordButton.setStyle("");
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                Alert.showAlert("Failed to update password. Please try again.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert.showAlert("An error occurred: " + e.getMessage());
        }
    }

    private void handleSignOut() {
        try {
            // Clear the current user session
            Session.getInstance().clearCurrentUser();

            // Navigate back to login screen
            Navigator.getInstance().gotoLogin();
        } catch (IOException e) {
            e.printStackTrace();
            Alert.showAlert("Error signing out. Please try again.");
        }
    }
}