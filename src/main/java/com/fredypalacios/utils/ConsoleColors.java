package com.fredypalacios.utils;

import com.diogonunes.jcolor.Ansi;
import com.diogonunes.jcolor.Attribute;

public class ConsoleColors {

    // Text color
    public static String success(String text) {
        return Ansi.colorize(text, Attribute.GREEN_TEXT());
    }

    public static String error(String text) {
        return Ansi.colorize(text, Attribute.RED_TEXT());
    }

    public static String warning(String text) {
        return Ansi.colorize(text, Attribute.YELLOW_TEXT());
    }

    public static String info(String text) {
        return Ansi.colorize(text, Attribute.CYAN_TEXT());
    }

    public static String highlight(String text) {
        return Ansi.colorize(text, Attribute.BRIGHT_MAGENTA_TEXT());
    }

    public static String title(String text) {
        return Ansi.colorize(text, Attribute.BRIGHT_BLUE_TEXT(), Attribute.BOLD());
    }

    // Backgrounds
    public static String successBg(String text) {
        return Ansi.colorize(" " + text + " ", Attribute.BLACK_TEXT(), Attribute.GREEN_BACK());
    }

    public static String errorBg(String text) {
        return Ansi.colorize(" " + text + " ", Attribute.WHITE_TEXT(), Attribute.RED_BACK());
    }

    public static String warningBg(String text) {
        return Ansi.colorize(" " + text + " ", Attribute.BLACK_TEXT(), Attribute.YELLOW_BACK());
    }

    public static String infoBg(String text) {
        return Ansi.colorize(" " + text + " ", Attribute.WHITE_TEXT(), Attribute.CYAN_BACK());
    }

    // Special effects
    public static String bold(String text) {
        return Ansi.colorize(text, Attribute.BOLD());
    }

    public static String underline(String text) {
        return Ansi.colorize(text, Attribute.UNDERLINE());
    }

    // Clean (Only Mac/Linux)
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    // Loading animation
    public static void loadingAnimation(String message, int duration) throws InterruptedException {
        String[] frames = {"⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"};
        long endTime = System.currentTimeMillis() + duration;
        int i = 0;

        while (System.currentTimeMillis() < endTime) {
            System.out.print("\r" + info(frames[i % frames.length] + " " + message));
            Thread.sleep(80);
            i++;
        }
        System.out.print("\r" + success("✓ " + message) + "          \n");
    }

    // Progress bar
    public static void progressBar(int current, int total, String label) {
        int barLength = 40;
        int progress = (int) ((double) current / total * barLength);

        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < barLength; i++) {
            if (i < progress) {
                bar.append("█");
            } else {
                bar.append("░");
            }
        }
        bar.append("]");

        int percentage = (int) ((double) current / total * 100);
        System.out.print("\r" + info(label + ": ") + success(bar.toString()) +
                " " + highlight(percentage + "%"));

        if (current == total) {
            System.out.println();
        }
    }

    // Banner
    public static void printBanner() {
        String[] banner = {
            "███████       ██     ██  █████  ██████  ███████ ██   ██  ██████  ██    ██ ███████ ███████",
            "██            ██     ██ ██   ██ ██   ██ ██      ██   ██ ██    ██ ██    ██ ██      ██",
            "█████   █████ ██  █  ██ ███████ ██████  █████   ███████ ██    ██ ██    ██ ███████ █████",
            "██            ██ ███ ██ ██   ██ ██   ██ ██      ██   ██ ██    ██ ██    ██      ██ ██",
            "███████        ███ ███  ██   ██ ██   ██ ███████ ██   ██  ██████   ██████  ███████ ███████"
        };

        Attribute[] colors = {
                Attribute.BRIGHT_BLUE_TEXT(),
                Attribute.BRIGHT_CYAN_TEXT(),
                Attribute.CYAN_TEXT(),
                Attribute.BRIGHT_BLUE_TEXT(),
                Attribute.BLUE_TEXT(),
                Attribute.BRIGHT_BLUE_TEXT()
        };

        System.out.println();
        for (int i = 0; i < banner.length; i++) {
            System.out.println(Ansi.colorize(banner[i], colors[i]));
        }
        System.out.println();
    }

    // Decorative dividers
    public static void printSeparator() {
        System.out.println(info("═".repeat(70)));
    }

    public static void printDoubleSeparator() {
        System.out.println(highlight("╔" + "═".repeat(68) + "╗"));
    }

    public static void printLine() {
        System.out.println(info("─".repeat(70)));
    }
}