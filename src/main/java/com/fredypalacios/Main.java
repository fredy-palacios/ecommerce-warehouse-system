package com.fredypalacios;

import java.util.Scanner;

import static com.fredypalacios.ui.MessagesUI.*;
import static com.fredypalacios.ui.ConsoleColors.*;

import com.fredypalacios.ui.MenuServiceUI;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try (scanner) {
            MenuServiceUI menuServiceUI = new MenuServiceUI(scanner);
            showWelcome();
            runApplication(menuServiceUI);
        } catch (Exception e) {
            System.out.println(error(Prefix.ERROR + e.getMessage()));
            e.printStackTrace();
        }
    }

    private static void runApplication(MenuServiceUI menuServiceUI) throws Exception {
        boolean running = true;

        while (running) {
            clearScreen();
            menuServiceUI.showMainMenu();

            int option = menuServiceUI.getIntInput("\nSelect option: ");

            if (option == 0) {
                running = !menuServiceUI.confirmExit();
            } else {
                menuServiceUI.handleMainMenuOption(option);
            }
        }
        showGoodbye();
    }
}