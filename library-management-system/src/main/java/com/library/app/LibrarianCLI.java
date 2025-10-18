
package com.library.app;

import com.library.service.LibrarianService;
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
            System.out.println("1) Detect Overdue & Issue Fines");
            System.out.println("0) Logout");

            int choice = InputHelper.readInt(in, "Choose: ", 0, 1);
            try {
                switch (choice) {
                    case 1:
                        detectOverdueFlow();
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

    private void detectOverdueFlow() throws Exception {
        MenuPrinter.title("Detect Overdue & Issue Fines");
        librarian.detectOverdueBooks();
        InputHelper.pressEnterToContinue(in);
    }
}
