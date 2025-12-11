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

    /**
     * Verifies that issueFine returns true when the insert succeeds.
     *
     * @throws Exception if the DAO call fails
     */
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

    /**
     * Verifies that getFineAmount returns the amount when a matching row exists.
     *
     * @throws Exception if the DAO call fails
     */
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

    /**
     * Verifies that getFineAmount throws when the fine is not found.
     *
     * @throws Exception if the DAO call fails
     */
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

    /**
     * Verifies that isPaid can return true, false, or null depending on rows.
     *
     * @throws Exception if the DAO call fails
     */
    @Test
    void isPaid_returnsTrueFalseOrNull() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
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

    /**
     * Verifies that findFines returns the user fines mapped from the result set.
     *
     * @throws Exception if the DAO call fails
     */
    @Test
    void findFines_returnsListOfUserFines() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
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

    /**
     * Verifies that getBorrowingFine returns a mapped Fine object when a row exists.
     *
     * @throws Exception if the DAO call fails
     */
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

    /**
     * Verifies that getBorrowingFine returns null when there is no matching row.
     *
     * @throws Exception if the DAO call fails
     */
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

    /**
     * Verifies that updateFineBalance executes without error.
     *
     * @throws Exception if the DAO call fails
     */
    @Test
    void updateFineBalance_runsWithoutError() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        FineDAO dao = new FineDAO();
        dao.updateFineBalance(conn, 18, -10.0);

        assertTrue(true);
    }

    /**
     * Verifies that updateFineDate executes without error.
     *
     * @throws Exception if the DAO call fails
     */
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
     * Covers the branch in payFine where the fine is already fully paid.
     * In this case getFineAmount returns a non-positive value, so payFine returns false.
     *
     * @throws Exception if the DAO call fails
     */
    @Test
    void payFine_returnsFalseWhenFineAlreadyPaid() throws Exception {
        Connection conn = mock(Connection.class);

        FineDAO dao = new FineDAO() {
            @Override
            public double getFineAmount(Connection c, int fineId, int userId) {
                return 0.0;
            }
        };

        boolean ok = dao.payFine(conn, 15, 101, 50.0);

        assertFalse(ok);
        verify(conn).setAutoCommit(false);
        verify(conn, atLeastOnce()).setAutoCommit(true);
        verify(conn, never()).commit();
    }

    /**
     * Covers the branch in payFine where the user makes a partial payment.
     * The fine remains unpaid and the method commits the transaction.
     *
     * @throws Exception if the DAO call fails
     */
    @Test
    void payFine_partialPaymentLeavesUnpaidAndCommits() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement psSelect = mock(PreparedStatement.class);
        PreparedStatement psUpdateFine = mock(PreparedStatement.class);
        PreparedStatement psUpdateUser = mock(PreparedStatement.class);
        ResultSet rsAmount = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(
                psSelect,
                psUpdateFine,
                psUpdateUser
        );
        when(psSelect.executeQuery()).thenReturn(rsAmount);
        when(rsAmount.next()).thenReturn(true);
        when(rsAmount.getDouble("amount")).thenReturn(50.0);
        when(psUpdateFine.executeUpdate()).thenReturn(1);
        when(psUpdateUser.executeUpdate()).thenReturn(1);

        FineDAO dao = new FineDAO();
        boolean ok = dao.payFine(conn, 10, 101, 20.0);

        assertTrue(ok);
        verify(conn).setAutoCommit(false);
        verify(conn).commit();
        verify(conn, atLeastOnce()).setAutoCommit(true);
    }

    /**
     * Covers the branch in payFine where the user pays the fine in full
     * and the transaction is committed, but the fine has no related borrowing.
     *
     * @throws Exception if the DAO call fails
     */
    @Test
    void payFine_fullPaymentMarksPaidAndCommits() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement psSelectFine = mock(PreparedStatement.class);
        PreparedStatement psUpdateFine = mock(PreparedStatement.class);
        PreparedStatement psUpdateUser = mock(PreparedStatement.class);
        PreparedStatement psSelectBorrow = mock(PreparedStatement.class);
        ResultSet rsAmount = mock(ResultSet.class);
        ResultSet rsBorrow = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(
                psSelectFine,
                psUpdateFine,
                psUpdateUser,
                psSelectBorrow
        );
        when(psSelectFine.executeQuery()).thenReturn(rsAmount);
        when(rsAmount.next()).thenReturn(true);
        when(rsAmount.getDouble("amount")).thenReturn(30.0);
        when(psUpdateFine.executeUpdate()).thenReturn(1);
        when(psUpdateUser.executeUpdate()).thenReturn(1);
        when(psSelectBorrow.executeQuery()).thenReturn(rsBorrow);
        when(rsBorrow.next()).thenReturn(false);

        FineDAO dao = new FineDAO();
        boolean ok = dao.payFine(conn, 11, 201, 30.0);

        assertTrue(ok);
        verify(conn).setAutoCommit(false);
        verify(conn).commit();
        verify(conn, atLeastOnce()).setAutoCommit(true);
    }

    /**
     * Covers the exception branch in payFine. If an exception occurs while
     * reading the fine amount, the method should roll back and rethrow.
     *
     * @throws Exception if the DAO call fails
     */
    @Test
    void payFine_rollsBackAndRethrowsOnException() throws Exception {
        Connection conn = mock(Connection.class);

        FineDAO dao = new FineDAO() {
            @Override
            public double getFineAmount(Connection c, int fineId, int userId) throws SQLException {
                throw new SQLException("fail");
            }
        };

        assertThrows(SQLException.class, () -> dao.payFine(conn, 5, 100, 10.0));

        verify(conn).setAutoCommit(false);
        verify(conn).rollback();
        verify(conn, atLeastOnce()).setAutoCommit(true);
    }

    /**
     * Covers the branch in payFine where the fine is fully paid and the
     * associated borrowing exists, so both the borrowing status and the
     * media availability are updated before committing.
     *
     * @throws Exception if the DAO call fails
     */
    @Test
    void payFine_fullPaymentWithBorrowingUpdatesStatusAndMedia() throws Exception {
        Connection conn = mock(Connection.class);

        PreparedStatement psSelectFine = mock(PreparedStatement.class);
        PreparedStatement psUpdateFine = mock(PreparedStatement.class);
        PreparedStatement psUpdateUser = mock(PreparedStatement.class);
        PreparedStatement psSelectBorrow = mock(PreparedStatement.class);
        PreparedStatement psUpdateBorrow = mock(PreparedStatement.class);
        PreparedStatement psUpdateMedia = mock(PreparedStatement.class);

        ResultSet rsAmount = mock(ResultSet.class);
        ResultSet rsBorrow = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(
                psSelectFine,  
                psUpdateFine,
                psUpdateUser,   
                psSelectBorrow, 
                psUpdateBorrow, 
                psUpdateMedia   
        );

        when(psSelectFine.executeQuery()).thenReturn(rsAmount);
        when(rsAmount.next()).thenReturn(true);
        when(rsAmount.getDouble("amount")).thenReturn(30.0);
        when(psUpdateFine.executeUpdate()).thenReturn(1);
        when(psUpdateUser.executeUpdate()).thenReturn(1);

        when(psSelectBorrow.executeQuery()).thenReturn(rsBorrow);
        when(rsBorrow.next()).thenReturn(true);
        when(rsBorrow.getInt("borrow_id")).thenReturn(77);
        when(rsBorrow.getInt("user_id")).thenReturn(7);
        when(rsBorrow.getInt("media_id")).thenReturn(55);
        when(rsBorrow.getDate("borrow_date")).thenReturn(Date.valueOf("2025-01-01"));
        when(rsBorrow.getDate("due_date")).thenReturn(Date.valueOf("2025-01-10"));
        when(rsBorrow.getDate("return_date")).thenReturn(null);
        when(rsBorrow.getString("status")).thenReturn("overdue");

        when(psUpdateBorrow.executeUpdate()).thenReturn(1);
        when(psUpdateMedia.executeUpdate()).thenReturn(1);

        FineDAO dao = new FineDAO();
        boolean ok = dao.payFine(conn, 20, 300, 30.0);

        assertTrue(ok);
        verify(conn).setAutoCommit(false);
        verify(conn).commit();
        verify(conn, atLeastOnce()).setAutoCommit(true);
        verify(psUpdateBorrow).executeUpdate();
        verify(psUpdateMedia).executeUpdate();
    }
}
