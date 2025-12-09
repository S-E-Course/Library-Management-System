package com.library.app;

import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for InputHelper utility methods.
 */
public class InputHelperTest {

    /**
     * Verifies readInt returns a valid value on the first try.
     */
    @Test
    public void testReadIntValidFirstTry() {
        String data = "5\n";
        Scanner scanner = new Scanner(data);
        int result = InputHelper.readInt(scanner, "Enter: ", 1, 10);
        assertEquals(5, result);
    }

    /**
     * Verifies readInt rejects an out-of-range value and then accepts a valid one.
     */
    @Test
    public void testReadIntOutOfRangeThenValid() {
        String data = "0\n7\n";
        Scanner scanner = new Scanner(data);
        int result = InputHelper.readInt(scanner, "Enter: ", 1, 10);
        assertEquals(7, result);
    }

    /**
     * Verifies readInt rejects a non-numeric value and then accepts a valid one.
     */
    @Test
    public void testReadIntNonNumericThenValid() {
        String data = "abc\n3\n";
        Scanner scanner = new Scanner(data);
        int result = InputHelper.readInt(scanner, "Enter: ", 1, 10);
        assertEquals(3, result);
    }

    /**
     * Verifies readNonEmpty skips empty and whitespace-only lines.
     */
    @Test
    public void testReadNonEmptySkipsEmpty() {
        String data = "\n   \nhello\n";
        Scanner scanner = new Scanner(data);
        String result = InputHelper.readNonEmpty(scanner, "Enter: ");
        assertEquals("hello", result);
    }

    /**
     * Verifies readPassword behaves like readNonEmpty.
     */
    @Test
    public void testReadPassword() {
        String data = "\nsecret\n";
        Scanner scanner = new Scanner(data);
        String result = InputHelper.readPassword(scanner, "Password: ");
        assertEquals("secret", result);
    }

    /**
     * Verifies pressEnterToContinue consumes exactly one line of input.
     */
    @Test
    public void testPressEnterToContinueConsumesLine() {
        String data = "\nnext\n";
        Scanner scanner = new Scanner(data);
        InputHelper.pressEnterToContinue(scanner);
        String result = InputHelper.readNonEmpty(scanner, "Enter: ");
        assertEquals("next", result);
    }

    /**
     * Verifies readDouble returns a valid value on the first try.
     */
    @Test
    public void testReadDoubleValidFirstTry() {
        String data = "3.5\n";
        Scanner scanner = new Scanner(data);
        double result = InputHelper.readDouble(scanner, "Enter: ", 0.0, 10.0);
        assertEquals(3.5, result);
    }

    /**
     * Verifies readDouble rejects an out-of-range value and then accepts a valid one.
     */
    @Test
    public void testReadDoubleOutOfRangeThenValid() {
        String data = "20\n4.5\n";
        Scanner scanner = new Scanner(data);
        double result = InputHelper.readDouble(scanner, "Enter: ", 0.0, 10.0);
        assertEquals(4.5, result);
    }

    /**
     * Verifies readDouble rejects a non-numeric value and then accepts a valid one.
     */
    @Test
    public void testReadDoubleNonNumericThenValid() {
        String data = "abc\n2.25\n";
        Scanner scanner = new Scanner(data);
        double result = InputHelper.readDouble(scanner, "Enter: ", 0.0, 10.0);
        assertEquals(2.25, result);
    }
}
