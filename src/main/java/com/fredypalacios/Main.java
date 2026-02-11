package com.fredypalacios;

import com.fredypalacios.service.MenuService;
import static com.fredypalacios.utils.UIMessages.*;
import static com.fredypalacios.utils.ConsoleColors.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MenuService menuService = new MenuService(scanner);

        try {
            showWelcome();
            runApplication(menuService);
        } catch (Exception e) {
            System.out.println(error(Prefix.ERROR + e.getMessage()));
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private static void runApplication(MenuService menuService) throws Exception {
        boolean running = true;

        while (running) {
            clearScreen();
            menuService.showMainMenu();

            int option = menuService.getIntInput("\nSelect option: ");

            if (option == 0) {
                running = !menuService.confirmExit();
            } else {
                menuService.handleMainMenuOption(option);
            }
        }
        showGoodbye();
    }
}