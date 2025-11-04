package com.library.app;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Utility methods for validated console input.
 * Provides reading of ints, doubles, non-empty strings, and pause prompts.
 */
public final class InputHelper {
    private InputHelper() {}

    /**
     * Reads an integer within bounds.
     *
     * @param in scanner
     * @param prompt text
     * @param min minimum value inclusive
     * @param max maximum value inclusive
     * @return parsed integer
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
     *
     * @param in scanner
     * @param prompt text
     * @return non-empty string
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
     * Reads a password string. Uses the same logic as readNonEmpty.
     *
     * @param in scanner
     * @param prompt text
     * @return password string
     */
    public static String readPassword(Scanner in, String prompt) {
        return readNonEmpty(in, prompt);
    }

    /**
     * Prompts the user to press Enter and consumes a line.
     *
     * @param in scanner
     */
    public static void pressEnterToContinue(Scanner in) {
        System.out.print("Press ENTER to continue...");
        try { in.nextLine(); } catch (Exception ignored) {}
    }

    /**
     * Reads a double within bounds.
     *
     * @param in scanner
     * @param prompt text
     * @param min minimum value inclusive
     * @param max maximum value inclusive
     * @return parsed double
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
