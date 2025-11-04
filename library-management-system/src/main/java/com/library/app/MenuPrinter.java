package com.library.app;

/**
 * Console helpers for clearing the screen and printing titles.
 */
public final class MenuPrinter {
    private MenuPrinter() {}
    public static void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Prints a full banner with a title.
     *
     * @param title banner title
     */
    public static void banner(String title) {
        System.out.println("==================================================");
        System.out.println(" " + title);
        System.out.println("==================================================");
    }

    /**
     * Prints a section title line.
     *
     * @param title section title
     */
    public static void title(String title) {
        System.out.println();
        System.out.println("---- " + title + " ----");
    }
}
