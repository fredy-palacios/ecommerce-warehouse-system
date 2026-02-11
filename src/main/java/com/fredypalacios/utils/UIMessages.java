package com.fredypalacios.utils;

public final class UIMessages {
    private UIMessages() {
        throw new AssertionError("Cannot instantiate constants class");
    }

    public static final class Input {
        public static final String PRESS_ENTER = "\n Press Enter to continue...";
        public static final String INVALID_NUMBER = "\n Enter a valid number";
        public static final String INVALID_OPTION = "\n Invalid option";
    }

    public static final class Status {
        public static final String SEARCHING = "Searching";
        public static final String LOADING = "Loading";
        public static final String CREATING = "Creating";
        public static final String UPDATING = "Updating";
    }

    public static final class Prefix {
        public static final String ERROR = "\n Error: ";
        public static final String SUCCESS = "\n ✓ ";
        public static final String WARNING = "\n ⚠ ";
        public static final String OPTION = "\nOption: ";
    }

    public static final class Titles {
        public static final String MAIN_MENU = "MAIN MENU";
        public static final String DASHBOARD = "📊 DASHBOARD";

        public static final String USER_MANAGEMENT = "\n═══ 👤 USER MANAGEMENT ═══\n";
        public static final String LIST_USER = "\n═══ 📋 USER LIST ═══\n";
        public static final String CREATE_USER = "\n═══ ➕ CREATE USER ═══\n";
        public static final String SEARCH_USER = "\n═══ 🔍 SEARCH USER ═══\n";

        public static final String PRODUCT_MANAGEMENT = "\n═══ 📦 PRODUCT MANAGEMENT ═══\n";
        public static final String CREATE_PRODUCT = "\n═══ ➕ CREATE PRODUCT ═══\n";
        public static final String UPDATE_STOCK = "\n═══ 📊 UPDATE STOCK ═══\n";
        public static final String SEARCH_PRODUCT = "\n═══ 🔍 SEARCH PRODUCT ═══\n";
        public static final String LOW_STOCK_PRODUCTS = "\n═══ LOW STOCK PRODUCTS ═══\n";

        public static final String CATEGORY_MANAGEMENT = "\n ═══ 🏷️  CATEGORY MANAGEMENT ═══ \n";
        public static final String CREATE_CATEGORY = "\n═══ ➕ CREATE CATEGORY ═══\n";
    }



}
