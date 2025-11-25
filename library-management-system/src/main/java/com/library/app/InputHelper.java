package com.library.app;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Helper methods for reading validated input from the console.
 */
public final class InputHelper {
    private InputHelper() {}

    /**
     * Reads an integer within the given range.  
     * Keeps asking until the input is valid.
     *
     * @param in scanner for input
     * @param prompt text to show
     * @param min lowest allowed value
     * @param max highest allowed value
     * @return the valid integer
     */
    public static int readInt(Scanner in, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                String line = in.nextLine();
                int v = Integer.parseInt(line.trim());
                if (v < min || v > max) {
                    System.out.printf("Please enter a number between %d and %d.%n", min, max);
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            } catch (NoSuchElementException e) {
                System.out.println("Input ended unexpectedly. Exiting.");
                System.exit(1);
            }
        }
    }

    /**
     * Reads a non-empty line of text.
     * Repeats until the user enters a non-blank value.
     *
     * @param in scanner for input
     * @param prompt text to show
     * @return the non-empty string
     */
    public static String readNonEmpty(Scanner in, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = in.nextLine();
            if (s == null) continue;
            s = s.trim();
            if (!s.isEmpty()) return s;
            System.out.println("Value cannot be empty.");
        }
    }

    /**
     * Reads a password string.
     * Works the same as reading a non-empty text.
     *
     * @param in scanner for input
     * @param prompt text to show
     * @return the password string
     */
    public static String readPassword(Scanner in, String prompt) {
        return readNonEmpty(in, prompt);
    }

    /**
     * Waits for the user to press ENTER.
     *
     * @param in scanner for input
     */
    public static void pressEnterToContinue(Scanner in) {
        System.out.print("Press ENTER to continue...");
        try { in.nextLine(); } catch (Exception ignored) {}
    }

    /**
     * Reads a double within the given range.
     * Keeps asking until the input is valid.
     *
     * @param in scanner for input
     * @param prompt text to show
     * @param min lowest allowed value
     * @param max highest allowed value
     * @return the valid double value
     */
	public static double readDouble(Scanner in, String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            try {
                String line = in.nextLine();
                double v = Double.parseDouble(line.trim());
                if (v < min || v > max) {
                    System.out.printf("Please enter a number between %.2f and %.2f.%n", min, max);
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }
}
