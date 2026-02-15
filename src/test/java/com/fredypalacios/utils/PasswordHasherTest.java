package com.fredypalacios.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

public class PasswordHasherTest {

    @Nested
    @DisplayName("Password hash tests")
    class HashTests {

        @Test
        @DisplayName("Hash should generate a valid BCrypt string")
        void hash_shouldGenerateValidBCrypt() {
            String plainPassword = "Admin123";

            String hashedPassword = PasswordHasher.hash(plainPassword);

            assertNotNull(hashedPassword);
            assertTrue(hashedPassword.startsWith("$2a$"));
            assertEquals(60, hashedPassword.length());
        }

        @Test
        @DisplayName("Same password should generate different hashes")
        void hash_samePasswordDifferentHashes() {
            String password = "Admin123";

            String hash1 = PasswordHasher.hash(password);
            String hash2 = PasswordHasher.hash(password);

            assertNotEquals(hash1, hash2);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Hash with null or empty password should throw IllegalArgumentException")
        void hash_nullOrEmpty_shouldThrowException(String password) {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> PasswordHasher.hash(password)
            );

            assertEquals("Password cannot be null or empty", exception.getMessage());
        }

        @Test
        @DisplayName("Password with only spaces should throw exception")
        void hash_blankPassword_shouldThrowException() {
            String blankPassword = "  ";

            assertThrows(
                IllegalArgumentException.class,
                () -> PasswordHasher.hash(blankPassword)
            );
        }
    }

    @Nested
    @DisplayName("Password verification tests")
    class VerifyTests {

        @Test
        @DisplayName("Correct password should verify successfully")
        void verify_correctPassword_shouldReturnTrue() {
            String plainPassword = "Admin123";
            String hashedPassword = PasswordHasher.hash(plainPassword);

            boolean result = PasswordHasher.verify(plainPassword, hashedPassword);

            assertTrue(result);
        }

        @Test
        @DisplayName("Incorrect password should fail verification")
        void verify_wrongPassword_shouldReturnFalse() {
            String correctPassword = "Admin123";
            String wrongPassword = "WrongPass123";
            String hashedPassword = PasswordHasher.hash(correctPassword);

            boolean result = PasswordHasher.verify(wrongPassword, hashedPassword);

            assertFalse(result);
        }

        @Test
        @DisplayName("Invalid hash should return false")
        void verify_invalidHash_shouldReturnFalse() {
            String invalidHash = "not-a-valid-bcrypt-hash";

            boolean result = PasswordHasher.verify("Admin123", invalidHash);

            assertFalse(result);
        }

        @Test
        @DisplayName("Password is case-sensitive")
        void verify_caseSensitive() {
            String password = "Admin123";
            String hashedPassword = PasswordHasher.hash(password);

            boolean correctCase = PasswordHasher.verify("Admin123", hashedPassword);
            boolean wrongCase = PasswordHasher.verify("admin123", hashedPassword);

            assertTrue(correctCase);
            assertFalse(wrongCase);
        }
    }

    @Nested
    @DisplayName("needsRehash tests")
    class NeedsRehashTests {

        @Test
        @DisplayName("Hash with current rounds does not need rehash")
        void needsRehash_currentRounds_shouldReturnFalse() {
            String password = "Admin123";
            String hash = PasswordHasher.hash(password);

            boolean needsRehash = PasswordHasher.needsRehash(hash);

            assertFalse(needsRehash);
        }

        @Test
        @DisplayName("Hash with old rounds needs rehash")
        void needsRehash_oldRounds_shouldReturnTrue() {
            String oldHash = "$2a$10$abc123...";

            boolean needsRehash = PasswordHasher.needsRehash(oldHash);

            assertTrue(needsRehash);
        }

        @Test
        @DisplayName("Invalid hash needs rehash")
        void needsRehash_invalidHash_shouldReturnTrue() {
            String invalidHash = "invalid";

            boolean needsRehash = PasswordHasher.needsRehash(invalidHash);

            assertTrue(needsRehash);
        }
    }

    @Nested
    @DisplayName("Integration tests")
    class IntegrationTests {

        @Test
        @DisplayName("Full cycle: hash -> verify -> rehash if needed")
        void testFullCycle() {
            String originalPassword = "Admin123";

            String hash1 = PasswordHasher.hash(originalPassword);
            assertNotNull(hash1);

            assertTrue(PasswordHasher.verify(originalPassword, hash1));

            assertFalse(PasswordHasher.needsRehash(hash1));

            assertFalse(PasswordHasher.verify("WrongPassword", hash1));
        }

        @Test
        @DisplayName("Multiple users with same password have different hashes")
        void testMultipleUsers_samePassword_differentHashes() {
            String password = "CommonPassword123";

            // same password, different hashes
            String hash1 = PasswordHasher.hash(password);
            String hash2 = PasswordHasher.hash(password);
            String hash3 = PasswordHasher.hash(password);

            assertNotEquals(hash1, hash2);
            assertNotEquals(hash2, hash3);
            assertNotEquals(hash1, hash3);

            // all hashes verify correctly
            assertTrue(PasswordHasher.verify(password, hash1));
            assertTrue(PasswordHasher.verify(password, hash2));
            assertTrue(PasswordHasher.verify(password, hash3));
        }
    }
}