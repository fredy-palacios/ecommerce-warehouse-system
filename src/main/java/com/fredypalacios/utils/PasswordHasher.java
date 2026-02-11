package com.fredypalacios.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {

    private static final int LOG_ROUNDS = 12;

    private PasswordHasher() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static String hash(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(LOG_ROUNDS));
    }

    public static boolean verify(String plainTextPassword, String hashedPassword) {
        if (plainTextPassword == null || hashedPassword == null) {
            return false;
        }

        try {
            return BCrypt.checkpw(plainTextPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            return false;
        }

    }

    public static boolean needsRehash(String hashedPassword) {
        try {
            String[] parts = hashedPassword.split("\\$");
            if (parts.length < 4) {
                return true;
            }

            int currentRounds = Integer.parseInt(parts[2]);
            return currentRounds < LOG_ROUNDS;
        } catch (Exception e) {
            return true;
        }
    }
}
