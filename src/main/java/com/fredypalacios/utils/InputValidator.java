package com.fredypalacios.utils;

import java.util.regex.Pattern;

public class InputValidator {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern SKU_PATTERN = Pattern.compile("^[A-Z0-9-]{3,50}$");

    private InputValidator() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String requiredNotEmpty(String value, String message) throws ValidationException {
        if (value == null || value.isEmpty()) {
            throw new ValidationException(message);
        }

        return value.trim();
    }

    public static String validateUsername(String username) throws ValidationException {

        username = requiredNotEmpty(username, "Username cannot be empty");

        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new ValidationException("Username must be 3-20 alphanumeric characters or underscores");
        }

        return username;
    }

    public static String validatePassword(String password) throws ValidationException {

        password = requiredNotEmpty(password,"Password cannot be empty");

        if (password.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters");
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new ValidationException("Password must contain at least one uppercase letter");
        }

        if (!password.matches(".*[a-z].*")) {
            throw new ValidationException("Password must contain at least one lowercase letter");
        }

        if (!password.matches(".*[0-9].*")) {
            throw new ValidationException("Password must contain at least one number");
        }

        return password;
    }

    public static String validateEmail(String email) throws ValidationException {

        email = requiredNotEmpty(email, "Email cannot be empty").toLowerCase();

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("Invalid email format");
        }

        if (email.length() > 100) {
            throw new ValidationException("Email is too long (max 100 characters)");
        }

        return email;
    }

    public static String validateFullName(String fullName) throws ValidationException {

        fullName = requiredNotEmpty(fullName,"Full name cannot be empty");

        if (fullName.length() < 2) {
            throw new ValidationException("Full name must be at least 2 characters");
        }

        if (fullName.length() > 100) {
            throw new ValidationException("Full name is too long (max 100 characters)");
        }

        if (!fullName.matches("^[a-zA-Z\\s]+$")) {
            throw new ValidationException("Full name can only contain letters and spaces");
        }

        return fullName;
    }

    public static String validateSKU(String sku) throws ValidationException {

        sku = requiredNotEmpty(sku,"SKU cannot be empty" ).toUpperCase();

        if (!SKU_PATTERN.matcher(sku).matches()) {
            throw new ValidationException("SKU must be 3-50 uppercase alphanumeric characters or hyphens");
        }

        return sku;
    }

    public static double validatePrice(double price) throws ValidationException {
        if (price < 0) {
            throw new ValidationException("Price cannot be negative");
        }

        if (price > 999999.99) {
            throw new ValidationException("Price is too large (max 999,999.99)");
        }

        return Math.round(price * 100.0) / 100.0;
    }

    public static int validateStock(int stock) throws ValidationException {
        if (stock < 0) {
            throw new ValidationException("Stock cannot be negative");
        }

        if (stock > 1000000) {
            throw new ValidationException("Stock quantity is too large (max 1,000,000)");
        }

        return stock;
    }

    // Validates generic string field with custom constraints
    public static String validateString(
            String value,
            String fieldName,
            int minLength,
            int maxLength,
            boolean allowEmpty
    ) throws ValidationException {
        if (value == null) {
            throw new ValidationException(fieldName + " cannot be null");
        }

        String trimmed = value.trim();

        if (!allowEmpty && trimmed.isBlank()) {
            throw new ValidationException(fieldName + " cannot be empty");
        }

        if (trimmed.length() < minLength) {
            throw new ValidationException(fieldName + " must be at least " + minLength + " characters");
        }

        if (trimmed.length() > maxLength) {
            throw new ValidationException(fieldName + " is too long (max " + maxLength + " characters)");
        }

        return trimmed;
    }
}
