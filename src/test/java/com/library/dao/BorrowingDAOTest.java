package com.library.dao;

import com.library.model.Borrowing;
import com.library.model.Media;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BorrowingDAO
 */
class BorrowingDAOTest {

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
     * Extra coverage for the branch where return_date is not null.
     *
     * @throws Exception if the DAO call fails
     */
    @Test
    void findOverdueMedia_handlesNonNullReturnDate() throws Exception {
        Connection conn = mock(Connection.class);
        Statement st = mock(Statement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.createStatement()).thenReturn(st);
        when(st.executeQuery(anyString())).thenReturn(rs);
        when(rs.next()).thenReturn(true, true, false);
        when(rs.getInt("borrow_id")).thenReturn(1, 2);
        when(rs.getInt("user_id")).thenReturn(10, 11);
        when(rs.getInt("media_id")).thenReturn(5, 6);
        when(rs.getDate("borrow_date"))
                .thenReturn(Date.valueOf("2025-01-01"),
                            Date.valueOf("2025-01-02"));
        when(rs.getDate("due_date"))
                .thenReturn(Date.valueOf("2025-01-05"),
                            Date.valueOf("2025-01-06"));
        when(rs.getDate("return_date"))
                .thenReturn(null,
                            Date.valueOf("2025-01-07"));
        when(rs.getString("status")).thenReturn("overdue");

        BorrowingDAO dao = new BorrowingDAO();
        List<Borrowing> list = dao.findOverdueMedia(conn);

        assertEquals(2, list.size());
        assertNotNull(list.get(1).getReturnDate());
    }

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
     * Tests mapping of rows in findBorrowings.
     *
     * @throws Exception if the DAO call fails
     */
    @Test
    void findBorrowings_returnsBorrowingsForUser() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, true, false);
        when(rs.getInt("borrow_id")).thenReturn(1, 2);
        when(rs.getString("status")).thenReturn("borrowed", "overdue");
        when(rs.getDate("due_date"))
                .thenReturn(Date.valueOf("2025-01-10"),
                            Date.valueOf("2025-01-11"));
        when(rs.getInt("media_id")).thenReturn(5, 6);

        BorrowingDAO dao = new BorrowingDAO();
        List<Borrowing> list = dao.findBorrowings(conn, 42);

        assertEquals(2, list.size());
        assertEquals(42, list.get(0).getUserId());
        assertEquals(1, list.get(0).getBorrowId());
        assertEquals("overdue", list.get(1).getStatus());
    }

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

    /**
     * No active borrowing: returnMedia should return false without commit or rollback.
     *
     * @throws Exception if the DAO call fails
     */
    @Test
    void returnMedia_returnsFalseWhenNoActiveBorrowing() throws Exception {
        Connection conn = mock(Connection.class);
        BorrowingDAO dao = spy(new BorrowingDAO());

        doReturn(null).when(dao).findActiveBorrowing(conn, 1, 2);

        boolean result = dao.returnMedia(conn, 1, 2);

        assertFalse(result);
        verify(conn, never()).commit();
        verify(conn, never()).rollback();
    }

    /**
     * Happy path for returnMedia: active borrowing is not overdue and media status is updated.
     *
     * @throws Exception if the DAO call fails
     */
    @Test
    void returnMedia_commitsWhenSuccessful() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(conn.prepareStatement(startsWith("UPDATE borrowings"))).thenReturn(ps);

        Borrowing borrowing = mock(Borrowing.class);
        when(borrowing.isOverdue()).thenReturn(false);
        when(borrowing.getBorrowId()).thenReturn(99);

        BorrowingDAO dao = spy(new BorrowingDAO());
        doReturn(borrowing).when(dao).findActiveBorrowing(conn, 1, 2);

        MediaDAO mediaDaoMock = mock(MediaDAO.class);
        when(mediaDaoMock.setMediaStatus(conn, 2, true)).thenReturn(true);
        injectMediaDao(dao, mediaDaoMock);

        boolean result = dao.returnMedia(conn, 1, 2);

        assertTrue(result);
        verify(conn).commit();
        verify(conn, never()).rollback();
    }

    /**
     * Overdue borrowing with fine not paid: method should roll back and return false.
     *
     * @throws Exception if the DAO call fails
     */
    @Test
    void returnMedia_overdueFineUnpaidRollsBackAndReturnsFalse() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement psFine = mock(PreparedStatement.class);
        PreparedStatement psUpdateBorrow = mock(PreparedStatement.class);
        ResultSet rsFine = mock(ResultSet.class);

        when(conn.prepareStatement(anyString()))
                .thenReturn(psFine, psUpdateBorrow);
        when(psFine.executeQuery()).thenReturn(rsFine);
        when(rsFine.next()).thenReturn(true);
        when(rsFine.getBoolean("paid")).thenReturn(false);

        Borrowing borrowing = mock(Borrowing.class);
        when(borrowing.isOverdue()).thenReturn(true);
        when(borrowing.getBorrowId()).thenReturn(50);

        BorrowingDAO dao = spy(new BorrowingDAO());
        doReturn(borrowing).when(dao).findActiveBorrowing(conn, 1, 2);

        boolean result = dao.returnMedia(conn, 1, 2);

        assertFalse(result);
        verify(conn).rollback();
        verify(conn, never()).commit();
    }

    /**
     * Overdue borrowing with fine fully paid: method should update status, update media, and commit.
     *
     * @throws Exception if the DAO call fails
     */
    @Test
    void returnMedia_overdueFinePaidCommits() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement psFine = mock(PreparedStatement.class);
        PreparedStatement psUpdateBorrow = mock(PreparedStatement.class);
        ResultSet rsFine = mock(ResultSet.class);

        when(conn.prepareStatement(anyString()))
                .thenReturn(psFine, psUpdateBorrow);
        when(psFine.executeQuery()).thenReturn(rsFine);
        when(rsFine.next()).thenReturn(true);
        when(rsFine.getBoolean("paid")).thenReturn(true);

        Borrowing borrowing = mock(Borrowing.class);
        when(borrowing.isOverdue()).thenReturn(true);
        when(borrowing.getBorrowId()).thenReturn(60);

        BorrowingDAO dao = spy(new BorrowingDAO());
        doReturn(borrowing).when(dao).findActiveBorrowing(conn, 1, 2);

        MediaDAO mediaDaoMock = mock(MediaDAO.class);
        when(mediaDaoMock.setMediaStatus(conn, 2, true)).thenReturn(true);
        injectMediaDao(dao, mediaDaoMock);

        boolean result = dao.returnMedia(conn, 1, 2);

        assertTrue(result);
        verify(conn).commit();
        verify(conn, never()).rollback();
    }

    /**
     * If updating media availability fails in returnMedia, the method should roll back and throw.
     *
     * @throws Exception if the DAO call fails
     */
    @Test
    void returnMedia_rollsBackOnMediaStatusFailure() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(conn.prepareStatement(startsWith("UPDATE borrowings"))).thenReturn(ps);

        Borrowing borrowing = mock(Borrowing.class);
        when(borrowing.isOverdue()).thenReturn(false);
        when(borrowing.getBorrowId()).thenReturn(100);

        BorrowingDAO dao = spy(new BorrowingDAO());
        doReturn(borrowing).when(dao).findActiveBorrowing(conn, 1, 2);

        MediaDAO mediaDaoMock = mock(MediaDAO.class);
        when(mediaDaoMock.setMediaStatus(conn, 2, true)).thenReturn(false);
        injectMediaDao(dao, mediaDaoMock);

        assertThrows(Exception.class, () -> dao.returnMedia(conn, 1, 2));
        verify(conn).rollback();
        verify(conn, never()).commit();
    }

    /**
     * borrowMedia: media not found.
     *
     * @throws Exception if the DAO call fails
     */
    @Test
    void borrowMedia_returnsFalseWhenMediaNotFound() throws Exception {
        Connection conn = mock(Connection.class);

        BorrowingDAO dao = spy(new BorrowingDAO());
        MediaDAO mediaDaoMock = mock(MediaDAO.class);
        when(mediaDaoMock.findById(conn, 2)).thenReturn(null);
        injectMediaDao(dao, mediaDaoMock);

        boolean result = dao.borrowMedia(conn, 1, 2);

        assertFalse(result);
        verify(conn, never()).commit();
        verify(conn, never()).rollback();
    }

    /**
     * borrowMedia: media already borrowed.
     *
     * @throws Exception if the DAO call fails
     */
    @Test
    void borrowMedia_returnsFalseWhenMediaUnavailable() throws Exception {
        Connection conn = mock(Connection.class);

        Media media = mock(Media.class);
        when(media.isAvailable()).thenReturn(false);

        BorrowingDAO dao = spy(new BorrowingDAO());
        MediaDAO mediaDaoMock = mock(MediaDAO.class);
        when(mediaDaoMock.findById(conn, 2)).thenReturn(media);
        injectMediaDao(dao, mediaDaoMock);

        boolean result = dao.borrowMedia(conn, 1, 2);

        assertFalse(result);
        verify(conn, never()).commit();
    }

    /**
     * borrowMedia: happy path.
     *
     * @throws Exception if the DAO call fails
     */
    @Test
    void borrowMedia_commitsWhenSuccessful() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(conn.prepareStatement(startsWith("INSERT INTO borrowings"))).thenReturn(ps);

        Media media = mock(Media.class);
        when(media.isAvailable()).thenReturn(true);
        when(media.getBorrowDurationDays()).thenReturn(7);

        BorrowingDAO dao = spy(new BorrowingDAO());
        MediaDAO mediaDaoMock = mock(MediaDAO.class);
        when(mediaDaoMock.findById(conn, 2)).thenReturn(media);
        when(mediaDaoMock.setMediaStatus(conn, 2, false)).thenReturn(true);
        injectMediaDao(dao, mediaDaoMock);

        boolean result = dao.borrowMedia(conn, 1, 2);

        assertTrue(result);
        verify(conn).commit();
    }

    /**
     * borrowMedia: failure when updating media status.
     *
     * @throws Exception if the DAO call fails
     */
    @Test
    void borrowMedia_rollsBackOnMediaStatusFailure() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(conn.prepareStatement(startsWith("INSERT INTO borrowings"))).thenReturn(ps);

        Media media = mock(Media.class);
        when(media.isAvailable()).thenReturn(true);
        when(media.getBorrowDurationDays()).thenReturn(7);

        BorrowingDAO dao = spy(new BorrowingDAO());
        MediaDAO mediaDaoMock = mock(MediaDAO.class);
        when(mediaDaoMock.findById(conn, 2)).thenReturn(media);
        when(mediaDaoMock.setMediaStatus(conn, 2, false)).thenReturn(false);
        injectMediaDao(dao, mediaDaoMock);

        assertThrows(Exception.class, () -> dao.borrowMedia(conn, 1, 2));
        verify(conn).rollback();
        verify(conn, never()).commit();
    }

    /**
     * getFineBorrowing: row found.
     *
     * @throws Exception if the DAO call fails
     */
    @Test
    void getFineBorrowing_returnsBorrowingWhenFound() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt("borrow_id")).thenReturn(9);
        when(rs.getInt("user_id")).thenReturn(3);
        when(rs.getInt("media_id")).thenReturn(4);
        when(rs.getDate("borrow_date")).thenReturn(Date.valueOf("2025-01-01"));
        when(rs.getDate("due_date")).thenReturn(Date.valueOf("2025-01-10"));
        when(rs.getDate("return_date")).thenReturn(null);
        when(rs.getString("status")).thenReturn("overdue");

        BorrowingDAO dao = new BorrowingDAO();
        Borrowing b = dao.getFineBorrowing(conn, 100);

        assertNotNull(b);
        assertEquals(9, b.getBorrowId());
        assertEquals("overdue", b.getStatus());
    }

    @Test
    void getFineBorrowing_returnsNullWhenNotFound() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        BorrowingDAO dao = new BorrowingDAO();
        Borrowing b = dao.getFineBorrowing(conn, 100);

        assertNull(b);
    }

    /**
     * updateBorrowingStatus should execute an update with the given values.
     *
     * @throws Exception if the DAO call fails
     */
    @Test
    void updateBorrowingStatus_updatesRow() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);

        BorrowingDAO dao = new BorrowingDAO();
        dao.updateBorrowingStatus(conn, 50, "returned");

        verify(ps).setString(1, "returned");
        verify(ps).setInt(2, 50);
        verify(ps).executeUpdate();
    }

    /**
     * Injects a mocked {@link MediaDAO} into a {@link BorrowingDAO} instance.
     *
     * @param dao target DAO
     * @param mediaDaoMock media DAO mock
     * @throws Exception if reflection fails
     */
    private static void injectMediaDao(BorrowingDAO dao, MediaDAO mediaDaoMock) throws Exception {
        Field f = BorrowingDAO.class.getDeclaredField("mediaDAO");
        f.setAccessible(true);
        f.set(dao, mediaDaoMock);
    }
}
