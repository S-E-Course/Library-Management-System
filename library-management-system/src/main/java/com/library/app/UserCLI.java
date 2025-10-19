
package com.library.app;

import com.library.model.Book;
import com.library.service.UserService;

import java.util.List;
import java.util.Scanner;


public class UserCLI {
    private final Scanner in;
    private final UserService user;

    public UserCLI(Scanner in, UserService user) {
        this.in = in;
        this.user = user;
    }

    public void run() {
        while (true) {
            MenuPrinter.clear();
            MenuPrinter.banner("User Menu");
            System.out.println("1) Search Books");
            System.out.println("2) Borrow Book");
            System.out.println("3) Return Book");
            System.out.println("5) Pay Fine");
            System.out.println("0) Logout");

            int choice = InputHelper.readInt(in, "Choose: ", 0, 5);
            try {
                switch (choice) {
                    case 1:
                        searchBooksFlow();
                        break;
                    case 2:
                        borrowBookFlow();
                        break;
                    case 3:
                        returnBookFlow();
                        break;
                    case 5:
                        payFinesFlow();
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

    private void searchBooksFlow() throws Exception {
        MenuPrinter.title("Search Books");
        String keyword = InputHelper.readNonEmpty(in, "Keyword: ");
        List<Book> results = user.searchBooks(keyword);
        if (results == null || results.isEmpty()) {
            System.out.println("(No matching books)");
        } else {
            for (Book b : results) {
                System.out.printf("#%d  %s | %s | %s | %s%n",
                        b.getBookId(), b.getTitle(), b.getAuthor(), b.getIsbn(),
                        b.isAvailable() ? "Available" : "Borrowed");
            }
        }
        InputHelper.pressEnterToContinue(in);
    }

    private void borrowBookFlow() throws Exception {
        MenuPrinter.title("Borrow Book");
        int bookId = InputHelper.readInt(in, "Book ID: ", 1, Integer.MAX_VALUE);
        boolean ok = user.borrowBook(bookId);
        System.out.println(ok ? "Borrowed successfully." : "Could not borrow.");
        InputHelper.pressEnterToContinue(in);
    }

    private void returnBookFlow() throws Exception {
        MenuPrinter.title("Return Book");
        int bookId = InputHelper.readInt(in, "Book ID: ", 1, Integer.MAX_VALUE);
        boolean ok = user.returnBook(bookId);
        System.out.println(ok ? "Returned successfully." : "Could not return.");
        InputHelper.pressEnterToContinue(in);
    }

    private void payFinesFlow() throws Exception {
        MenuPrinter.title("Pay Fine");
        int fineId = InputHelper.readInt(in, "Fine ID: ", 1, Integer.MAX_VALUE);
        double amount = InputHelper.readDouble(in, "Amount: ", 0.01, 1_000_000);
        boolean ok = user.payFine(fineId, amount);
        System.out.println(ok ? "Paid." : "Payment failed.");
        InputHelper.pressEnterToContinue(in);
    }
}
