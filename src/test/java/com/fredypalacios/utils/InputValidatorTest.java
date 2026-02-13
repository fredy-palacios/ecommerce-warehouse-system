package com.fredypalacios.utils;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class InputValidatorTest {

    @Nested
    @DisplayName("SKU Validation Tests")
    class SKUValidationTests {

        @Test
        @DisplayName("Empty or null SKU should throw ValidationException")
        void validateSKU_notEmpty_shouldThrowException() throws ValidationException {
            String[] inputs = {"", null};

            for(String input : inputs) {
                ValidationException exception = assertThrows(ValidationException.class,
                    () -> InputValidator.validateSKU(input));

                assertEquals("SKU cannot be empty", exception.getMessage());
            }
        }

        @Test
        @DisplayName("SKU with invalid characters should throw exception")
        void validateSKU_invalidCharacters_shouldThrowException() {
            String[] inputs = {
                "PROD@123",
                "PROD-.123",
                "11",
                "64374850596088980483298490328648732648723648236643826483"
            };

            for(String input : inputs) {
                ValidationException exception = assertThrows(ValidationException.class,
                    () -> InputValidator.validateSKU(input));

                assertEquals("SKU must be 3-50 uppercase alphanumeric characters or hyphens", exception.getMessage());
            }
        }

        @Test
        @DisplayName("Lowercase valid SKU should be converted to uppercase")
        void validateSKU_shouldConvertToUppercase() throws ValidationException {
            String input = "prod-001";

            String result = InputValidator.validateSKU(input);

            assertEquals("PROD-001", result);
        }
    }


    @Nested
    @DisplayName("Username Validation Tests")
    class UsernameValidationTests {

        @Test
        @DisplayName("Valid username should pass")
        void validateUsername_valid_shouldPass() throws ValidationException {
            String validUsername = "admin123";

            String result = InputValidator.validateUsername(validUsername);

            assertEquals("admin123", result);
        }

        @Test
        @DisplayName("Username with underscore should pass")
        void validateUsername_withUnderscore_shouldPass() throws ValidationException {
            String username = "user_name_123";

            String result = InputValidator.validateUsername(username);

            assertEquals("user_name_123", result);
        }

        @Test
        @DisplayName("Too short username should fail")
        void validateUsername_tooShort_shouldFail() {
            String shortUsername = "ab";  // Minimum 3

            assertThrows(
                ValidationException.class,
                () -> InputValidator.validateUsername(shortUsername)
            );
        }

        @Test
        @DisplayName("Username with spaces should fail")
        void validateUsername_withSpaces_shouldFail() {
            String usernameWithSpaces = "user name";

            assertThrows(
                ValidationException.class,
                () -> InputValidator.validateUsername(usernameWithSpaces)
            );
        }
    }

    @Nested
    @DisplayName("Password Validation Tests")
    class PasswordValidationTests {

        @Test
        @DisplayName("Valid password should pass")
        void validatePassword_valid_shouldPass() throws ValidationException {
            String validPassword = "Admin123";

            String result = InputValidator.validatePassword(validPassword);

            assertEquals("Admin123", result);
        }

        @Test
        @DisplayName("Password without uppercase should fail")
        void validatePassword_noUppercase_shouldFail() {
            String password = "admin123";

            ValidationException exception = assertThrows(
                ValidationException.class,
                () -> InputValidator.validatePassword(password)
            );

            assertTrue(exception.getMessage().contains("uppercase"));
        }

        @Test
        @DisplayName("Password without lowercase should fail")
        void validatePassword_noLowercase_shouldFail() {
            String password = "ADMIN123";

            ValidationException exception = assertThrows(
                ValidationException.class,
                () -> InputValidator.validatePassword(password)
            );

            assertTrue(exception.getMessage().contains("lowercase"));
        }

        @Test
        @DisplayName("Password without number should fail")
        void validatePassword_noNumber_shouldFail() {
            String password = "AdminPass";

            ValidationException exception = assertThrows(
                ValidationException.class,
                () -> InputValidator.validatePassword(password)
            );

            assertTrue(exception.getMessage().contains("number"));
        }

        @Test
        @DisplayName("Too short password should fail")
        void validatePassword_tooShort_shouldFail() {
            String password = "Ad1";  // Less than 8

            assertThrows(
                ValidationException.class,
                () -> InputValidator.validatePassword(password)
            );
        }
    }


    @Nested
    @DisplayName("Email Validation Tests")
    class EmailValidationTests {

        @Test
        @DisplayName("Valid email should pass")
        void validateEmail_valid_shouldPass() throws ValidationException {
            String validEmail = "admin@warehouse.com";

            String result = InputValidator.validateEmail(validEmail);

            assertEquals("admin@warehouse.com", result);
        }

        @Test
        @DisplayName("Email should be converted to lowercase")
        void validateEmail_shouldConvertToLowercase() throws ValidationException {
            String emailWithCaps = "Admin@WareHouse.COM";

            String result = InputValidator.validateEmail(emailWithCaps);

            assertEquals("admin@warehouse.com", result);
        }

        @Test
        @DisplayName("Email without @ should fail")
        void validateEmail_noAtSign_shouldFail() {
            String invalidEmail = "adminwarehouse.com";

            assertThrows(
                ValidationException.class,
                () -> InputValidator.validateEmail(invalidEmail)
            );
        }

        @Test
        @DisplayName("Email without domain should fail")
        void validateEmail_noDomain_shouldFail() {
            String invalidEmail = "admin@";

            assertThrows(
                ValidationException.class,
                () -> InputValidator.validateEmail(invalidEmail)
            );
        }
    }

    @Nested
    @DisplayName("Price Validation Tests")
    class PriceValidationTests {

        @Test
        @DisplayName("Valid price should pass")
        void validatePrice_valid_shouldPass() throws ValidationException {
            double validPrice = 99.99;

            double result = InputValidator.validatePrice(validPrice);

            assertEquals(99.99, result, 0.001);
        }

        @Test
        @DisplayName("Price should round to 2 decimals")
        void validatePrice_shouldRoundToTwoDecimals() throws ValidationException {
            double priceWithManyDecimals = 99.999;

            double result = InputValidator.validatePrice(priceWithManyDecimals);

            assertEquals(100.0, result, 0.001);
        }

        @Test
        @DisplayName("Negative price should fail")
        void validatePrice_negative_shouldFail() {
            double negativePrice = -10.0;

            assertThrows(
                ValidationException.class,
                () -> InputValidator.validatePrice(negativePrice)
            );
        }

        @Test
        @DisplayName("Too large price should fail")
        void validatePrice_tooLarge_shouldFail() {
            double hugePrice = 9999999.99;

            assertThrows(
                ValidationException.class,
                () -> InputValidator.validatePrice(hugePrice)
            );
        }

        @Test
        @DisplayName("Zero price should be valid")
        void validatePrice_zero_shouldPass() throws ValidationException {
            double result = InputValidator.validatePrice(0.0);

            assertEquals(0.0, result, 0.001);
        }
    }

    @Nested
    @DisplayName("Stock Validation Tests")
    class StockValidationTests {

        @Test
        @DisplayName("Valid stock should pass")
        void validateStock_valid_shouldPass() throws ValidationException {
            int validStock = 100;

            int result = InputValidator.validateStock(validStock);

            assertEquals(100, result);
        }

        @Test
        @DisplayName("Negative stock should fail")
        void validateStock_negative_shouldFail() {
            int negativeStock = -5;

            assertThrows(
                ValidationException.class,
                () -> InputValidator.validateStock(negativeStock)
            );
        }

        @Test
        @DisplayName("Too large stock should fail")
        void validateStock_tooLarge_shouldFail() {
            int hugeStock = 2000000;

            assertThrows(
                ValidationException.class,
                () -> InputValidator.validateStock(hugeStock)
            );
        }

        @Test
        @DisplayName("Zero stock should be valid")
        void ValidateStock_zero_shouldPass() throws ValidationException {
            int result = InputValidator.validateStock(0);

            assertEquals(0, result);
        }
    }
}