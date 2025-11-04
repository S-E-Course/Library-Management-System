package com.library.app;

import com.library.service.LibrarianService;
import java.util.Scanner;

/**
 * Command-line interface for librarian workflows.
 * Provides actions for overdue detection and fine issuing.
 */
public class LibrarianCLI {
    private final Scanner in;
    private final LibrarianService librarian;

    /**
     * Creates a new LibrarianCLI.
     *
     * @param in console scanner
     * @param librarian librarian service instance
     */
    public LibrarianCLI(Scanner in, LibrarianService librarian) {
        this.in = in;
        this.librarian = librarian;
    }

    /**
     * Runs the librarian interaction loop until logout.
     */
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

    /**
     * Runs overdue detection and fine issuing.
     *
     * @throws Exception if a service error occurs
     */
    private void detectOverdueFlow() throws Exception {
        MenuPrinter.title("Detect Overdue & Issue Fines");
        librarian.detectOverdueMedia();
        InputHelper.pressEnterToContinue(in);
    }
}
