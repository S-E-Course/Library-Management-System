package com.library.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.library.model.Borrowing;
import com.library.model.Fine;

/**
 * Handles database operations for fines.
 * Supports creating, paying, reading, and updating fines.
 *
 * @author Abdallah
 * @version 1.0
 */
public class FineDAO {

    /**
     * Creates a new fine for a borrowing.
     *
     * @param conn active database connection
     * @param borrowId borrowing id
     * @param userId user id
     * @param amount fine amount
     * @return true if the fine was inserted
     * @throws Exception if a database error occurs
     */
    public boolean issueFine(Connection conn, int borrowId, int userId, double amount) throws Exception {
        String sql = "INSERT INTO fines (user_id, borrow_id, amount, paid) VALUES (?, ?, ?, FALSE)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, borrowId);
            stmt.setDouble(3, amount);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Pays part or all of a fine.
     * Updates the user balance and, if fully paid, marks the borrowing as returned and media as available.
     *
     * @param conn active database connection
     * @param fineId fine id
     * @param userId user id
     * @param amount amount to pay
     * @return true if the update succeeded
     * @throws Exception if a database error occurs
     */
    public boolean payFine(Connection conn, int fineId, int userId, double amount) throws Exception {
        conn.setAutoCommit(false);
        try {
            double fineAmount = getFineAmount(conn, fineId, userId);
            if (fineAmount <= 0) {
                System.out.println("This fine is already paid.");
                return false;
            }

            double payment = Math.min(amount, fineAmount);
            double newAmount = fineAmount - payment;
            boolean paid = newAmount <= 0;

            String updateSql = "UPDATE fines SET amount = ?, paid = ? WHERE fine_id = ? AND user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setDouble(1, newAmount);
                stmt.setBoolean(2, paid);
                stmt.setInt(3, fineId);
                stmt.setInt(4, userId);
                stmt.executeUpdate();
            }

            UserDAO userDAO = new UserDAO();
            userDAO.updateUserBalance(conn, userId, -payment);

            if (paid) {
                BorrowingDAO borrowingDAO = new BorrowingDAO();
                Borrowing b = borrowingDAO.getFineBorrowing(conn, fineId);
                if (b != null) {
                    borrowingDAO.updateBorrowingStatus(conn, b.getBorrowId(), "returned");

                    MediaDAO mediaDAO = new MediaDAO();
                    mediaDAO.setMediaStatus(conn, b.getMediaId(), paid);
                }
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    /**
     * Gets the current amount of a fine.
     *
     * @param conn active database connection
     * @param fineId fine id
     * @param userId user id
     * @return current fine amount
     * @throws SQLException if the fine is not found or a database error occurs
     */
    public double getFineAmount(Connection conn, int fineId, int userId) throws SQLException {
        String sql = "SELECT amount FROM fines WHERE fine_id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fineId);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("amount");
                }
                throw new SQLException("Fine not found");
            }
        }
    }

    /**
     * Checks if a fine for a given borrowing is paid.
     *
     * @param conn active database connection
     * @param borrowId borrowing id
     * @return true if paid, false if not paid, or null if no fine exists
     * @throws Exception if a database error occurs
     */
    public Boolean isPaid(Connection conn, int borrowId) throws Exception {
        String sql = "SELECT paid FROM fines WHERE borrow_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, borrowId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getBoolean("paid");
            }
        }
        return null;
    }

    /**
     * Returns all fines for a user.
     *
     * @param conn active database connection
     * @param userId user id
     * @return list of fines for the user
     * @throws Exception if a database error occurs
     */
    public List<Fine> findFines(Connection conn, int userId) throws Exception {
        List<Fine> fines = new ArrayList<>();
        String sql = "SELECT * FROM fines WHERE user_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Fine f = new Fine();
                    f.setId(rs.getInt("fine_id"));
                    f.setUserId(userId);
                    f.setBorrowId(rs.getInt("borrow_id"));
                    f.setAmount(rs.getDouble("amount"));
                    f.setPaid(rs.getBoolean("paid"));
                    f.setFineDate(rs.getDate("fine_date").toLocalDate());
                    fines.add(f);
                }
            }
        }
        return fines;
    }

    /**
     * Returns the fine linked to a borrowing.
     *
     * @param conn active database connection
     * @param borrowId borrowing id
     * @return fine or null if not found
     * @throws Exception if a database error occurs
     */
    public Fine getBorrowingFine(Connection conn, int borrowId) throws Exception {
        String sql = "SELECT * FROM fines WHERE borrow_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, borrowId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Fine fine = new Fine();
                    fine.setId(rs.getInt("fine_id"));
                    fine.setUserId(rs.getInt("user_id"));
                    fine.setBorrowId(rs.getInt("borrow_id"));
                    fine.setAmount(rs.getDouble("amount"));
                    fine.setPaid(rs.getBoolean("paid"));
                    fine.setFineDate(rs.getDate("fine_date").toLocalDate());
                    return fine;
                }
            }
        }
        return null;
    }

    /**
     * Changes the fine amount by a given value.
     * A positive amount increases the fine; a negative amount decreases it.
     *
     * @param conn active database connection
     * @param fineId fine id
     * @param amount change to apply to the fine
     * @throws Exception if a database error occurs
     */
    public void updateFineBalance(Connection conn, int fineId, double amount) throws Exception {
        String sql = "UPDATE fines SET amount = amount + ? WHERE fine_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, amount);
            stmt.setInt(2, fineId);
            stmt.executeUpdate();
        }
    }

    /**
     * Sets the fine date to today's date.
     *
     * @param conn active database connection
     * @param fineId fine id
     * @throws Exception if a database error occurs
     */
    public void updateFineDate(Connection conn, int fineId) throws Exception {
        String sql = "UPDATE fines SET fine_date = CURRENT_DATE WHERE fine_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fineId);
            stmt.executeUpdate();
        }
    }

}
