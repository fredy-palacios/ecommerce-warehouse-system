package com.fredypalacios.ui;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import static com.fredypalacios.ui.ConsoleColors.*;
import static com.fredypalacios.ui.MessagesUI.*;

import com.fredypalacios.model.Category;
import com.fredypalacios.service.CategoryService;
import com.fredypalacios.utils.ValidationException;

public class CategoryConsoleUI {

    private final CategoryService categoryService;
    private final Scanner scanner;

    public CategoryConsoleUI(Scanner scanner) {
        this.categoryService = new CategoryService();
        this.scanner = scanner;
    }

    public void showMenu() throws Exception {
        boolean back = false;

        while (!back) {
            clearScreen();
            System.out.println(title(Titles.CATEGORY_MANAGEMENT));
            System.out.println("  1. List all categories");
            System.out.println("  2. List active categories");
            System.out.println("  3. Search by ID");
            System.out.println("  4. Create category");
            System.out.println("  5. Update category");
            System.out.println("  6. Toggle active/inactive");
            System.out.println("  7. Delete category");
            System.out.println("  0. Back");

            int option = getIntInput(Prefix.OPTION);
            switch (option) {
                case 1 -> listAll();
                case 2 -> listActive();
                case 3 -> searchById();
                case 4 -> create();
                case 5 -> update();
                case 6 -> toggleActive();
                case 7 -> delete();
                case 0 -> back = true;
                default -> {
                    System.out.println(error(Input.INVALID_OPTION));
                    Thread.sleep(1000);
                }
            }
        }
    }

    private void listAll() throws Exception {
        clearScreen();
        System.out.println(title(Titles.ALL_CATEGORIES));

        try {
            List<Category> categories = categoryService.findAll();

            if (categories.isEmpty()) {
                System.out.println(warning(Prefix.WARNING + "  No categories registered"));
            } else {
                printLine();
                System.out.printf("  %-5s %-25s %-35s %-10s%n","ID", "NAME", "DESCRIPTION", "STATUS");
                printLine();

                for (Category c : categories) {
                    String status = c.active() ? success(Prefix.SUCCESS + " Active") : error(Prefix.ERROR + " Inactive");
                    System.out.printf("  %-5d %-25s %-35s %s%n", c.id(), c.name(), truncate(c.description(), 35), status);
                }

                printLine();
                System.out.println(info("\n  Total: " + categories.size() + " category(ies)"));
            }
        } catch (SQLException e) {
            System.out.println(error(Prefix.ERROR + e.getMessage()));
        }

        waitForEnter();
    }

    private void listActive() throws Exception {
        clearScreen();
        System.out.println(title(Titles.ACTIVE_CATEGORIES));

        try {
            List<Category> categories = categoryService.findAllActive();

            if (categories.isEmpty()) {
                System.out.println(warning(Prefix.WARNING + "  No active categories found"));
            } else {
                printLine();
                System.out.printf("%-5s %-25s %-35s%n", "ID", "NAME", "DESCRIPTION");
                printLine();

                for (Category c : categories) {
                    System.out.printf("%-5d %-25s %-35s%n", c.id(), c.name(), truncate(c.description(), 35));
                }

                printLine();
                System.out.println(info("\n  Total: " + categories.size() + " active category(ies)"));
            }
        } catch (SQLException e) {
            System.out.println(error(Prefix.ERROR + e.getMessage()));
        }

        waitForEnter();
    }

    private void searchById() throws Exception {
        clearScreen();
        System.out.println(title(Titles.SEARCH_CATEGORY));

        int id = getIntInput("Category ID: ");

        try {
            loadingAnimation(Status.SEARCHING, 400);

            Category category = categoryService.findById(id);

            if (category != null) {
                System.out.println(success(Prefix.SUCCESS + " Category found:\n"));
                System.out.println(info("  ID:          ") + highlight(String.valueOf(category.id())));
                System.out.println(info("  Name:        ") + highlight(category.name()));
                System.out.println(info("  Description: ") + category.description());
                System.out.println(info("  Status:      ") +
                        (category.active() ? success("Active") : warning("Inactive")));
            } else {
                System.out.println(error(Prefix.WARNING + " Category not found"));
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
            loadingAnimation(Status.CREATING + " category", 500);

            boolean created = categoryService.create(name, description);

            if (created) {
                System.out.println(success(Prefix.SUCCESS + " Category created successfully"));
            } else {
                System.out.println(error(Prefix.WARNING + " Error creating category"));
            }
        } catch (ValidationException e) {
            System.out.println(error(Prefix.WARNING + " Validation error: " + e.getMessage()));
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("unique constraint")) {
                System.out.println(error(Prefix.ERROR + " Category name already exists"));
            } else {
                System.out.println(error(Prefix.ERROR + " Database error"));
            }
        } catch (Exception e) {
            System.out.println(error(Prefix.ERROR + e.getMessage()));
        }

        Thread.sleep(1500);
    }

    private void update() throws Exception {
        clearScreen();
        System.out.println(title(Titles.UPDATE_CATEGORY));

        int id = getIntInput("Category ID to update: ");

        try {
            Category existing = categoryService.findById(id);

            if (existing == null) {
                System.out.println(error(Prefix.WARNING + " Category not found"));
                Thread.sleep(1500);
                return;
            }

            System.out.println(info("\nCurrent data:"));
            System.out.println(info("  Name:        ") + existing.name());
            System.out.println(info("  Description: ") + existing.description());
            System.out.println(info("  Status:      ") +
                    (existing.active() ? success("Active") : warning("Inactive")));

            System.out.println(info("\nEnter new data (press Enter to keep current value):"));

            System.out.print(info("New name [" + existing.name() + "]: "));
            String newName = scanner.nextLine();
            if (newName.isBlank()) {
                newName = existing.name();
            }

            System.out.print(info("New description [" + existing.description() + "]: "));
            String newDescription = scanner.nextLine();
            if (newDescription.isBlank()) {
                newDescription = existing.description();
            }

            System.out.print(info("Active (Y/N) [" + (existing.active() ? "Y" : "N") + "]: "));
            String activeInput = scanner.nextLine();
            boolean newActive = existing.active();
            if (!activeInput.isBlank()) {
                newActive = activeInput.equalsIgnoreCase("Y") || activeInput.equalsIgnoreCase("YES");
            }

            Category updated = new Category(id, newName, newDescription, newActive);

            loadingAnimation(Status.UPDATING, 500);

            boolean result = categoryService.update(updated);

            if (result) {
                System.out.println(success(Prefix.SUCCESS + " Category updated successfully"));
            } else {
                System.out.println(error(Prefix.WARNING + " Error updating category"));
            }
        } catch (SQLException e) {
            System.out.println(error(Prefix.ERROR + " Database error: " + e.getMessage()));
        } catch (Exception e) {
            System.out.println(error(Prefix.ERROR + e.getMessage()));
        }

        Thread.sleep(1500);
    }

    private void toggleActive() throws Exception {
        clearScreen();
        System.out.println(title(Titles.TOGGLE_CATEGORY_STATUS));

        int id = getIntInput("Category ID: ");

        try {
            Category existing = categoryService.findById(id);

            if (existing == null) {
                System.out.println(error(Prefix.WARNING + " Category not found"));
                Thread.sleep(1500);
                return;
            }

            System.out.println(info("\nCurrent status: ") +
                    (existing.active() ? success("Active") : warning("Inactive")));
            System.out.println(info("New status will be: ") +
                    (!existing.active() ? success("Active") : warning("Inactive")));

            System.out.print(warning("\nConfirm toggle? (Y/N): "));
            String confirm = scanner.nextLine();

            if (confirm.equalsIgnoreCase("Y") || confirm.equalsIgnoreCase("YES")) {
                loadingAnimation(Status.UPDATING, 500);

                boolean result = categoryService.toggleActive(id);

                if (result) {
                    System.out.println(success(Prefix.SUCCESS + " Category status updated"));
                } else {
                    System.out.println(error(Prefix.WARNING + " Error updating status"));
                }
            } else {
                System.out.println(warning("Operation cancelled"));
            }
        } catch (SQLException e) {
            System.out.println(error(Prefix.ERROR + e.getMessage()));
        }

        Thread.sleep(1500);
    }

    private void delete() throws Exception {
        clearScreen();
        System.out.println(title(Titles.DELETE_CATEGORY));

        int id = getIntInput("Category ID to delete: ");

        try {
            Category existing = categoryService.findById(id);

            if (existing == null) {
                System.out.println(error(Prefix.WARNING + " Category not found"));
                Thread.sleep(1500);
                return;
            }

            System.out.println(warning(Prefix.WARNING + " You are about to delete:"));
            System.out.println(info("  ID:   ") + existing.id());
            System.out.println(info("  Name: ") + existing.name());

            System.out.print(error("\nAre you sure? (Y/N): "));
            String confirm = scanner.nextLine();

            if (confirm.equalsIgnoreCase("Y") || confirm.equalsIgnoreCase("YES")) {
                loadingAnimation("Deleting", 500);

                boolean deleted = categoryService.delete(id);

                if (deleted) {
                    System.out.println(success(Prefix.SUCCESS + " Category deleted successfully"));
                } else {
                    System.out.println(error(Prefix.WARNING + " Error deleting category"));
                }
            } else {
                System.out.println(warning("Delete cancelled"));
            }

        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("foreign key")) {
                System.out.println(error(Prefix.ERROR + " Cannot delete: Category is being used by products"));
            } else {
                System.out.println(error(Prefix.ERROR + " Database error: " + e.getMessage()));
            }
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
