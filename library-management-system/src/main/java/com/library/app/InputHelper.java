package com.library.app;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public final class InputHelper {
    private InputHelper() {}

    public static int readInt(Scanner in, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int v = Integer.parseInt(in.nextLine().trim());
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

    public static double readDouble(Scanner in, String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            try {
                double v = Double.parseDouble(in.nextLine().trim());
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

    public static String readPassword(Scanner in, String prompt) {
        // Simpler for CLI; if needed, replace with Console.readPassword()
        return readNonEmpty(in, prompt);
    }

    public static LocalDate readDate(Scanner in, String prompt) {
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
        while (true) {
            String s = readNonEmpty(in, prompt + " (yyyy-MM-dd): ");
            try {
                return LocalDate.parse(s, fmt);
            } catch (DateTimeParseException e) {
                System.out.println("Bad date format. Try again.");
            }
        }
    }

    public static void pressEnterToContinue(Scanner in) {
        System.out.print("Press ENTER to continue...");
        try { in.nextLine(); } catch (Exception ignored) {}
    }
}