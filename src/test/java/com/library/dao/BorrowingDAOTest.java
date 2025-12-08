package com.library.dao;

import com.library.model.Borrowing;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for BorrowingDAO using Mockito mocks.
 */
class BorrowingDAOTest {

    /**
     * Ensures findOverdueMedia returns a list containing one overdue borrowing.
     */
    @Test
    void findOverdueMedia_returnsListWithOneBorrowing() throws Exception {
        Connection conn = mock(Connection.class);
        Statement st = mock(Statement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.createStatement()).thenReturn(st);
        when(st.executeQuery(anyString())).thenReturn(rs);

        when(rs.next()).thenReturn(true, false);
        when(rs.getInt("borrow_id")).thenReturn(1);
        when(rs.getInt("user_id")).thenReturn(10);
        when(rs.getInt("media_id")).thenReturn(5);
        when(rs.getDate("borrow_date")).thenReturn(Date.valueOf("2025-01-01"));
        when(rs.getDate("due_date")).thenReturn(Date.valueOf("2025-01-05"));
        when(rs.getDate("return_date")).thenReturn(null);
        when(rs.getString("status")).thenReturn("overdue");

        BorrowingDAO dao = new BorrowingDAO();
        List<Borrowing> list = dao.findOverdueMedia(conn);

        assertEquals(1, list.size());
        assertEquals(1, list.get(0).getBorrowId());
        assertEquals("overdue", list.get(0).getStatus());
    }

    /**
     * Ensures findActiveBorrowing returns a borrowing when a record exists.
     */
    @Test
    void findActiveBorrowing_returnsBorrowingWhenFound() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true);
        when(rs.getInt("borrow_id")).thenReturn(3);
        when(rs.getString("status")).thenReturn("borrowed");
        when(rs.getDate("due_date")).thenReturn(Date.valueOf("2025-02-10"));

        BorrowingDAO dao = new BorrowingDAO();
        Borrowing b = dao.findActiveBorrowing(conn, 7, 5);

        assertNotNull(b);
        assertEquals(3, b.getBorrowId());
        assertEquals("borrowed", b.getStatus());
    }

    /**
     * Ensures findActiveBorrowing returns null when no record matches.
     */
    @Test
    void findActiveBorrowing_returnsNullWhenNotFound() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        BorrowingDAO dao = new BorrowingDAO();
        Borrowing b = dao.findActiveBorrowing(conn, 7, 5);

        assertNull(b);
    }

    /**
     * Ensures hasOverdueForUser returns true when an overdue record exists.
     */
    @Test
    void hasOverdueForUser_returnsTrueWhenOverdueExists() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);

        BorrowingDAO dao = new BorrowingDAO();
        boolean result = dao.hasOverdueForUser(conn, 1);

        assertTrue(result);
    }

    /**
     * Ensures hasOverdueForUser returns false when there are no overdue records.
     */
    @Test
    void hasOverdueForUser_returnsFalseWhenNoOverdueExists() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        BorrowingDAO dao = new BorrowingDAO();
        boolean result = dao.hasOverdueForUser(conn, 1);

        assertFalse(result);
    }
}
