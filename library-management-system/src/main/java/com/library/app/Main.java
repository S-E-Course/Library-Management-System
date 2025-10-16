package com.library.app;

import com.library.service.AdminService;
import com.library.service.LibrarianService;
import com.library.service.UserService;
import com.library.util.DatabaseConnection;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            // Initialize DB connection once for the app lifetime (optional)
            try {
                DatabaseConnection.connect();
            } catch (Exception e) {
                System.out.println("Warning: Could not connect to DB at startup. Will try on demand.");
            }

            Scanner in = new Scanner(System.in);
            while (true) {
                MenuPrinter.clear();
                MenuPrinter.banner("Library Management System - CLI");
                System.out.println("1) Login as Admin");
                System.out.println("2) Login as Librarian");
                System.out.println("3) Login as User");
                System.out.println("0) Exit");
                int choice = InputHelper.readInt(in, "Choose: ", 0, 3);

                switch (choice) {
                    case 1: loginAdmin(in);
                    case 2: loginLibrarian(in);
                    case 3: loginUser(in);
                    case 0: {
                        System.out.println("Goodbye!");
                        try { DatabaseConnection.disconnect(); } catch (Exception ignored) {}
                        return;
                    }
                }
            }
        } catch (Throwable t) {
            System.err.println("Fatal error: " + t.getMessage());
            t.printStackTrace();
        }
    }

    private static void loginAdmin(Scanner in) {
        AdminService adminService = new AdminService();
        String username = InputHelper.readNonEmpty(in, "Admin username: ");
        String password = InputHelper.readPassword(in, "Admin password: ");
        try {
            if (adminService.login(username, password)) {
                new AdminCLI(in, adminService).run();
                adminService.logout();
            } else {
                System.out.println("Invalid admin credentials.");
                InputHelper.pressEnterToContinue(in);
            }
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            InputHelper.pressEnterToContinue(in);
        }
    }

    private static void loginLibrarian(Scanner in) {
        LibrarianService librarianService = new LibrarianService();
        String username = InputHelper.readNonEmpty(in, "Librarian username: ");
        String password = InputHelper.readPassword(in, "Librarian password: ");
        try {
            if (librarianService.login(username, password)) {
                new LibrarianCLI(in, librarianService).run();
                librarianService.logout();
            } else {
                System.out.println("Invalid librarian credentials.");
                InputHelper.pressEnterToContinue(in);
            }
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            InputHelper.pressEnterToContinue(in);
        }
    }

    private static void loginUser(Scanner in) {
        UserService userService = new UserService();
        String username = InputHelper.readNonEmpty(in, "User username: ");
        String password = InputHelper.readPassword(in, "User password: ");
        try {
            if (userService.login(username, password)) {
                new UserCLI(in, userService).run();
                userService.logout();
            } else {
                System.out.println("Invalid user credentials.");
                InputHelper.pressEnterToContinue(in);
            }
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            InputHelper.pressEnterToContinue(in);
        }
    }
}