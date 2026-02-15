package com.fredypalacios.ui;

import com.fredypalacios.service.DashboardService;
import com.fredypalacios.service.ProductService;

import static com.fredypalacios.ui.utils.ConsoleColors.*;
import static com.fredypalacios.ui.utils.MessagesUI.*;


import java.util.Scanner;

public class MenuServiceUI {
    private final Scanner scanner;
    private final UserConsoleUI userConsoleUI;

    private final ProductService productService;
    private final DashboardService dashboardService;
    private final CategoryConsoleUI categoryConsoleUI;

    public MenuServiceUI(Scanner scanner) {
        this.scanner = scanner;
        this.userConsoleUI = new UserConsoleUI(scanner);
        this.productService = new ProductService(scanner);
        this.categoryConsoleUI = new CategoryConsoleUI(scanner);
        this.dashboardService = new DashboardService();

    }

    public void showMainMenu() {
        System.out.println(title("╔══════════════════════════════════════════════════════╗"));
        System.out.println(title("║") + bold("                       MAIN MENU                      ") + title("║"));
        System.out.println(title("╚══════════════════════════════════════════════════════╝\n"));

        System.out.println(info("  1.") + " 👤  User Management");
        System.out.println(info("  2.") + " 📦  Product Management");
        System.out.println(info("  3.") + " 🏷️  Category Management");
        System.out.println(info("  4.") + " 📊  Dashboard");
    }

    public void handleMainMenuOption(int option) throws Exception {
        switch (option) {
            case 1 -> userConsoleUI.showMenu();
            case 2 -> productService.showMenu();
            case 3 -> categoryConsoleUI.showMenu();
            case 4 -> showDashboard();
            case 0 -> {}
            default -> {
                System.out.println(error(Input.INVALID_OPTION));
                Thread.sleep(1000);
            }
        }
    }

    private void showDashboard() throws Exception {
        clearScreen();
        System.out.println(title("╔══════════════════════════════════════════════════════╗"));
        System.out.println(title("║") + bold("                       DASHBOARD                      ") + title("║"));
        System.out.println(title("╚══════════════════════════════════════════════════════╝\n"));

        dashboardService.showStatistics();

        System.out.println(info(Input.PRESS_ENTER));
        scanner.nextLine();
    }

    public int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(highlight(prompt));
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println(error(Input.INVALID_NUMBER));
            }
        }
    }

    public boolean confirmExit() {
        System.out.print(warning("\nAre you sure you want to exit? (Y/N): "));
        String response = scanner.nextLine();
        return response.equalsIgnoreCase("Y") || response.equalsIgnoreCase("YES");
    }
}