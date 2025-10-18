
package com.library.app;

public final class MenuPrinter {
    private MenuPrinter() {}

    public static void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void banner(String title) {
        System.out.println("==================================================");
        System.out.println(" " + title);
        System.out.println("==================================================");
    }

    public static void title(String title) {
        System.out.println();
        System.out.println("---- " + title + " ----");
    }
}
