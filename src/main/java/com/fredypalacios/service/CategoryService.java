package com.fredypalacios.service;

import com.fredypalacios.dao.CategoryDAO;
import com.fredypalacios.model.Category;
import static com.fredypalacios.utils.ConsoleColors.*;
import static com.fredypalacios.utils.UIMessages.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class CategoryService {
    private final CategoryDAO categoryDAO;
    private final Scanner scanner;

    public CategoryService(Scanner scanner) {
        this.categoryDAO = new CategoryDAO();
        this.scanner = scanner;
    }

    public void showMenu() throws Exception {
        boolean back = false;

        while (!back) {
            clearScreen();
            System.out.println(title(Titles.CATEGORY_MANAGEMENT ));
            System.out.println("  1. List categories");
            System.out.println("  2. Create category");
            System.out.println("  0. Back");

            int option = getIntInput("\n Option: ");
            switch (option) {
                case 1 -> listAll();
                case 2 -> create();
                case 0 -> back = true;
                default -> {
                    System.out.println(error(Input.INVALID_OPTION ));
                    Thread.sleep(1000);
                }
            }
        }
    }

    private void listAll() throws Exception {
        clearScreen();
        System.out.println(title(Titles.CATEGORY_MANAGEMENT));
        try {
            List<Category> categories = categoryDAO.findAll();

            if (categories.isEmpty()) {
                System.out.println(warning(Prefix.WARNING + "  No categories registered"));
            } else {
                printLine();
                System.out.printf("  %-5s %-25s %-35s %-10s%n",
                        "ID", "NAME", "DESCRIPTION", "STATUS");
                printLine();

                for (Category c : categories) {
                    String status = c.active() ? success( Prefix.SUCCESS+ " Active") : error( Prefix.ERROR + " Inactive");
                    System.out.printf("  %-5d %-25s %-35s %s%n",
                            c.id(), c.name(), truncate(c.description(), 35), status);
                }

                printLine();
                System.out.println(info("Total: " + categories.size() + " category(ies)"));
            }
        } catch (SQLException e) {
            System.out.println(error(Prefix.ERROR + e.getMessage()));
        }
        waitForEnter();
    }

    private void create() throws Exception {
        clearScreen();
        System.out.println(title(Titles.CREATE_CATEGORY));

        System.out.print(info("Name: "));
        String name = scanner.nextLine();
        System.out.print(info("Description: "));
        String description = scanner.nextLine();

        try {
            Category category = new Category(name, description);

            loadingAnimation("Creating category", 500);

            if (categoryDAO.create(category)) {
                System.out.println(success(Prefix.SUCCESS +" Category created successfully"));
            } else {
                System.out.println(error(Prefix.WARNING + " Error creating category"));
            }
        } catch (Exception e) {
            System.out.println(error(Prefix.ERROR + e.getMessage()));
        }
        Thread.sleep(1500);
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
                System.out.println(error(Prefix.ERROR + " Enter a valid number"));
            }
        }
    }

    private void waitForEnter() {
        System.out.println(info(Input.PRESS_ENTER));
        scanner.nextLine();
    }
}