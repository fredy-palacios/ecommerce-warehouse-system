package com.fredypalacios.service;

import com.fredypalacios.dao.CategoryDAO;
import com.fredypalacios.dao.ProductDAO;
import com.fredypalacios.model.Category;
import com.fredypalacios.model.Product;
import com.fredypalacios.utils.InputValidator;
import com.fredypalacios.utils.ValidationException;
import static com.fredypalacios.utils.ConsoleColors.*;
import static com.fredypalacios.utils.UIMessages.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ProductService {
    private final ProductDAO productDAO;
    private final CategoryDAO categoryDAO;
    private final Scanner scanner;

    public ProductService(Scanner scanner) {
        this.productDAO = new ProductDAO();
        this.categoryDAO = new CategoryDAO();
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
                //case 3 -> findBySku();
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

    private void listAll() throws Exception {
        clearScreen();

        try {
            List<Product> products = productDAO.findAll();
            if (products.isEmpty()) {
                System.out.println(warning(Prefix.WARNING + "  No products registered"));
            } else {
                printLine();
                System.out.printf("  %-5s %-12s %-25s %-10s %-8s%n",
                        "ID", "SKU", "NAME", "PRICE", "STOCK");
                printLine();
                for (Product p : products) {
                    System.out.printf("  %-5d %-12s %-25s $%-9.2f %-8d%n",
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
            List<Category> categories = categoryDAO.findAllActive();
            if (categories.isEmpty()) {
                System.out.println(error("No categories. Create one first."));
                Thread.sleep(2000);
                return;
            }

            System.out.println(info("Available categories:"));
            for (Category category : categories) {
                System.out.println("  " + highlight(category.id() + ".") + " " + category.name());
            }
            System.out.println();

            System.out.print(info("SKU: "));
            String sku = scanner.nextLine();
            System.out.print(info("Name: "));
            String name = scanner.nextLine();
            System.out.print(info("Description: "));
            String description = scanner.nextLine();

            double price = getDoubleInput("Price: $");
            int stock = getIntInput("Initial stock: ");
            int minStock = getIntInput("Minimum stock: ");

            System.out.print(info("Location (e.g., A-12-3): "));
            String location = scanner.nextLine();

            int categoryId = getIntInput("Category ID: ");

            String validSku = InputValidator.validateSKU(sku);
            String validName = InputValidator.validateString(name, "Product name", 2, 100, false);
            String validDescription = InputValidator.validateString(description, "Description", 0, 255, true);
            double validPrice = InputValidator.validatePrice(price);
            int validStock = InputValidator.validateStock(stock);
            int validMinStock = InputValidator.validateStock(minStock);
            String validLocation = InputValidator.validateString(location, "Location", 1, 20, true);

            Product product = new Product(
                validSku,
                validName,
                validDescription,
                validPrice,
                validStock,
                validMinStock,
                validLocation,
                categoryId
            );

            loadingAnimation( Status.CREATING +" product", 500);

            if (productDAO.create(product)) {
                System.out.println(success(Prefix.SUCCESS +" Product created successfully"));
            } else {
                System.out.println(error(Prefix.WARNING +" Error creating product"));
            }
        } catch (ValidationException e) {
            System.out.println(error(Prefix.WARNING + " Validation error: " + e.getMessage()));

        } catch (SQLException e) {
            if(e.getMessage() != null && e.getMessage().contains("unique constraint")) {
                System.out.println(error(Prefix.ERROR + " SKU already exists"));
            } else {
                System.out.println(error(Prefix.ERROR + "Database error"));
            }

        } catch (Exception e) {
            System.out.println(error(Prefix.ERROR + e.getMessage()));
        }

        Thread.sleep(1500);
    }

    //REFACTOR
    /*
    private void findBySku() throws Exception {
        clearScreen();
        System.out.println(title(Titles.SEARCH_PRODUCT));
        System.out.print(info("SKU: "));
        String sku = scanner.nextLine();

        try {
            loadingAnimation("Searching", 400);
            if (p != null) {
                System.out.println(success("\n✓ Product found:\n"));
                System.out.println(info("  SKU:    ") + highlight(p.sku()));
                System.out.println(info("  Name:   ") + highlight(p.name()));
                System.out.println(info("  Price:  ") + success("$" + p.price()));
                System.out.println(info("  Stock:  ") + success(String.valueOf(p.stock())));
            } else {
                System.out.println(error(Prefix.WARNING + "Product not found"));
            }
        } catch (SQLException e) {
            System.out.println(error(Prefix.ERROR + e.getMessage()));
        }
        waitForEnter();
    }
     */

    private void updateStock() throws Exception {
        clearScreen();
        System.out.println(title(Titles.UPDATE_STOCK));

        int id = getIntInput("Product ID: ");

        try {
            Product p = productDAO.findById(id);
            if (p == null) {
                System.out.println(error(Prefix.WARNING + " Product not found"));
                Thread.sleep(1500);
                return;
            }

            System.out.println(info("\nProduct: ") + highlight(p.name()));
            System.out.println(info("Current stock: ") + success(String.valueOf(p.stock())));

            int newStock = getIntInput("\nNew stock: ");

            loadingAnimation(Status.UPDATING, 500);

            if (productDAO.updateStock(id, newStock)) {
                System.out.println(success(Prefix.SUCCESS + " Stock updated"));

                if (newStock <= p.minStock()) {
                    System.out.println(warning(Prefix.WARNING + " Alert: Low stock."));
                }
            } else {
                System.out.println(error(Prefix.WARNING + " Error updating"));
            }

        } catch (SQLException e) {
            System.out.println(error(Prefix.ERROR + e.getMessage()));
        }

        Thread.sleep(2000);
    }

    private void showLowStock() throws Exception {
        clearScreen();
        System.out.println(warning(Titles.LOW_STOCK_PRODUCTS));

        try {
            List<Product> products = productDAO.findLowStockProducts();

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
}
