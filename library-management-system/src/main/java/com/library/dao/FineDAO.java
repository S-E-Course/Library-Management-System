package com.library.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.library.model.Borrowing;
import com.library.model.Fine;


public class FineDAO {
	
	/**
     * Issues a new fine for a specific borrowing.
     * @param conn    active DB connection
     * @param borrowId related borrowing record
     * @param userId  the fined user's ID
     * @param amount  fine amount
     * @return true if inserted successfully
	 * @throws Exception
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
     * Allows a user to pay all or part of the fine.
     * Decreases fine amount.
     * Marks the fine as paid if balance reaches 0.
     * Updates user total balance and borrowing status if fully paid.
     * @param conn   DB connection
     * @param fineId fine record ID
     * @param userId user who pays
     * @param amount amount paid
     * @return true if update was successful
     * @throws Exception if any SQL error occurs
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
     * Gets the current fine balance for a specific fine record.
     * @param conn
     * @param fineId
     * @param userId
     * @return
     * @throws SQLException
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
     * Checks if a fine for a borrowing has been paid.
     * @param conn
     * @param borrowId
     * @return TRUE if paid, FALSE if not, or NULL if no fine exists
     * @throws Exception
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
     * Retrieves the fine associated with a specific borrowing record.
     * @param conn active DB connection
     * @param borrowId the borrowing ID to look up
     * @return Fine object if found, otherwise null
     * @throws Exception if SQL error occurs
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
     * Adds or subtracts from the fine amount.
     * Positive amount → increases fine.
     * Negative amount → decreases fine.
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
     * Updates fine date to the current date.
     */
    public void updateFineDate(Connection conn, int fineId) throws Exception {
        String sql = "UPDATE fines SET fine_date = CURRENT_DATE WHERE fine_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fineId);
            stmt.executeUpdate();
        }
    }


}
