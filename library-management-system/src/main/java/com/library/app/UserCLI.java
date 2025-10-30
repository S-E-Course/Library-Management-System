package com.library.app;

import com.library.model.Borrowing;
import com.library.model.Fine;
import com.library.model.Media;
import com.library.service.UserService;
import com.library.util.DisplayPrinter;

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
            System.out.println("1) Search Media");
            System.out.println("2) Borrow Media");
            System.out.println("3) Return Media");
            System.out.println("5) Pay Fine");
            System.out.println("0) Logout");

            int choice = InputHelper.readInt(in, "Choose: ", 0, 5);
            try {
                switch (choice) {
                    case 1:
                        searchMediaFlow();
                        break;
                    case 2:
                        borrowMediaFlow();
                        break;
                    case 3:
                        returnMediaFlow();
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

    private void searchMediaFlow() throws Exception {
        MenuPrinter.title("Search Media");
        String type = InputHelper.readNonEmpty(in, "Media Type (book/cd/journal/media): ");
        String keyword = InputHelper.readNonEmpty(in, "Keyword: ");
        List<Media> results = user.searchMedia(keyword, type);
        DisplayPrinter.printMediaList(results);
        InputHelper.pressEnterToContinue(in);
    }

    private void borrowMediaFlow() throws Exception {
        MenuPrinter.title("Borrow Media");
        int mediaId = InputHelper.readInt(in, "Media ID: ", 1, Integer.MAX_VALUE);
        boolean ok = user.borrowMedia(mediaId);
        System.out.println(ok ? "Borrowed successfully." : "Could not borrow.");
        InputHelper.pressEnterToContinue(in);
    }

    private void returnMediaFlow() throws Exception {
        MenuPrinter.title("Return Media ----\n---- Borrowed Media");
        List<Borrowing> borrowings = user.findBorrowings(user.getLoggedUser().getUserId());
        DisplayPrinter.printBorrowedMedia(user.getUserConnection(), borrowings);
        int mediaId = InputHelper.readInt(in, "Media ID: ", 1, Integer.MAX_VALUE);
        boolean ok = user.returnMedia(mediaId);
        System.out.println(ok ? "Returned successfully." : "Could not return.");
        InputHelper.pressEnterToContinue(in);
    }

    private void payFinesFlow() throws Exception {
        MenuPrinter.title("Pay Fine");
        List<Fine> fines = user.findFines(user.getLoggedUser().getUserId());
        DisplayPrinter.printFines(fines);
        int fineId = InputHelper.readInt(in, "Fine ID: ", 1, Integer.MAX_VALUE);
        double amount = InputHelper.readDouble(in, "Amount: ", 0.01, 1_000_000);
        boolean ok = user.payFine(fineId, amount);
        System.out.println(ok ? "Paid." : "Payment failed.");
        InputHelper.pressEnterToContinue(in);
    }
}
