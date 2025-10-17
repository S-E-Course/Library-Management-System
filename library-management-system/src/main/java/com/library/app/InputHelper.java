package com.library.app;

import java.util.NoSuchElementException;
import java.util.Scanner;

public final class InputHelper {
    private InputHelper() {}

    public static int readInt(Scanner in, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                String line = in.nextLine();           // <-- read the whole line
                int v = Integer.parseInt(line.trim()); // <-- parse it
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
        return readNonEmpty(in, prompt);
    }

    public static void pressEnterToContinue(Scanner in) {
        System.out.print("Press ENTER to continue...");
        try { in.nextLine(); } catch (Exception ignored) {}
    }

	public static double readDouble(Scanner in, String prompt, double min, double max) {
    while (true) {
        System.out.print(prompt);
        try {
            String line = in.nextLine();       // Read full line
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
