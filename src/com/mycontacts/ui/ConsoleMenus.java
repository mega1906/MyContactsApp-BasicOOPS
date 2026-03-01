package com.mycontacts.ui;

// Console menu text in one place.
public final class ConsoleMenus {
    private ConsoleMenus() {}

    public static void showGuestMenu() {
        System.out.println("\nMy Contacts App");
        System.out.println("1. Register");
        System.out.println("2. Login (Basic Auth)");
        System.out.println("3. Login (OAuth)");
        System.out.println("0. Exit");
        System.out.print("Choose option: ");
    }

    public static void showLoggedInMenu() {
        System.out.println("\n1. Update Profile");
        System.out.println("2. Change Password");
        System.out.println("3. Manage Preferences");
        System.out.println("4. Create Contact");
        System.out.println("5. Logout");
        System.out.print("Choose option: ");
    }
}
