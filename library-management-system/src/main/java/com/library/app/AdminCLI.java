package com.library.app;

import com.library.model.Book;
import com.library.model.User;
import com.library.service.AdminService;

import java.util.List;
import java.util.Scanner;

public class AdminCLI {
    private final Scanner in;
    private final AdminService admin;

    public AdminCLI(Scanner in, AdminService admin) {
        this.in = in;
        this.admin = admin;
    }

    public void run() {
        while (true) {
            MenuPrinter.clear();
            MenuPrinter.banner("Admin Menu");
            System.out.println("1) Add Book");
            System.out.println("2) Remove Book");
            System.out.println("3) List Books");
            System.out.println("4) Add User");
            System.out.println("5) Remove User");
            System.out.println("6) List Users");
            System.out.println("0) Logout");
            int choice = InputHelper.readInt(in, "Choose: ", 0, 6);

            try {
                switch (choice) {
                    case 1: addBookFlow();
                    case 2: removeBookFlow();
                    case 3: listBooksFlow();
                    case 4: addUserFlow();
                    case 5: removeUserFlow();
                    case 6: listUsersFlow();
                    case 0: { return; }
                }
            } catch (Exception e) {
                System.out.println("Operation failed: " + e.getMessage());
                InputHelper.pressEnterToContinue(in);
            }
        }
    }

    private void addBookFlow() throws Exception {
        MenuPrinter.title("Add Book");
        String title = InputHelper.readNonEmpty(in, "Title: ");
        String author = InputHelper.readNonEmpty(in, "Author: ");
        String isbn = InputHelper.readNonEmpty(in, "ISBN: ");
        boolean ok = admin.addBook(title, author, isbn);
        System.out.println(ok ? "Book added." : "Failed to add book.");
        InputHelper.pressEnterToContinue(in);
    }

    private void removeBookFlow() throws Exception {
        MenuPrinter.title("Remove Book");
        int bookId = InputHelper.readInt(in, "Book ID: ", 1, Integer.MAX_VALUE);
        boolean ok = admin.removeBook(bookId);
        System.out.println(ok ? "Book removed." : "Failed to remove book.");
        InputHelper.pressEnterToContinue(in);
    }

    private void listBooksFlow() throws Exception {
        MenuPrinter.title("Books");
        List<Book> books = admin.listBooks();
        if (books == null || books.isEmpty()) {
            System.out.println("(No books)");
        } else {
            for (Book b : books) {
                System.out.printf("#%d  %s | %s | %s | %s%n",
                        b.getBookId(), b.getTitle(), b.getAuthor(), b.getIsbn(),
                        b.isAvailable() ? "Available" : "Borrowed");
            }
        }
        InputHelper.pressEnterToContinue(in);
    }

    private void addUserFlow() throws Exception {
        MenuPrinter.title("Add User");
        String username = InputHelper.readNonEmpty(in, "Username: ");
        String email = InputHelper.readNonEmpty(in, "Email: ");
        String password = InputHelper.readPassword(in, "Password (plain/hash): ");
        String role = InputHelper.readNonEmpty(in, "Role (ADMIN|LIBRARIAN|USER): ").toUpperCase();
        boolean ok = admin.addUser(username, email, password, role);
        System.out.println(ok ? "User added." : "Failed to add user.");
        InputHelper.pressEnterToContinue(in);
    }

    private void removeUserFlow() throws Exception {
        MenuPrinter.title("Remove User");
        int userId = InputHelper.readInt(in, "User ID: ", 1, Integer.MAX_VALUE);
        boolean ok = admin.removeUser(userId);
        System.out.println(ok ? "User removed." : "Failed to remove user.");
        InputHelper.pressEnterToContinue(in);
    }

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
}