package com.fredypalacios.service;

import com.fredypalacios.dao.UserDAO;
import com.fredypalacios.enums.UserRole;
import com.fredypalacios.model.User;
import static com.fredypalacios.utils.ConsoleColors.*;
import static com.fredypalacios.utils.UIMessages.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class UserService {
    private final UserDAO userDAO;
    private final Scanner scanner;

    public UserService(Scanner scanner) {
        this.userDAO = new UserDAO();
        this.scanner = scanner;
    }

    public void showMenu() throws Exception {
        boolean back = false;
        while (!back) {
            clearScreen();
            System.out.println(title(Titles.USER_MANAGEMENT));
            System.out.println("  1. List users");
            System.out.println("  2. Create user");
            System.out.println("  3. Search by ID");
            System.out.println("  0. Back");
            int option = getIntInput(Prefix.OPTION);
            switch (option) {
                case 1 -> listAll();
                case 2 -> create();
                case 3 -> findById();
                case 0 -> back = true;
                default -> {
                    System.out.println(error( Prefix.WARNING + Input.INVALID_OPTION));
                    Thread.sleep(1000);
                }
            }
        }
    }

    private void listAll() throws Exception {
        clearScreen();
        System.out.println(title(Titles.LIST_USER));
        try {
            List<User> users = userDAO.findAll();
            if(users.isEmpty()) {
                System.out.println(warning(Prefix.WARNING + " No users registered"));
            } else {
                printLine();
                System.out.printf("  %-5s %-15s %-25s %-20s %-12s%n",
                        "ID", "USERNAME", "EMAIL", "NAME", "ROLE");
                printLine();
            }

            for (User user : users) {
                String roleIcon = getRoleIcon(user.role());
                System.out.printf("  %-5d %-15s %-25s %-20s %s %s%n",
                        user.id(), user.username(), user.email(),
                        truncate(user.fullName(), 20), roleIcon, user.role());
            }
            printLine();
            System.out.println(info("\n  Total: " + users.size() + " user(s)"));
        } catch (SQLException e) {
            System.out.println(error(Prefix.ERROR + e.getMessage()));
        }
        waitForEnter();
    }

    public void create() throws Exception {
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
            User user = new User(username, password, email, fullName, role);
            loadingAnimation(Status.CREATING, 500);
            if (userDAO.create(user)) {
                System.out.println(success(Prefix.SUCCESS +" User created successfully"));
            } else {
                System.out.println(error(Prefix.WARNING + "Error creating user"));
            }
        } catch (Exception e) {
            System.out.println(error(Prefix.ERROR + e.getMessage()));
        }
        Thread.sleep(1500);
    }

    private void findById() throws Exception {
        clearScreen();
        System.out.println(title(Titles.SEARCH_USER));
        int id = getIntInput("User ID: ");
        try {
            loadingAnimation(Status.SEARCHING, 400);
            User user = userDAO.findById(id);
            if (user != null) {
                System.out.println(success(Prefix.SUCCESS +  " User found:\n"));
                System.out.println(info("  ID:       ") + highlight(String.valueOf(user.id())));
                System.out.println(info("  Username: ") + highlight(user.username()));
                System.out.println(info("  Email:    ") + user.email());
                System.out.println(info("  Name:     ") + user.fullName());
                System.out.println(info("  Role:     ") + getRoleIcon(user.role()) + " " + user.role());
            } else {
                System.out.println(error(Prefix.WARNING + " User not found"));
            }
        } catch (SQLException e) {
            System.out.println(error(Prefix.ERROR + e.getMessage()));
        }
        waitForEnter();
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