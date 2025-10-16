package com.library.app;

import com.library.model.Book;
import com.library.service.LibrarianService;

import java.util.List;
import java.util.Scanner;

public class LibrarianCLI {
    private final Scanner in;
    private final LibrarianService librarian;

    public LibrarianCLI(Scanner in, LibrarianService librarian) {
        this.in = in;
        this.librarian = librarian;
    }

    public void run() {
        while (true) {
            MenuPrinter.clear();
            MenuPrinter.banner("Librarian Menu");
            System.out.println("1) Add Book");
            System.out.println("2) Remove Book");
            System.out.println("3) List Books");
            System.out.println("4) Set Book Availability");
            System.out.println("0) Logout");
            int choice = InputHelper.readInt(in, "Choose: ", 0, 4);

            try {
                switch (choice) {
                    case 1: addBookFlow();
                    case 2: removeBookFlow();
                    case 3: listBooksFlow();
                    case 4: setBookAvailabilityFlow();
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
        boolean ok = librarian.addBook(title, author, isbn);
        System.out.println(ok ? "Book added." : "Failed to add book.");
        InputHelper.pressEnterToContinue(in);
    }

    private void removeBookFlow() throws Exception {
        MenuPrinter.title("Remove Book");
        int bookId = InputHelper.readInt(in, "Book ID: ", 1, Integer.MAX_VALUE);
        boolean ok = librarian.removeBook(bookId);
        System.out.println(ok ? "Book removed." : "Failed to remove book.");
        InputHelper.pressEnterToContinue(in);
    }

    private void listBooksFlow() throws Exception {
        MenuPrinter.title("Books");
        List<Book> books = librarian.listBooks();
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

    private void setBookAvailabilityFlow() throws Exception {
        MenuPrinter.title("Set Book Availability");
        int bookId = InputHelper.readInt(in, "Book ID: ", 1, Integer.MAX_VALUE);
        int a = InputHelper.readInt(in, "Availability (1=available, 0=borrowed): ", 0, 1);
        boolean ok = librarian.setBookAvailability(bookId, a == 1);
        System.out.println(ok ? "Updated." : "Failed.");
        InputHelper.pressEnterToContinue(in);
    }
}