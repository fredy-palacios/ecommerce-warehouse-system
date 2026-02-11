package com.fredypalacios.enums;

public enum UserRole {
    MANAGER("👔", "Manager"),
    PICKER("📦", "Picker"),
    RECEIVER("📥", "Receiver"),
    CONTROLLER("📊", "Controller");

    private final String icon;
    private final String displayName;

    UserRole(String icon, String displayName) {
        this.icon = icon;
        this.displayName = displayName;
    }

    public String getIcon() {
        return icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String formatted() {
        return icon + " " + displayName;
    }
}