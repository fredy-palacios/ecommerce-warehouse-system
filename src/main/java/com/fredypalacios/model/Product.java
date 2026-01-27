package com.fredypalacios.model;

import com.fredypalacios.enums.ProductStatus;
import java.time.LocalDateTime;

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
            throw new IllegalArgumentException("El SKU no puede estar vacío");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        if (price < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        if (reservedStock < 0) {
            throw new IllegalArgumentException("El stock reservado no puede ser negativo");
        }
        if (minStock < 0) {
            throw new IllegalArgumentException("El stock mínimo no puede ser negativo");
        }
        if (status == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo");
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