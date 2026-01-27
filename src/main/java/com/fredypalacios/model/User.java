package com.fredypalacios.model;

import com.fredypalacios.enums.UserRole;
import java.time.LocalDateTime;

public record User(
    int id,
    String username,
    String password,
    String email,
    String fullName,
    UserRole role,
    LocalDateTime createdAt
) {
    public User {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("El username no puede estar vacío");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Email inválido");
        }
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("El nombre completo no puede estar vacío");
        }
        if (role == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }
    }

    // Constructor to create new users (without ID or date)
    public User(String username, String password, String email, String fullName, UserRole role) {
        this(0, username, password, email, fullName, role, LocalDateTime.now());
    }

}
