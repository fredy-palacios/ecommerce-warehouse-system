package com.fredypalacios.model;

import com.fredypalacios.enums.UserRole;
import java.time.LocalDateTime;

public record User(
    Integer id,
    String username,
    String password,
    String email,
    String fullName,
    UserRole role,
    LocalDateTime createdAt
) {
    public User {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name cannot be empty");
        }
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
    }

    // Constructor to create new users (without ID or date)
    public User(String username, String password, String email, String fullName, UserRole role) {
        this(0, username, password, email, fullName, role, LocalDateTime.now());
    }

}
