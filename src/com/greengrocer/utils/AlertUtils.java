package com.greengrocer.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

/**
 * Utility class for displaying alert dialogs.
 * Provides methods for common alert types.
 * 
 * @author Group17
 * @version 1.0
 */
public class AlertUtils {

    /**
     * Shows an information alert.
     * 
     * @param title   Alert title
     * @param message Alert message
     */
    public static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows a warning alert.
     * 
     * @param title   Alert title
     * @param message Alert message
     */
    public static void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows an error alert.
     * 
     * @param title   Alert title
     * @param message Alert message
     */
    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows a confirmation dialog.
     * 
     * @param title   Dialog title
     * @param message Dialog message
     * @return true if user clicked OK, false otherwise
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Shows a success message.
     * 
     * @param message Success message
     */
    public static void showSuccess(String message) {
        showInfo("Success", message);
    }

    /**
     * Shows an input validation error.
     * 
     * @param message Validation error message
     */
    public static void showValidationError(String message) {
        showError("Validation Error", message);
    }

    /**
     * Shows a database error.
     * 
     * @param message Error message
     */
    public static void showDatabaseError(String message) {
        showError("Database Error", message);
    }
}
