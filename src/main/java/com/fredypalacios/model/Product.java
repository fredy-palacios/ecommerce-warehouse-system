package com.fredypalacios.model;

import java.time.LocalDateTime;

import com.fredypalacios.enums.ProductStatus;

public record Product(
    int id,
    String sku,
    String name,
    String description,
    double price,
    int stock,
    int reservedStock,
    int minStock,
    String location,
    ProductStatus status,
    int categoryId,
    LocalDateTime lastUpdate
) {
    public Product {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("SKU cannot be empty");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        if (reservedStock < 0) {
            throw new IllegalArgumentException("Reserved stock cannot be negative");
        }
        if (minStock < 0) {
            throw new IllegalArgumentException("Minimum stock cannot be negative");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
    }

    // Constructor to create new products (without ID, without reserved stock, without date)
    public Product(String sku, String name, String description, double price,
                   int stock, int minStock, String location, int categoryId) {
        this(0, sku, name, description, price, stock, 0, minStock, location,
                calculateStatus(stock, minStock), categoryId, LocalDateTime.now());
    }

    private static ProductStatus calculateStatus(int stock, int minStock) {
        if (stock == 0) {
            return ProductStatus.OUT_OF_STOCK;
        } else if (stock <= minStock) {
            return ProductStatus.LOW_STOCK;
        } else {
            return ProductStatus.AVAILABLE;
        }
    }

    public boolean needsRestock() {
        return stock <= minStock;
    }

    // Gets the actual available stock (subtracting reserved stock)
    public int getAvailableStock() {
        return stock - reservedStock;
    }
}