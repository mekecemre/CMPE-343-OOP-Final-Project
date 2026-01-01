package com.greengrocer.controllers;

import com.greengrocer.database.UserDAO;
import com.greengrocer.models.User;
import com.greengrocer.utils.SceneNavigator;
import com.greengrocer.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for the Login screen.
 * Handles user authentication and navigation to role-specific interfaces.
 * 
 * @author Group17
 * @version 1.0
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink registerLink;

    /** User data access object */
    private UserDAO userDAO;

    /**
     * Initializes the controller.
     * Called automatically after FXML is loaded.
     */
    @FXML
    public void initialize() {
        userDAO = new UserDAO();
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    /**
     * Handles the login button click event.
     * Authenticates the user and navigates to the appropriate interface.
     * 
     * @param event The action event
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }

        // Attempt authentication
        User user = userDAO.authenticate(username, password);

        if (user != null) {
            // Authentication successful
            SessionManager.getInstance().setCurrentUser(user);

            // Navigate to appropriate interface based on role
            Stage stage = (Stage) loginButton.getScene().getWindow();

            switch (user.getRole()) {
                case "CUSTOMER":
                    SceneNavigator.loadScene(stage, "Customer.fxml", "Group17 GreenGrocer - Customer");
                    break;
                case "CARRIER":
                    SceneNavigator.loadScene(stage, "Carrier.fxml", "Group17 GreenGrocer - Carrier");
                    break;
                case "OWNER":
                    SceneNavigator.loadScene(stage, "Owner.fxml", "Group17 GreenGrocer - Owner");
                    break;
                default:
                    showError("Unknown user role.");
            }
        } else {
            // Authentication failed
            showError("Invalid username or password. Please try again.");
            passwordField.clear();
        }
    }

    /**
     * Handles the register link click event.
     * Navigates to the registration screen.
     * 
     * @param event The action event
     */
    @FXML
    private void handleRegisterLink(ActionEvent event) {
        Stage stage = (Stage) registerLink.getScene().getWindow();
        SceneNavigator.loadScene(stage, "Registration.fxml", "Group17 GreenGrocer - Register");
    }

    /**
     * Shows an error message on the form.
     * 
     * @param message The error message to display
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setManaged(true);
        errorLabel.setVisible(true);
    }
}
