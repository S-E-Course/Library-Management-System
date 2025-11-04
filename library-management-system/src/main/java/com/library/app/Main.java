package com.library.app;

import com.library.model.User;
import com.library.service.AdminService;
import com.library.service.AuthService;
import com.library.service.LibrarianService;
import com.library.service.UserService;
import com.library.util.DatabaseConnection;

import java.util.Scanner;

/**
 * Application entry point and role router.
 * Opens a database connection, authenticates the user, and runs the role-specific CLI.
 */
public class Main {
    /**
     * Starts the application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        try {
            DatabaseConnection.connect();
            Scanner in = new Scanner(System.in);

            while (true) {
                MenuPrinter.clear();
                MenuPrinter.banner("Library Management System - Login");

                String username = InputHelper.readNonEmpty(in, "Username: ");
                String password = InputHelper.readPassword(in, "Password: ");

                AuthService auth = new AuthService();
                User found = auth.authenticate(username, password);

                if (found == null) {
                    System.out.println("Invalid credentials. Try again.");
                    InputHelper.pressEnterToContinue(in);
                    continue;
                }

                String role = found.getRole();
                if (role == null) {
                    System.out.println("User has no role assigned.");
                    InputHelper.pressEnterToContinue(in);
                    continue;
                }

                switch (role.toLowerCase()) {
                    case "admin":
                        runAdmin(in, username, password);
                        break;
                    case "librarian":
                        runLibrarian(in, username, password);
                        break;
                    case "user":
                        runUser(in, username, password);
                        break;
                    default:
                        System.out.println("Unknown role: " + role);
                        InputHelper.pressEnterToContinue(in);
                        break;
                }
            }
        } catch (Throwable t) {
            System.err.println("Fatal error: " + t.getMessage());
            t.printStackTrace();
        } finally {
            try { DatabaseConnection.disconnect(); } catch (Exception ignore) {}
        }
    }

    /**
     * Runs an admin session.
     *
     * @param in console scanner
     * @param username admin username
     * @param password admin password
     * @throws Exception if a service error occurs
     */
    private static void runAdmin(Scanner in, String username, String password) throws Exception {
        AdminService adminService = new AdminService();
        try {
            if (!adminService.login(username, password)) {
                System.out.println("Login failed for admin.");
                InputHelper.pressEnterToContinue(in);
                return;
            }
            new AdminCLI(in, adminService).run();
        } catch (Exception e) {
            System.out.println("Admin session error: " + e.getMessage());
            InputHelper.pressEnterToContinue(in);
        } finally {
            adminService.logout();
        }
    }

    /**
     * Runs a librarian session.
     *
     * @param in console scanner
     * @param username librarian username
     * @param password librarian password
     * @throws Exception if a service error occurs
     */
    private static void runLibrarian(Scanner in, String username, String password) throws Exception {
        LibrarianService librarianService = new LibrarianService();
        try {
            if (!librarianService.login(username, password)) {
                System.out.println("Login failed for admin.");
                InputHelper.pressEnterToContinue(in);
                return;
            }
            new LibrarianCLI(in, librarianService).run();
        } catch (Exception e) {
            System.out.println("Librarian session error: " + e.getMessage());
            InputHelper.pressEnterToContinue(in);
        } finally {
            librarianService.logout();
        }
    }

    /**
     * Runs a user session.
     *
     * @param in console scanner
     * @param username user username
     * @param password user password
     * @throws Exception if a service error occurs
     */
    private static void runUser(Scanner in, String username, String password) throws Exception {
        UserService userService = new UserService();
        try {
            if (!userService.login(username, password)) {
                System.out.println("Login failed for user.");
                InputHelper.pressEnterToContinue(in);
                return;
            }
            new UserCLI(in, userService).run();
        } catch (Exception e) {
            System.out.println("User session error: " + e.getMessage());
            InputHelper.pressEnterToContinue(in);
        } finally {
            userService.logout();
        }
    }
}
