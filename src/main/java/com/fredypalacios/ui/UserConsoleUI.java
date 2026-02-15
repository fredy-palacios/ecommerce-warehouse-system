package com.fredypalacios.ui;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import static com.fredypalacios.ui.ConsoleColors.*;
import static com.fredypalacios.ui.MessagesUI.*;

import com.fredypalacios.enums.UserRole;
import com.fredypalacios.model.User;
import com.fredypalacios.service.UserService;
import com.fredypalacios.utils.ValidationException;

public class UserConsoleUI {
    private final UserService userService;
    private final Scanner scanner;

    public UserConsoleUI(Scanner scanner) {
        this.userService = new UserService();
        this.scanner = scanner;
    }

    public void showMenu() throws Exception {
        boolean back = false;

        while (!back) {
            clearScreen();
            System.out.println(title(Titles.USER_MANAGEMENT));
            System.out.println("  1. List all users");
            System.out.println("  2. Search by ID");
            System.out.println("  3. Search by username");
            System.out.println("  4. Create user");
            System.out.println("  5. Update user");
            System.out.println("  6. Change password");
            System.out.println("  7. Delete user");
            System.out.println("  0. Back");

            int option = getIntInput(Prefix.OPTION);

            switch (option) {
                case 1 -> listAll();
                case 2 -> searchById();
                case 3 -> searchByUsername();
                case 4 -> create();
                case 5 -> update();
                case 6 -> changePassword();
                case 7 -> delete();
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
        System.out.println(title(Titles.LIST_USER));

        try {
            List<User> users = userService.findAll();

            if (users.isEmpty()) {
                System.out.println(warning(Prefix.WARNING + " No users registered"));
            } else {
                printLine();
                System.out.printf("%-5s %-15s %-25s %-20s %-12s%n", "ID", "USERNAME", "EMAIL", "NAME", "ROLE");
                printLine();

                for (User user : users) {
                    String roleIcon = getRoleIcon(user.role());
                    System.out.printf("%-5d %-15s %-25s %-20s %s %s%n",
                        user.id(), user.username(), user.email(),
                        truncate(user.fullName(), 20), roleIcon, user.role());
                }

                printLine();
                System.out.println(info("\n  Total: " + users.size() + " user(s)"));
            }
        } catch (SQLException e) {
            System.out.println(error(Prefix.ERROR + e.getMessage()));
        }

        waitForEnter();
    }

    private void searchById() throws Exception {
        clearScreen();
        System.out.println(title(Titles.SEARCH_USER_BY_ID));

        int id = getIntInput("User ID: ");

        try {
            loadingAnimation(Status.SEARCHING, 400);

            User user = userService.findById(id);

            if (user != null) {
                displayUserDetails(user);
            } else {
                System.out.println(error(Prefix.WARNING + " User not found"));
            }
        } catch (SQLException e) {
            System.out.println(error(Prefix.ERROR + e.getMessage()));
        }

        waitForEnter();
    }

    private void searchByUsername() throws Exception {
        clearScreen();
        System.out.println(title(Titles.SEARCH_USER_BY_USERNAME));

        System.out.print(info("Username: "));
        String username = scanner.nextLine();

        try {
            loadingAnimation(Status.SEARCHING, 400);

            User user = userService.findByUsername(username);

            if (user != null) {
                displayUserDetails(user);
            } else {
                System.out.println(error(Prefix.WARNING + " User not found"));
            }
        } catch (SQLException e) {
            System.out.println(error(Prefix.ERROR + e.getMessage()));
        }

        waitForEnter();
    }

    private void create() throws Exception {
        clearScreen();
        System.out.println(title(Titles.CREATE_USER));

        System.out.print(info("Username: "));
        String username = scanner.nextLine();

        System.out.print(info("Password: "));
        String password = scanner.nextLine();

        System.out.print(info("Email: "));
        String email = scanner.nextLine();

        System.out.print(info("Full name: "));
        String fullName = scanner.nextLine();

        System.out.println(info("\nRoles:"));
        System.out.println("  1. 👔 MANAGER");
        System.out.println("  2. 📦 PICKER");
        System.out.println("  3. 📥 RECEIVER");
        System.out.println("  4. 📊 CONTROLLER");

        int roleOpt = getIntInput("\nRole (1-4): ");

        UserRole role = switch (roleOpt) {
            case 1 -> UserRole.MANAGER;
            case 2 -> UserRole.PICKER;
            case 3 -> UserRole.RECEIVER;
            case 4 -> UserRole.CONTROLLER;
            default -> UserRole.CONTROLLER;
        };

        try {
            loadingAnimation(Status.CREATING, 500);

            boolean created = userService.create(username, password, email, fullName, role);

            if (created) {
                System.out.println(success(Prefix.SUCCESS + " User created successfully"));
            } else {
                System.out.println(error(Prefix.WARNING + " Error creating user"));
            }

        } catch (ValidationException e) {
            System.out.println(error(Prefix.WARNING + " Validation error: " + e.getMessage()));
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("unique")) {
                System.out.println(error(Prefix.ERROR + " Username or email already exists"));
            } else {
                System.out.println(error(Prefix.ERROR + " Database error: " + e.getMessage()));
            }
        } catch (Exception e) {
            System.out.println(error(Prefix.ERROR + e.getMessage()));
        }

        Thread.sleep(1500);
    }

    private void update() throws Exception {
        clearScreen();
        System.out.println(title(Titles.UPDATE_USER));

        int id = getIntInput("User ID to update: ");

        try {
            User existing = userService.findById(id);

            if (existing == null) {
                System.out.println(error(Prefix.WARNING + " User not found"));
                Thread.sleep(1500);
                return;
            }

            displayUserDetails(existing);

            System.out.println(info("\nEnter new data (press Enter to keep current):"));

            System.out.print(info("Email [" + existing.email() + "]: "));
            String newEmail = scanner.nextLine();
            if (newEmail.isBlank()) {
                newEmail = existing.email();
            }

            System.out.print(info("Full name [" + existing.fullName() + "]: "));
            String newFullName = scanner.nextLine();
            if (newFullName.isBlank()) {
                newFullName = existing.fullName();
            }

            User updated = new User(
                existing.id(), existing.username(), existing.password(), newEmail,
                newFullName, existing.role(), existing.createdAt()
            );

            loadingAnimation(Status.UPDATING, 500);

            boolean result = userService.update(updated);

            if (result) {
                System.out.println(success(Prefix.SUCCESS + " User updated successfully"));
            } else {
                System.out.println(error(Prefix.WARNING + " Error updating user"));
            }
        } catch (SQLException e) {
            System.out.println(error(Prefix.ERROR + e.getMessage()));
        }

        Thread.sleep(1500);
    }

    private void changePassword() throws Exception {
        clearScreen();
        System.out.println(title(Titles.CHANGE_PASSWORD));

        int id = getIntInput("User ID: ");

        try {
            User user = userService.findById(id);

            if (user == null) {
                System.out.println(error(Prefix.WARNING + " User not found"));
                Thread.sleep(1500);
                return;
            }

            System.out.println(info("\nUser: ") + highlight(user.username()));

            System.out.print(info("New password: "));
            String newPassword = scanner.nextLine();

            System.out.print(info("Confirm password: "));
            String confirmPassword = scanner.nextLine();

            if (!newPassword.equals(confirmPassword)) {
                System.out.println(error(Prefix.WARNING + " Passwords do not match"));
                Thread.sleep(1500);
                return;
            }

            loadingAnimation(Status.UPDATING, 500);

            boolean result = userService.updatePassword(id, newPassword);

            if (result) {
                System.out.println(success(Prefix.SUCCESS + " Password updated successfully"));
            } else {
                System.out.println(error(Prefix.WARNING + " Error updating password"));
            }

        } catch (ValidationException e) {
            System.out.println(error(Prefix.WARNING + " Validation error: " + e.getMessage()));
        } catch (SQLException e) {
            System.out.println(error(Prefix.ERROR + e.getMessage()));
        }

        Thread.sleep(1500);
    }

    private void delete() throws Exception {
        clearScreen();
        System.out.println(title(Titles.DELETE_USER));

        int id = getIntInput("User ID to delete: ");

        try {
            User user = userService.findById(id);

            if (user == null) {
                System.out.println(error(Prefix.WARNING + " User not found"));
                Thread.sleep(1500);
                return;
            }

            System.out.println(warning(Prefix.WARNING + " You are about to delete:"));
            displayUserDetails(user);

            System.out.print(error("\nAre you sure? (Y/N): "));
            String confirm = scanner.nextLine();

            if (confirm.equalsIgnoreCase("Y") || confirm.equalsIgnoreCase("YES")) {
                loadingAnimation("Deleting", 500);

                boolean deleted = userService.delete(id);

                if (deleted) {
                    System.out.println(success(Prefix.SUCCESS + " User deleted successfully"));
                } else {
                    System.out.println(error(Prefix.WARNING + " Error deleting user"));
                }
            } else {
                System.out.println(warning("Delete cancelled"));
            }

        } catch (SQLException e) {
            System.out.println(error(Prefix.ERROR + " Database error: " + e.getMessage()));
        }

        Thread.sleep(1500);
    }

    private void displayUserDetails(User user) {
        System.out.println(success( Prefix.SUCCESS + " User found:\n"));
        System.out.println(info("  ID:       ") + highlight(String.valueOf(user.id())));
        System.out.println(info("  Username: ") + highlight(user.username()));
        System.out.println(info("  Email:    ") + user.email());
        System.out.println(info("  Name:     ") + user.fullName());
        System.out.println(info("  Role:     ") + getRoleIcon(user.role()) + " " + user.role());
        System.out.println(info("  Created:  ") + user.createdAt());
    }

    private String getRoleIcon(UserRole role) {
        return switch (role) {
            case MANAGER -> "👔";
            case PICKER -> "📦";
            case RECEIVER -> "📥";
            case CONTROLLER -> "📊";
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

    private void waitForEnter() {
        System.out.println(info(Input.PRESS_ENTER));
        scanner.nextLine();
    }
}