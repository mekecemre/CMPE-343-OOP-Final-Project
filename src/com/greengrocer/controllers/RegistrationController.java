package com.greengrocer.controllers;

import com.greengrocer.database.UserDAO;
import com.greengrocer.models.User;
import com.greengrocer.utils.AlertUtils;
import com.greengrocer.utils.SceneNavigator;
import com.greengrocer.utils.ValidationUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Controller for the Registration screen.
 * Handles new customer registration.
 * 
 * @author Group17
 * @version 1.0
 */
public class RegistrationController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField fullNameField;

    @FXML
    private TextArea addressField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField emailField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button registerButton;

    @FXML
    private Button cancelButton;

    /** User data access object */
    private UserDAO userDAO;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        userDAO = new UserDAO();
        errorLabel.setVisible(false);
    }

    /**
     * Handles the register button click event.
     * Validates input and creates new user.
     * 
     * @param event The action event
     */
    @FXML
    private void handleRegister(ActionEvent event) {
        // Get form values
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String fullName = fullNameField.getText().trim();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        // Validate required fields
        if (username.isEmpty()) {
            showError("Username is required.");
            return;
        }

        // Validate password strength
        String passwordError = ValidationUtils.getPasswordError(password);
        if (passwordError != null) {
            showError(passwordError);
            return;
        }

        // Validate password match
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }

        // Create user object
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setRole("CUSTOMER");
        newUser.setFullName(fullName.isEmpty() ? null : fullName);
        newUser.setAddress(address.isEmpty() ? null : address);
        newUser.setPhone(phone.isEmpty() ? null : phone);
        newUser.setEmail(email.isEmpty() ? null : email);

        // Attempt registration
        boolean success = userDAO.register(newUser);

        if (success) {
            AlertUtils.showSuccess("Registration successful! You can now login with your credentials.");

            // Navigate back to login
            Stage stage = (Stage) registerButton.getScene().getWindow();
            SceneNavigator.loadScene(stage, "Login.fxml", "Group17 GreenGrocer - Login");
        } else {
            showError("Registration failed. Username may already exist.");
        }
    }

    /**
     * Handles the cancel button click event.
     * Returns to the login screen.
     * 
     * @param event The action event
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        SceneNavigator.loadScene(stage, "Login.fxml", "Group17 GreenGrocer - Login");
    }

    /**
     * Shows an error message on the form.
     * 
     * @param message The error message to display
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
