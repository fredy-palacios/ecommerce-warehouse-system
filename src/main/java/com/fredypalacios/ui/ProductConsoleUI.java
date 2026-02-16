package com.fredypalacios.ui;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import static com.fredypalacios.ui.utils.ConsoleColors.*;
import static com.fredypalacios.ui.utils.MessagesUI.*;

import com.fredypalacios.model.Category;
import com.fredypalacios.model.Product;
import com.fredypalacios.service.CategoryService;
import com.fredypalacios.service.ProductService;
import com.fredypalacios.utils.ValidationException;

public class ProductConsoleUI {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final Scanner scanner;

    public ProductConsoleUI(Scanner scanner) {
        this.productService =  new ProductService();
        this.categoryService = new CategoryService();
        this.scanner = scanner;
    }

    public void showMenu() throws Exception {
        boolean back = false;

        while (!back) {
            clearScreen();
            System.out.println(title(Titles.PRODUCT_MANAGEMENT));
            System.out.println("  1. List products");
            System.out.println("  2. Create product");
            System.out.println("  3. Search by SKU");
            System.out.println("  4. Update stock");
            System.out.println("  5. Low stock products");
            System.out.println("  0. Back");

            int option = getIntInput(Prefix.OPTION);

            switch (option) {
                case 1 -> listAll();
                case 2 -> create();
                case 3 -> searchBySku();
                case 4 -> updateStock();
                case 5 -> showLowStock();
                case 0 -> back = true;
                default -> {
                    System.out.println(error(Prefix.WARNING + Input.INVALID_OPTION));
                    Thread.sleep(1000);
                }
            }
        }
    }

    private void listAll() {
        clearScreen();

        try {
            List<Product> products = productService.findAll();
            if (products.isEmpty()) {
                System.out.println(warning(Prefix.WARNING + "  No products registered"));
            } else {
                printLine();
                System.out.printf("%-5s %-12s %-25s %-10s %-8s%n",
                        "ID", "SKU", "NAME", "PRICE", "STOCK");
                printLine();
                for (Product p : products) {
                    System.out.printf("%-5d %-12s %-25s $%-9.2f %-8d%n",
                            p.id(), p.sku(), truncate(p.name(), 25), p.price(), p.stock());
                }
                printLine();
                System.out.println(info("\n  Total: " + products.size() + " product(s)"));
            }
        } catch (SQLException e) {
            System.out.println(error(Prefix.ERROR + e.getMessage()));
        }
        waitForEnter();
    }

    private void create() throws Exception {
        clearScreen();
        System.out.println(title(Titles.CREATE_PRODUCT));

        try {
            List<Category> categories = categoryService.findAllActive();
            if (categories.isEmpty()) {
                System.out.println(error("No categories. Create one first."));
                Thread.sleep(2000);
                return;
            }

            displayAvailableCategories(categories);

            String sku = promptInput("SKU: ");
            String name = promptInput("Name: ");
            String description = promptInput("Description: ");
            double price = getDoubleInput("Price: $");
            int stock = getIntInput("Initial stock: ");
            int minStock = getIntInput("Minimum stock: ");
            String location = promptInput("Location (e.g., A-12-3): ");
            int categoryId = getIntInput("Category ID: ");

            loadingAnimation(Status.CREATING + " product", 500);

            if (productService.create(sku, name, description, price, stock, minStock, location, categoryId)) {
                System.out.println(success(Prefix.SUCCESS + " Product created successfully"));
            } else {
                System.out.println(error(Prefix.WARNING + " Error creating product"));
            }
        } catch (ValidationException e) {
            System.out.println(error(Prefix.WARNING + " Validation error: " + e.getMessage()));

        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("unique constraint")) {
                System.out.println(error(Prefix.ERROR + " SKU already exists"));
            } else {
                System.out.println(error(Prefix.ERROR + " Database error"));
            }
        } catch (Exception e) {
            System.out.println(error(Prefix.ERROR + e.getMessage()));
        }

        Thread.sleep(1500);
    }

    private void searchBySku() throws Exception {
        clearScreen();
        System.out.println(title(Titles.SEARCH_BY_SKU));
        System.out.print(info("SKU: "));
        String sku = scanner.nextLine();

        try {
            loadingAnimation(Status.SEARCHING, 400);
            Product p = productService.findBySku(sku);

            if (p != null) {
                System.out.println(success("\n✓ Product found:\n"));
                displayProductDetails(p);
            } else {
                System.out.println(error(Prefix.WARNING + " Product not found"));
            }
        } catch (SQLException e) {
            System.out.println(error(Prefix.ERROR + e.getMessage()));
        }
        waitForEnter();
    }

    private void updateStock() throws Exception {
        clearScreen();
        System.out.println(title(Titles.UPDATE_STOCK));

        int id = getIntInput("Product ID: ");

        try {
            Product p = productService.findById(id);
            if (p == null) {
                System.out.println(error(Prefix.WARNING + " Product not found"));
                Thread.sleep(1500);
                return;
            }

            System.out.println(info("\nProduct: ") + highlight(p.name()));
            System.out.println(info("Current stock: ") + success(String.valueOf(p.stock())));

            int newStock = getIntInput("\nNew stock: ");

            loadingAnimation(Status.UPDATING, 500);

            boolean updated = productService.updateStock(id, newStock);

            if (updated) {
                System.out.println(success(Prefix.SUCCESS + " Stock updated"));

                if (newStock <= p.minStock()) {
                    System.out.println(warning(Prefix.WARNING + " Alert: Low stock."));
                }
            } else {
                System.out.println(error(Prefix.WARNING + " Error updating"));
            }

        } catch (ValidationException e) {
            System.out.println(error(Prefix.WARNING + " Validation: " + e.getMessage()));
        } catch (SQLException e) {
            System.out.println(error(Prefix.ERROR + e.getMessage()));
        }

        Thread.sleep(2000);
    }

    private void showLowStock() {
        clearScreen();
        System.out.println(warning(Titles.LOW_STOCK_PRODUCTS));

        try {
            List<Product> products = productService.getLowStockProducts();

            if (products.isEmpty()) {
                System.out.println(success(Prefix.SUCCESS + " No low stock products"));
            } else {
                printLine();
                System.out.printf("  %-12s %-30s %-8s %-8s%n", "SKU", "NAME", "STOCK", "MIN");
                printLine();

                for (Product p : products) {
                    System.out.printf("  %-12s %-30s %s%-8d%s %-8d%n",
                            p.sku(), truncate(p.name(), 30), warning(""), p.stock(), "", p.minStock());
                }

                printLine();
                System.out.println(warning("\n  Total: " + products.size() + " product(s)"));
            }
        } catch (SQLException e) {
            System.out.println(error(Prefix.ERROR + e.getMessage()));
        }
        waitForEnter();
    }

    private void displayProductDetails(Product p) {
        System.out.println(info("  SKU:         ") + highlight(p.sku()));
        System.out.println(info("  Name:        ") + highlight(p.name()));
        System.out.println(info("  Price:       ") + success("$" + p.price()));
        System.out.println(info("  Stock:       ") + success(String.valueOf(p.stock())));
        System.out.println(info("  Min Stock:   ") + p.minStock());
        System.out.println(info("  Status:      ") + getStatusColor(p.status()));
    }

    private String getStatusColor(com.fredypalacios.enums.ProductStatus status) {
        return switch (status) {
            case AVAILABLE -> success(status.toString());
            case LOW_STOCK -> warning(status.toString());
            case OUT_OF_STOCK -> error(status.toString());
        };
    }

    private String truncate(String text, int max) {
        if (text == null) return "";
        return text.length() > max ? text.substring(0, max - 3) + "..." : text;
    }

    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(highlight(prompt));
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println(error(Prefix.WARNING + Input.INVALID_NUMBER));
            }
        }
    }

    private double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(info(prompt));
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println(error(Prefix.WARNING + Input.INVALID_NUMBER));
            }
        }
    }

    private void waitForEnter() {
        System.out.println(info(Input.PRESS_ENTER));
        scanner.nextLine();
    }

    private String promptInput(String prompt) {
        System.out.print(info(prompt));
        return scanner.nextLine();
    }

    private void displayAvailableCategories(List<Category> categories) {
        System.out.println(info("Available categories:"));
        for (Category category : categories) {
            System.out.println("  " + highlight(category.id() + ".") + " " + category.name());
        }
        System.out.println();
    }
}