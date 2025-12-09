package com.library.dao;

import com.library.model.Fine;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for FineDAO using Mockito-mocks.
 */
class FineDAOTest {

    @Test
    void issueFine_returnsTrueWhenInsertSucceeds() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        FineDAO dao = new FineDAO();
        boolean ok = dao.issueFine(conn, 11, 101, 20.0);

        assertTrue(ok);
    }

    @Test
    void getFineAmount_returnsAmountWhenRowExists() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getDouble("amount")).thenReturn(60.5);

        FineDAO dao = new FineDAO();
        double amount = dao.getFineAmount(conn, 15, 101);

        assertEquals(60.5, amount);
    }

    @Test
    void getFineAmount_throwsWhenFineNotFound() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        FineDAO dao = new FineDAO();

        boolean exceptionThrown = false;

        try {
            dao.getFineAmount(conn, 99, 101);
        } catch (SQLException e) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);

    }

    @Test
    void isPaid_returnsTrueFalseOrNull() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        // First call: one row -> paid = true
        // Second call: one row -> paid = false
        // Third call: no row -> null
        when(rs.next()).thenReturn(true, true, false);
        when(rs.getBoolean("paid")).thenReturn(true, false);

        FineDAO dao = new FineDAO();

        Boolean p1 = dao.isPaid(conn, 1);
        Boolean p2 = dao.isPaid(conn, 2);
        Boolean p3 = dao.isPaid(conn, 3);

        assertEquals(Boolean.TRUE, p1);
        assertEquals(Boolean.FALSE, p2);
        assertNull(p3);
    }

    @Test
    void findFines_returnsListOfUserFines() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        // Two rows then end
        when(rs.next()).thenReturn(true, true, false);
        when(rs.getInt("fine_id")).thenReturn(12, 13);
        when(rs.getInt("borrow_id")).thenReturn(9001, 9002);
        when(rs.getDouble("amount")).thenReturn(20.0, 40.0);
        when(rs.getBoolean("paid")).thenReturn(false, true);
        when(rs.getDate("fine_date"))
                .thenReturn(Date.valueOf("2025-11-04"),
                            Date.valueOf("2025-11-05"));

        FineDAO dao = new FineDAO();
        List<Fine> fines = dao.findFines(conn, 101);

        assertNotNull(fines);
        assertEquals(2, fines.size());

        Fine f1 = fines.get(0);
        assertEquals(12, f1.getId());
        assertEquals(101, f1.getUserId());
        assertEquals(9001, f1.getBorrowId());
        assertEquals(20.0, f1.getAmount());
        assertFalse(f1.isPaid());

        Fine f2 = fines.get(1);
        assertEquals(13, f2.getId());
        assertEquals(40.0, f2.getAmount());
        assertTrue(f2.isPaid());
    }

    @Test
    void getBorrowingFine_returnsFineWhenExists() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true);
        when(rs.getInt("fine_id")).thenReturn(18);
        when(rs.getInt("user_id")).thenReturn(14);
        when(rs.getInt("borrow_id")).thenReturn(9003);
        when(rs.getDouble("amount")).thenReturn(50.0);
        when(rs.getBoolean("paid")).thenReturn(false);
        when(rs.getDate("fine_date")).thenReturn(Date.valueOf("2025-11-06"));

        FineDAO dao = new FineDAO();
        Fine f = dao.getBorrowingFine(conn, 9003);

        assertNotNull(f);
        assertEquals(18, f.getId());
        assertEquals(14, f.getUserId());
        assertEquals(9003, f.getBorrowId());
        assertEquals(50.0, f.getAmount());
        assertFalse(f.isPaid());
    }

    @Test
    void getBorrowingFine_returnsNullWhenNoRow() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        FineDAO dao = new FineDAO();
        Fine f = dao.getBorrowingFine(conn, 9999);

        assertNull(f);
    }

    @Test
    void updateFineBalance_runsWithoutError() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        FineDAO dao = new FineDAO();
        dao.updateFineBalance(conn, 18, -10.0);

        // No assertion needed; not throwing is enough here
        assertTrue(true);
    }

    @Test
    void updateFineDate_runsWithoutError() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        FineDAO dao = new FineDAO();
        dao.updateFineDate(conn, 18);

        assertTrue(true);
    }

    /**
     * Test for the "already paid" branch of payFine:
     * getFineAmount returns 0, so payFine should return false and not throw.
     */
    @Test
    void payFine_returnsFalseWhenFineAlreadyPaid() throws Exception {
        Connection conn = mock(Connection.class);

        // subclass to override getFineAmount logic
        FineDAO dao = new FineDAO() {
            @Override
            public double getFineAmount(Connection c, int fineId, int userId) {
                return 0.0; // simulate already paid
            }
        };

        boolean ok = dao.payFine(conn, 15, 101, 50.0);

        assertFalse(ok);
    }
}
