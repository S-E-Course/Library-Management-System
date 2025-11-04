package com.library.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.library.model.Borrowing;
import com.library.model.Fine;

/**
 * DAO for issuing, updating, and reading fines.
 * Handles fine creation, payment, queries, and updates.
 *
 * @author
 * @version 1.0
 */
public class FineDAO {

    /**
     * Issues a new fine for a borrowing.
     *
     * @param conn active database connection
     * @param borrowId borrowing identifier
     * @param userId user identifier
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
     * Pays part or all of a fine. Updates the user balance. If fully paid then the borrowing is marked returned and media is set available.
     *
     * @param conn active database connection
     * @param fineId fine identifier
     * @param userId user identifier
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
     * Returns the current amount for a fine.
     *
     * @param conn active database connection
     * @param fineId fine identifier
     * @param userId user identifier
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
     * Checks if a fine for a borrowing is paid.
     *
     * @param conn active database connection
     * @param borrowId borrowing identifier
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
     * @param userId user identifier
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
     * Returns the fine for a borrowing.
     *
     * @param conn active database connection
     * @param borrowId borrowing identifier
     * @return the fine or null if not found
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
     * Updates a fine by adding a delta amount. A positive value increases the fine. A negative value decreases the fine.
     *
     * @param conn active database connection
     * @param fineId fine identifier
     * @param amount delta amount to apply
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
     * Sets the fine date to the current date.
     *
     * @param conn active database connection
     * @param fineId fine identifier
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
