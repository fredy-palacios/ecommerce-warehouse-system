package com.fredypalacios.model;

public record Category(
    int id,
    String name,
    String description,
    boolean active
) {
    public Category {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("The category name cannot be empty.");
        }
    }

    // Constructor to create new categories (without ID)
    public Category(String name, String description, boolean active) {
        this(0, name, description, active);
    }

    // Constructor with category active by default
    public Category(String name, String description) {
        this(0, name, description, true);
    }
}
