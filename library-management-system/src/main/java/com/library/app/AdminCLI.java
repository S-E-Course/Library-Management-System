package com.library.app;

import com.library.model.*;
import com.library.model.User;
import com.library.service.AdminService;
import com.library.util.DisplayPrinter;
import com.library.util.ValidationHelper;

import java.util.List;
import java.util.Scanner;

/**
 * Provides the admin command-line menu and actions.
 */
public class AdminCLI {
    private final Scanner in;
    private final AdminService admin;

    /**
     * Creates an admin menu interface.
     *
     * @param in scanner used for input
     * @param admin service used for admin actions
     */
    public AdminCLI(Scanner in, AdminService admin) {
        this.in = in;
        this.admin = admin;
    }

    /**
     * Shows the admin menu and handles user choices.
     */
    public void run() {
        while (true) {
            MenuPrinter.clear();
            MenuPrinter.banner("Admin Menu");
            System.out.println("1) Add Media");
            System.out.println("2) List Media");
            System.out.println("3) Remove Media");
            System.out.println("4) Add User");
            System.out.println("5) List Users");
            System.out.println("6) Remove User");
            System.out.println("7) Send Overdue Reminders");
            System.out.println("0) Logout");

            int choice = InputHelper.readInt(in, "Choose: ", 0, 7);

            try {
                switch (choice) {
                    case 1:
                        addMediaFlow();
                        break;
                    case 2:
                        listMediaFlow();
                        break;
                    case 3:
                        removeMediaFlow();
                        break;
                    case 4:
                        addUserFlow();
                        break;
                    case 5:
                        listUsersFlow();
                        break;
                    case 6:
                        removeUserFlow();
                        break;
                    case 7:
                        sendRemindersFlow();
                        break;
                    case 0:
                        return;
                    default:
                        break;
                }
            } catch (Exception e) {
                System.out.println("Operation failed: " + e.getMessage());
                InputHelper.pressEnterToContinue(in);
            }
        }
    }

    /**
     * Reads media details and adds a new media item.
     *
     * @throws Exception if adding fails
     */
    private void addMediaFlow() throws Exception {
        MenuPrinter.title("Add Media");
        String title  = InputHelper.readNonEmpty(in, "Title: ");
        String author = InputHelper.readNonEmpty(in, "Author: ");
        String isbn   = InputHelper.readNonEmpty(in, "ISBN: ");
        String type   = InputHelper.readNonEmpty(in, "Type (book|cd|journal): ").toLowerCase();

        Media m;
        switch (type) {
        case "cd":
            m = new CD();
            break;
        case "journal":
            m = new Journal();
            break;
        default:
            m = new Book();
            break;
        }
        m.setTitle(title);
        m.setAuthor(author);
        m.setIsbn(isbn);
        boolean ok = admin.addMedia(m);
        System.out.println(ok ? "Media added." : "Failed to add media.");
        InputHelper.pressEnterToContinue(in);
    }

    /**
     * Shows all media filtered by type.
     *
     * @throws Exception if loading media fails
     */
    private void listMediaFlow() throws Exception {
        MenuPrinter.title("Media");
        String type = InputHelper.readNonEmpty(in, "Filter Type (book|cd|journal|media): ").toLowerCase();
        List<Media> mediaList = admin.listAllMedia(type);
        DisplayPrinter.printMediaList(mediaList);
        InputHelper.pressEnterToContinue(in);
    }

    /**
     * Reads an ID and removes the selected media item.
     *
     * @throws Exception if removing fails
     */
    private void removeMediaFlow() throws Exception {
        MenuPrinter.title("Remove Media");
        int mediaId = InputHelper.readInt(in, "Media ID: ", 1, Integer.MAX_VALUE);
        boolean ok = admin.removeMedia(mediaId);
        System.out.println(ok ? "Media removed." : "Failed to remove media.");
        InputHelper.pressEnterToContinue(in);
    }

    /**
     * Reads user info and adds a new user.
     *
     * @throws Exception if adding fails
     */
    private void addUserFlow() throws Exception {
        MenuPrinter.title("Add User");
        String username = InputHelper.readNonEmpty(in, "Username: ");
        String email    = InputHelper.readNonEmpty(in, "Email: ");
        String password = InputHelper.readPassword(in, "Password: ");
        String role     = InputHelper.readNonEmpty(in, "Role (admin|librarian|user): ").toLowerCase();
        if (!ValidationHelper.isValidEmail(email)) {
            System.out.println("Invalid email format.");
            InputHelper.pressEnterToContinue(in);
            return;
        }
        if (!ValidationHelper.isValidRole(role)) {
            System.out.println("Invalid role. Must be admin, librarian, or user.");
            InputHelper.pressEnterToContinue(in);
            return;
        }
        boolean ok = admin.addUser(username, email, password, role);
        System.out.println(ok ? "User added." : "Failed to add user.");
        InputHelper.pressEnterToContinue(in);
    }

    /**
     * Lists all users with basic details.
     *
     * @throws Exception if loading users fails
     */
    private void listUsersFlow() throws Exception {
        MenuPrinter.title("Users");
        List<User> users = admin.listUsers();
        if (users == null || users.isEmpty()) {
            System.out.println("(No users)");
        } else {
            for (User u : users) {
                System.out.printf("#%d  %s | %s | role=%s | balance=%.2f%n",
                        u.getUserId(), u.getUsername(), u.getEmail(), u.getRole(), u.getBalance());
            }
        }
        InputHelper.pressEnterToContinue(in);
    }

    /**
     * Reads a user ID and removes that user.
     *
     * @throws Exception if removing fails
     */
    private void removeUserFlow() throws Exception {
        MenuPrinter.title("Remove User");
        int userId = InputHelper.readInt(in, "User ID: ", 1, Integer.MAX_VALUE);
        boolean ok = admin.removeUser(userId);
        System.out.println(ok ? "User removed." : "Failed to remove user.");
        InputHelper.pressEnterToContinue(in);
    }

    /**
     * Sends overdue reminder emails.
     *
     * @throws Exception if sending fails
     */
    private void sendRemindersFlow() throws Exception {
        MenuPrinter.title("Sending Overdue Reminders");
        int notified = admin.sendOverdueRemindersFromEnv();
        System.out.println("Reminders sent to " + notified + " user(s).");
        InputHelper.pressEnterToContinue(in);
    }

}
