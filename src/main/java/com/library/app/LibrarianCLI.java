package com.library.app;

import com.library.service.LibrarianService;
import java.util.Scanner;

/**
 * Command-line menu for librarian actions.
 */
public class LibrarianCLI {
    private final Scanner in;
    private final LibrarianService librarian;

    /**
     * Creates a new librarian menu.
     *
     * @param in scanner for input
     * @param librarian service for librarian features
     */
    public LibrarianCLI(Scanner in, LibrarianService librarian) {
        this.in = in;
        this.librarian = librarian;
    }

    /**
     * Runs the menu until the librarian logs out.
     */
    public void run() {
        boolean running = true;
        while (running) {
            MenuPrinter.clear();
            MenuPrinter.banner("Librarian Menu");
            System.out.println("1) Detect Overdue & Issue Fines");
            System.out.println("0) Logout");

            int choice = InputHelper.readInt(in, "Choose: ", 0, 1);
            try {
                if (choice == 1) {
                    detectOverdueFlow();
                } else {
                    // choice == 0 (the only other valid option)
                    running = false;
                }
            } catch (Exception e) {
                System.out.println("Operation failed: " + e.getMessage());
                InputHelper.pressEnterToContinue(in);
            }
        }
    }

    /**
     * Detects overdue media and issues fines.
     *
     * @throws Exception if the service fails
     */
    private void detectOverdueFlow() throws Exception {
        MenuPrinter.title("Detect Overdue & Issue Fines");
        librarian.detectOverdueMedia();
        InputHelper.pressEnterToContinue(in);
    }
}
