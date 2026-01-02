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
 * Handles new customer registration with dynamic validation.
 * 
 * @author Group17
 * @version 1.0
 */
public class RegistrationController {

    // Form fields
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

    // Validation hint labels
    @FXML
    private Label usernameHint;
    @FXML
    private Label confirmPwdHint;
    @FXML
    private Label fullNameHint;
    @FXML
    private Label addressHint;
    @FXML
    private Label phoneHint;
    @FXML
    private Label emailHint;

    // Password requirement labels
    @FXML
    private Label pwdLengthReq;
    @FXML
    private Label pwdUpperReq;
    @FXML
    private Label pwdLowerReq;

    // Other UI elements
    @FXML
    private Label errorLabel;
    @FXML
    private Button registerButton;
    @FXML
    private Hyperlink cancelButton;

    /** User data access object */
    private UserDAO userDAO;

    /** Styles for validation states */
    private static final String VALID_STYLE = "-fx-border-color: #27ae60; -fx-border-width: 2;";
    private static final String INVALID_STYLE = "-fx-border-color: #e74c3c; -fx-border-width: 2;";
    private static final String NEUTRAL_STYLE = "";
    private static final String VALID_TEXT_STYLE = "-fx-font-size: 11px; -fx-text-fill: #27ae60;";
    private static final String INVALID_TEXT_STYLE = "-fx-font-size: 11px; -fx-text-fill: #e74c3c;";

    /**
     * Default constructor for RegistrationController.
     * Called by JavaFX when loading the FXML file.
     */
    public RegistrationController() {
    }

    /**
     * Initializes the controller and sets up validation listeners.
     */
    @FXML
    public void initialize() {
        userDAO = new UserDAO();
        errorLabel.setVisible(false);

        // Hide all hints initially
        hideAllHints();

        // Set up dynamic validation listeners
        setupValidationListeners();
    }

    /**
     * Hides all validation hints initially.
     */
    private void hideAllHints() {
        usernameHint.setText("");
        confirmPwdHint.setText("");
        fullNameHint.setText("");
        addressHint.setText("");
        phoneHint.setText("");
        emailHint.setText("");
    }

    /**
     * Sets up listeners for dynamic validation on all fields.
     */
    private void setupValidationListeners() {
        // Username validation - minimum 3 characters
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                usernameField.setStyle(NEUTRAL_STYLE);
                usernameHint.setText("");
            } else if (newVal.trim().length() < 3) {
                usernameField.setStyle(INVALID_STYLE);
                usernameHint.setStyle(INVALID_TEXT_STYLE);
                usernameHint.setText("✗ Username must be at least 3 characters");
            } else {
                usernameField.setStyle(VALID_STYLE);
                usernameHint.setStyle(VALID_TEXT_STYLE);
                usernameHint.setText("✓ Valid username");
            }
        });

        // Password validation - update requirement indicators
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            updatePasswordRequirements(newVal);
            validateConfirmPassword();
        });

        // Confirm password validation
        confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
            validateConfirmPassword();
        });

        // Full name validation - letters and spaces only
        fullNameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                fullNameField.setStyle(NEUTRAL_STYLE);
                fullNameHint.setText("");
            } else if (!isValidFullName(newVal)) {
                fullNameField.setStyle(INVALID_STYLE);
                fullNameHint.setStyle(INVALID_TEXT_STYLE);
                fullNameHint.setText("✗ Letters and spaces only (no numbers or special characters)");
            } else if (newVal.trim().length() < 2) {
                fullNameField.setStyle(INVALID_STYLE);
                fullNameHint.setStyle(INVALID_TEXT_STYLE);
                fullNameHint.setText("✗ Name is too short");
            } else {
                fullNameField.setStyle(VALID_STYLE);
                fullNameHint.setStyle(VALID_TEXT_STYLE);
                fullNameHint.setText("✓ Valid name");
            }
        });

        // Address - no visual validation, just check if not empty during registration

        // Phone validation - exactly 11 digits
        phoneField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                phoneField.setStyle(NEUTRAL_STYLE);
                phoneHint.setText("");
            } else {
                String cleaned = newVal.replaceAll("[\\s\\-\\(\\)]", "");
                if (!cleaned.matches("\\d*")) {
                    phoneField.setStyle(INVALID_STYLE);
                    phoneHint.setStyle(INVALID_TEXT_STYLE);
                    phoneHint.setText("✗ Numbers only (e.g., 05551234567)");
                } else if (cleaned.length() != 11) {
                    phoneField.setStyle(INVALID_STYLE);
                    phoneHint.setStyle(INVALID_TEXT_STYLE);
                    phoneHint.setText("✗ Must be exactly 11 digits (" + cleaned.length() + "/11)");
                } else {
                    phoneField.setStyle(VALID_STYLE);
                    phoneHint.setStyle(VALID_TEXT_STYLE);
                    phoneHint.setText("✓ Valid phone number");
                }
            }
        });

        // Email validation
        emailField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                emailField.setStyle(NEUTRAL_STYLE);
                emailHint.setText("");
            } else if (!ValidationUtils.isValidEmail(newVal)) {
                emailField.setStyle(INVALID_STYLE);
                emailHint.setStyle(INVALID_TEXT_STYLE);
                emailHint.setText("✗ Invalid format (e.g., user@example.com)");
            } else {
                emailField.setStyle(VALID_STYLE);
                emailHint.setStyle(VALID_TEXT_STYLE);
                emailHint.setText("✓ Valid email");
            }
        });
    }

    /**
     * Updates password requirement indicators with tick/cross marks.
     * 
     * @param password The current password value
     */
    private void updatePasswordRequirements(String password) {
        // Length requirement (6+ chars)
        if (password.length() >= 6) {
            pwdLengthReq.setText("✓ At least 6 characters");
            pwdLengthReq.setStyle(VALID_TEXT_STYLE);
        } else {
            pwdLengthReq.setText("✗ At least 6 characters");
            pwdLengthReq.setStyle(INVALID_TEXT_STYLE);
        }

        // Uppercase requirement
        boolean hasUppercase = password.chars().anyMatch(Character::isUpperCase);
        if (hasUppercase) {
            pwdUpperReq.setText("✓ One uppercase letter (A-Z)");
            pwdUpperReq.setStyle(VALID_TEXT_STYLE);
        } else {
            pwdUpperReq.setText("✗ One uppercase letter (A-Z)");
            pwdUpperReq.setStyle(INVALID_TEXT_STYLE);
        }

        // Lowercase requirement
        boolean hasLowercase = password.chars().anyMatch(Character::isLowerCase);
        if (hasLowercase) {
            pwdLowerReq.setText("✓ One lowercase letter (a-z)");
            pwdLowerReq.setStyle(VALID_TEXT_STYLE);
        } else {
            pwdLowerReq.setText("✗ One lowercase letter (a-z)");
            pwdLowerReq.setStyle(INVALID_TEXT_STYLE);
        }

        // Update password field border based on overall validity
        boolean allMet = password.length() >= 6 && hasUppercase && hasLowercase;
        if (password.isEmpty()) {
            passwordField.setStyle(NEUTRAL_STYLE);
        } else if (allMet) {
            passwordField.setStyle(VALID_STYLE);
        } else {
            passwordField.setStyle(INVALID_STYLE);
        }
    }

    /**
     * Validates that confirm password matches password.
     */
    private void validateConfirmPassword() {
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (confirmPassword.isEmpty()) {
            confirmPasswordField.setStyle(NEUTRAL_STYLE);
            confirmPwdHint.setText("");
        } else if (!confirmPassword.equals(password)) {
            confirmPasswordField.setStyle(INVALID_STYLE);
            confirmPwdHint.setStyle(INVALID_TEXT_STYLE);
            confirmPwdHint.setText("✗ Passwords do not match");
        } else {
            confirmPasswordField.setStyle(VALID_STYLE);
            confirmPwdHint.setStyle(VALID_TEXT_STYLE);
            confirmPwdHint.setText("✓ Passwords match");
        }
    }

    /**
     * Validates that a full name contains only letters and spaces.
     * 
     * @param name The name to validate
     * @return true if valid (letters and spaces only)
     */
    private boolean isValidFullName(String name) {
        // Allow only letters (including accented) and spaces
        return name.matches("^[a-zA-ZÀ-ÿ\\s]+$");
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

        // Validate all required fields
        if (username.isEmpty()) {
            showError("Username is required.");
            return;
        }

        if (username.length() < 3) {
            showError("Username must be at least 3 characters.");
            return;
        }

        if (fullName.isEmpty()) {
            showError("Full name is required.");
            return;
        }

        if (!isValidFullName(fullName)) {
            showError("Full name can only contain letters and spaces.");
            return;
        }

        if (address.isEmpty()) {
            showError("Address is required.");
            return;
        }

        if (email.isEmpty()) {
            showError("Email is required.");
            return;
        }

        if (phone.isEmpty()) {
            showError("Phone number is required.");
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

        // Validate email format (must contain @ and . with proper structure)
        if (!ValidationUtils.isValidEmail(email)) {
            showError("Please enter a valid email address (e.g., user@example.com).");
            return;
        }

        // Validate phone number format (exactly 11 digits)
        if (!ValidationUtils.isValidPhone(phone)) {
            showError("Please enter a valid phone number (exactly 11 digits).");
            return;
        }

        // Create user object
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setRole("CUSTOMER");
        newUser.setFullName(fullName);
        newUser.setAddress(address);
        newUser.setPhone(phone);
        newUser.setEmail(email);

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
