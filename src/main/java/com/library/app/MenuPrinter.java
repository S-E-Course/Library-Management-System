package com.library.app;

/**
 * Utility class for printing menu screens and titles in the console.
 */
public final class MenuPrinter {

    private MenuPrinter() {}

    /**
     * Clears the console output.
     * Uses an ANSI escape sequence to move the cursor and wipe the screen.
     */
    public static void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Prints a large banner with a centered title.
     *
     * @param title the banner text to show
     */
    public static void banner(String title) {
        System.out.println("==================================================");
        System.out.println(" " + title);
        System.out.println("==================================================");
    }

    /**
     * Prints a simple section title.
     *
     * @param title the title to show
     */
    public static void title(String title) {
        System.out.println();
        System.out.println("---- " + title + " ----");
    }
}
