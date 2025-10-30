package com.library.dao;

import com.library.model.Borrowing;
import com.library.model.Media;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for handling borrow and return operations of media (books, CDs, journals).
 * 
 * Supports dynamic due date based on media type:
 *  - Book: 28 days
 *  - CD: 7 days
 *  - Journal: 14 days
 */
public class BorrowingDAO {

    private final MediaDAO mediaDAO = new MediaDAO();

    /**
     * Finds all overdue borrowings.
     * @return list of overdue borrowings
     */
    public List<Borrowing> findOverdueMedia(Connection conn) throws Exception {
        List<Borrowing> list = new ArrayList<>();
        String sql = "SELECT * FROM borrowings " +
                     "WHERE (status = 'borrowed' AND due_date < CURRENT_DATE) " +
                     "OR (status = 'overdue')";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Borrowing b = new Borrowing();
                b.setBorrowId(rs.getInt("borrow_id"));
                b.setUserId(rs.getInt("user_id"));
                b.setMediaId(rs.getInt("media_id"));
                b.setBorrowDate(rs.getDate("borrow_date").toLocalDate());
                b.setDueDate(rs.getDate("due_date").toLocalDate());
                Date ret = rs.getDate("return_date");
                if (ret != null) b.setReturnDate(ret.toLocalDate());
                b.setStatus(rs.getString("status"));
                list.add(b);
            }
        }
        return list;
    }
    
    public List<Borrowing> findBorrowings(Connection conn, int userId) throws Exception {
        List<Borrowing> list = new ArrayList<>();
        String sql = "SELECT * FROM borrowings " +
                     "WHERE user_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Borrowing b = new Borrowing();
                    b.setBorrowId(rs.getInt("borrow_id"));
                    b.setStatus(rs.getString("status"));
                    b.setDueDate(rs.getDate("due_date").toLocalDate());
                    b.setUserId(userId);
                    b.setMediaId(rs.getInt("media_id"));
                    list.add(b);
                }
            }
        }
        return list;
    }

    /**
     * Finds an active borrowing for a specific user and media item.
     */
    public Borrowing findActiveBorrowing(Connection conn, int userId, int mediaId) throws Exception {
        String sql = "SELECT borrow_id, status, due_date FROM borrowings " +
                     "WHERE user_id = ? AND media_id = ? AND (status = 'borrowed' OR status = 'overdue')";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, mediaId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Borrowing b = new Borrowing();
                    b.setBorrowId(rs.getInt("borrow_id"));
                    b.setStatus(rs.getString("status"));
                    b.setDueDate(rs.getDate("due_date").toLocalDate());
                    b.setUserId(userId);
                    b.setMediaId(mediaId);
                    return b;
                }
            }
        }
        return null;
    }

    /**
     * Handles returning a borrowed media item.
     * Checks for overdue status and unpaid fines.
     */
    public boolean returnMedia(Connection conn, int userId, int mediaId) throws Exception {
        conn.setAutoCommit(false);
        try {
            Borrowing borrowing = findActiveBorrowing(conn, userId, mediaId);
            if (borrowing == null) {
                System.out.println("No active borrowing found for this media.");
                return false;
            }

            if (borrowing.isOverdue()) {
                FineDAO fineDAO = new FineDAO();
                Boolean paid = fineDAO.isPaid(conn, borrowing.getBorrowId());
                if (paid == null || !paid) {
                    System.out.println("Media is overdue and fine is unpaid.");
                    conn.rollback();
                    return false;
                }
            }

            String updateSql = "UPDATE borrowings SET status = 'returned', return_date = CURRENT_DATE WHERE borrow_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setInt(1, borrowing.getBorrowId());
                stmt.executeUpdate();
            }

            if (!mediaDAO.setMediaStatus(conn, mediaId, true)) {
                throw new SQLException("Failed to update media availability");
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            conn.rollback();
            throw e;
        }
    }

    /**
     * Handle borrowing a media item.
     * Calculates due date based on media type.
     */
    public boolean borrowMedia(Connection conn, int userId, int mediaId) throws Exception {
        conn.setAutoCommit(false);
        try {
            Media media = mediaDAO.findById(conn, mediaId);
            if (media == null) {
                System.out.println("Media not found.");
                return false;
            }
            if (!media.isAvailable()) {
                System.out.println("Media is already borrowed.");
                return false;
            }

            int borrowDays = media.getBorrowDurationDays();

            String sql = "INSERT INTO borrowings (user_id, media_id, borrow_date, due_date, status) " +
                         "VALUES (?, ?, CURRENT_DATE, CURRENT_DATE + INTERVAL '" + borrowDays + " days', 'borrowed')";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, mediaId);
                stmt.executeUpdate();
            }

            if (!mediaDAO.setMediaStatus(conn, mediaId, false)) {
                throw new SQLException("Failed to update media availability");
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            conn.rollback();
            throw e;
        }
    }
    
    /**
     * Retrieves the borrowing record associated with a specific fine.
     * @param conn active DB connection
     * @param fineId the fine ID
     * @return Borrowing object if found, otherwise null
     * @throws Exception if SQL error occurs
     */
    public Borrowing getFineBorrowing(Connection conn, int fineId) throws Exception {
    	String sql =
    	        "SELECT * " +
    	        "FROM borrowings b " +
    	        "JOIN fines f ON b.borrow_id = f.borrow_id " +
    	        "WHERE f.fine_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fineId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Borrowing b = new Borrowing();
                    b.setBorrowId(rs.getInt("borrow_id"));
                    b.setUserId(rs.getInt("user_id"));
                    b.setMediaId(rs.getInt("media_id"));
                    Date borrowDate = rs.getDate("borrow_date");
                    Date dueDate = rs.getDate("due_date");
                    Date returnDate = rs.getDate("return_date");
                    
                    if (borrowDate != null) b.setBorrowDate(borrowDate.toLocalDate());
                    if (dueDate != null) b.setDueDate(dueDate.toLocalDate());
                    if (returnDate != null) b.setReturnDate(returnDate.toLocalDate());
                    
                    b.setStatus(rs.getString("status"));
                    return b;
                }
            }
        }
        return null;
    }
    
    /**
     * Updates the status of a borrowing record
     * @param conn active database connection
     * @param borrowingId the borrowing record to update
     * @param newStatus the new status value
     * @throws Exception if SQL operation fails
     */
    public void updateBorrowingStatus(Connection conn, int borrowingId, String newStatus) throws Exception {
        String sql = "UPDATE borrowings SET status = ? WHERE borrow_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, borrowingId);
            stmt.executeUpdate();
        }
    }

}
