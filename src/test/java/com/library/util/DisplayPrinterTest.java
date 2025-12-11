package com.library.util;

import com.library.model.Book;
import com.library.model.Borrowing;
import com.library.model.Fine;
import com.library.service.FineSummary;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DisplayPrinter
 */
class DisplayPrinterTest {

    /**
     * Captures System.out output while running the given action.
     *
     * @param action code to run
     * @return captured console output as a string
     */
    private String captureOutput(Runnable action) {
        PrintStream original = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setOut(ps);
        try {
            action.run();
        } finally {
            System.setOut(original);
        }
        return baos.toString();
    }

    /**
     * Verifies that printMediaList prints a placeholder for an empty list.
     */
    @Test
    void printMediaList_printsPlaceholderWhenEmpty() {
        String out = captureOutput(() ->
                DisplayPrinter.printMediaList(Collections.emptyList()));

        assertTrue(out.contains("(No matching media)"));
    }

    /**
     * Verifies that printMediaList prints a placeholder when the list is null.
     */
    @Test
    void printMediaList_printsPlaceholderWhenNull() {
        String out = captureOutput(() ->
                DisplayPrinter.printMediaList(null));

        assertTrue(out.contains("(No matching media)"));
    }

    /**
     * Verifies that printMediaList prints details for a non-empty list.
     */
    @Test
    void printMediaList_printsMediaRows() {
        Book b = new Book();
        b.setId(1);
        b.setTitle("How to Test");
        b.setAuthor("Someone");
        b.setIsbn("123");
        b.setAvailable(true);

        List<com.library.model.Media> list = new ArrayList<>();
        list.add(b);

        String out = captureOutput(() ->
                DisplayPrinter.printMediaList(list));

        assertTrue(out.contains("#1"));
        assertTrue(out.contains("How to Test"));
        assertTrue(out.contains("Available"));
    }

    /**
     * Verifies that printBorrowedMedia prints a placeholder when there are no borrowings.
     */
    @Test
    void printBorrowedMedia_printsPlaceholderWhenEmpty() throws Exception {
        Connection conn = mock(Connection.class);
        List<Borrowing> borrowings = Collections.emptyList();

        String out = captureOutput(() ->
        {
            try {
                DisplayPrinter.printBorrowedMedia(conn, borrowings);
            } catch (Exception e) {
                fail(e);
            }
        });

        assertTrue(out.contains("(You have no borrowed books)"));
    }

    /**
     * Verifies that printBorrowedMedia prints a placeholder when the list is null.
     */
    @Test
    void printBorrowedMedia_printsPlaceholderWhenNull() {
        Connection conn = mock(Connection.class);

        String out = captureOutput(() ->
        {
            try {
                DisplayPrinter.printBorrowedMedia(conn, null);
            } catch (Exception e) {
                fail(e);
            }
        });

        assertTrue(out.contains("(You have no borrowed books)"));
    }

    /**
     * Verifies that printBorrowedMedia prints an overdue status
     * when the due date is in the past.
     */
    @Test
    void printBorrowedMedia_printsOverdueStatus() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true, false);
        when(rs.getString("type")).thenReturn("book");
        when(rs.getInt("media_id")).thenReturn(10);
        when(rs.getString("title")).thenReturn("Overdue Book");
        when(rs.getString("author")).thenReturn("Auth");
        when(rs.getString("isbn")).thenReturn("ISBN");
        when(rs.getBoolean("available")).thenReturn(false);

        Borrowing b = new Borrowing();
        b.setMediaId(10);
        b.setStatus("borrowed");
        b.setDueDate(LocalDate.now().minusDays(2)); // overdue

        List<Borrowing> borrowings = Collections.singletonList(b);

        String out = captureOutput(() ->
        {
            try {
                DisplayPrinter.printBorrowedMedia(conn, borrowings);
            } catch (Exception e) {
                fail(e);
            }
        });

        assertTrue(out.contains("Overdue Book"));
        assertTrue(out.contains("Overdue (Can't return)"));
    }

    /**
     * Verifies that printBorrowedMedia prints "On Time" when the item is not overdue.
     */
    @Test
    void printBorrowedMedia_printsOnTimeStatus() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true, false);
        when(rs.getString("type")).thenReturn("book");
        when(rs.getInt("media_id")).thenReturn(20);
        when(rs.getString("title")).thenReturn("On-time Book");
        when(rs.getString("author")).thenReturn("Auth");
        when(rs.getString("isbn")).thenReturn("ISBN");
        when(rs.getBoolean("available")).thenReturn(false);

        Borrowing b = new Borrowing();
        b.setMediaId(20);
        b.setStatus("borrowed");
        b.setDueDate(LocalDate.now().plusDays(2)); // not overdue

        List<Borrowing> borrowings = Collections.singletonList(b);

        String out = captureOutput(() ->
        {
            try {
                DisplayPrinter.printBorrowedMedia(conn, borrowings);
            } catch (Exception e) {
                fail(e);
            }
        });

        assertTrue(out.contains("On-time Book"));
        assertTrue(out.contains("On Time"));
    }

    /**
     * Verifies that printBorrowedMedia skips items with status "returned".
     */
    @Test
    void printBorrowedMedia_skipsReturnedItems() {
        Connection conn = mock(Connection.class);

        Borrowing b = new Borrowing();
        b.setMediaId(30);
        b.setStatus("returned");
        b.setDueDate(LocalDate.now().minusDays(1));

        List<Borrowing> borrowings = Collections.singletonList(b);

        String out = captureOutput(() ->
        {
            try {
                DisplayPrinter.printBorrowedMedia(conn, borrowings);
            } catch (Exception e) {
                fail(e);
            }
        });

        // No lines should be printed for returned items
        assertTrue(out.isEmpty());
    }

    /**
     * Verifies that printFines prints a placeholder for an empty list.
     */
    @Test
    void printFines_printsPlaceholderWhenEmpty() {
        String out = captureOutput(() ->
                DisplayPrinter.printFines(Collections.emptyList()));

        assertTrue(out.contains("(No fines found)"));
    }

    /**
     * Verifies that printFines prints a placeholder when the list is null.
     */
    @Test
    void printFines_printsPlaceholderWhenNull() {
        String out = captureOutput(() ->
                DisplayPrinter.printFines(null));

        assertTrue(out.contains("(No fines found)"));
    }

    /**
     * Verifies that printFines prints details for fines.
     */
    @Test
    void printFines_printsFineRows() {
        Fine f = new Fine();
        f.setId(5);
        f.setAmount(12.5);
        f.setPaid(false);

        List<Fine> list = new ArrayList<>();
        list.add(f);

        String out = captureOutput(() ->
                DisplayPrinter.printFines(list));

        assertTrue(out.contains("Fine #5"));
        assertTrue(out.contains("12.50"));
        assertTrue(out.contains("No"));
    }

    /**
     * Verifies that printFineSummary prints "No unpaid fines" when empty.
     */
    @Test
    void printFineSummary_printsEmptyMessage() {
        FineSummary summary = new FineSummary();

        String out = captureOutput(() ->
                DisplayPrinter.printFineSummary(summary));

        assertTrue(out.contains("No unpaid fines."));
    }

    /**
     * Verifies that printFineSummary prints per-type rows and a total.
     */
    @Test
    void printFineSummary_printsPerTypeAndTotal() {
        FineSummary summary = new FineSummary();
        summary.add("book", 10.0);
        summary.add("cd", 5.5);

        String out = captureOutput(() ->
                DisplayPrinter.printFineSummary(summary));

        assertTrue(out.contains("book"));
        assertTrue(out.contains("10.00"));
        assertTrue(out.contains("cd"));
        assertTrue(out.contains("5.50"));
        assertTrue(out.contains("TOTAL"));
        assertTrue(out.contains("15.50"));
    }
}
