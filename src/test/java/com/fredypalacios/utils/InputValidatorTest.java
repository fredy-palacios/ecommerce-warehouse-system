package com.fredypalacios.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class InputValidatorTest {

    @Nested
    @DisplayName("SKU Validation Tests")
    class SKUValidationTests {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("ValidateSKU with null or empty should throw ValidationException")
        void validateSKU_nullOrEmpty_shouldThrowException(String sku) {
            ValidationException exception = assertThrows(ValidationException.class,
                    () -> InputValidator.validateSKU(sku)
            );

            assertEquals("SKU cannot be empty", exception.getMessage());
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "PROD@123",     // Invalid: @
            "PROD-.123",    // Invalid: .
            "11",           // Invalid: too short
            "64374850596088980483298490328648732648723648236643826483"    //Invalid: too long
        })
        @DisplayName("ValidateSKU with invalid characters should throw ValidationException")
        void validateSKU_invalidCharacters_shouldThrowException(String invalidSKU) {
            ValidationException exception = assertThrows(
                ValidationException.class,
                () -> InputValidator.validateSKU(invalidSKU)
            );

            assertEquals("SKU must be 3-50 uppercase alphanumeric characters or hyphens",
                    exception.getMessage());
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