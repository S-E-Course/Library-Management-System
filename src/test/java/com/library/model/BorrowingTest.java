package com.library.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Borrowing model class
 */
class BorrowingTest {

    /**
     * Tests all getters and setters to ensure values are stored correctly.
     */
    @Test
    void testGettersAndSetters() {
        Borrowing b = new Borrowing();

        b.setBorrowId(10);
        b.setUserId(20);
        b.setMediaId(30);

        LocalDate bd = LocalDate.of(2024, 1, 1);
        LocalDate dd = LocalDate.of(2024, 1, 10);
        LocalDate rd = LocalDate.of(2024, 1, 5);

        b.setBorrowDate(bd);
        b.setDueDate(dd);
        b.setReturnDate(rd);
        b.setStatus("borrowed");

        assertEquals(10, b.getBorrowId());
        assertEquals(20, b.getUserId());
        assertEquals(30, b.getMediaId());
        assertEquals(bd, b.getBorrowDate());
        assertEquals(dd, b.getDueDate());
        assertEquals(rd, b.getReturnDate());
        assertEquals("borrowed", b.getStatus());
    }

    /**
     * Tests isOverdue(): returnDate null, dueDate before today =>true.
     */
    @Test
    void testIsOverdueReturningTrue() {
        Borrowing b = new Borrowing();
        b.setReturnDate(null);
        b.setDueDate(LocalDate.now().minusDays(1)); 

        assertTrue(b.isOverdue());
    }

    /**
     * Tests isOverdue(): dueDate is today or after today => false.
     */
    @Test
    void testIsOverdueDueDateNotBeforeToday() {
        Borrowing b = new Borrowing();
        b.setReturnDate(null);
        b.setDueDate(LocalDate.now()); 

        assertFalse(b.isOverdue());
    }

    /**
     * Tests isOverdue(): returnDate not null => false.
     */
    @Test
    void testIsOverdueWithReturnDate() {
        Borrowing b = new Borrowing();
        b.setReturnDate(LocalDate.now());  
        b.setDueDate(LocalDate.now().minusDays(5));

        assertFalse(b.isOverdue());
    }

    /**
     * Tests isOverdue(): null dueDate => false.
     */
    @Test
    void testIsOverdueWithNullDueDate() {
        Borrowing b = new Borrowing();
        b.setReturnDate(null);
        b.setDueDate(null);

        assertFalse(b.isOverdue());
    }
}
