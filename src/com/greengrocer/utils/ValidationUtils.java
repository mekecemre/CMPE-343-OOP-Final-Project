package com.greengrocer.utils;

/**
 * Utility class for input validation.
 * Provides methods to validate user inputs.
 * 
 * @author Group17
 * @version 1.0
 */
public class ValidationUtils {

    /**
     * Validates that a string is not null or empty.
     * 
     * @param value The string to validate
     * @return true if valid (not null and not empty)
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Validates that a string can be parsed as a positive double.
     * 
     * @param value The string to validate
     * @return true if valid positive double
     */
    public static boolean isValidPositiveDouble(String value) {
        if (!isNotEmpty(value)) {
            return false;
        }

        try {
            double parsed = Double.parseDouble(value.trim());
            return parsed > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Parses a string to double, returning 0 if invalid.
     * 
     * @param value The string to parse
     * @return Parsed double or 0 if invalid
     */
    public static double parseDouble(String value) {
        if (!isNotEmpty(value)) {
            return 0;
        }

        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Parses a string to int, returning 0 if invalid.
     * 
     * @param value The string to parse
     * @return Parsed int or 0 if invalid
     */
    public static int parseInt(String value) {
        if (!isNotEmpty(value)) {
            return 0;
        }

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Validates that a double value is positive (greater than 0).
     * 
     * @param value The value to validate
     * @return true if positive
     */
    public static boolean isPositive(double value) {
        return value > 0;
    }

    /**
     * Validates that an int value is positive (greater than 0).
     * 
     * @param value The value to validate
     * @return true if positive
     */
    public static boolean isPositive(int value) {
        return value > 0;
    }

    /**
     * Validates that an email has basic format.
     * 
     * @param email The email to validate
     * @return true if basic format is valid
     */
    public static boolean isValidEmail(String email) {
        if (!isNotEmpty(email)) {
            return false;
        }

        // Basic email validation - contains @ and .
        return email.contains("@") && email.contains(".");
    }

    /**
     * Validates that a phone number contains only digits and is reasonable length.
     * 
     * @param phone The phone number to validate
     * @return true if valid format
     */
    public static boolean isValidPhone(String phone) {
        if (!isNotEmpty(phone)) {
            return false;
        }

        // Remove common separators
        String cleaned = phone.replaceAll("[\\s\\-\\(\\)]", "");

        // Check if remaining is digits and reasonable length
        return cleaned.matches("\\d{7,15}");
    }

    /**
     * Formats a price for display.
     * 
     * @param price The price value
     * @return Formatted price string
     */
    public static String formatPrice(double price) {
        return String.format("$%.2f", price);
    }

    /**
     * Formats a quantity for display.
     * 
     * @param quantity The quantity in kg
     * @return Formatted quantity string
     */
    public static String formatQuantity(double quantity) {
        return String.format("%.2f kg", quantity);
    }
}
