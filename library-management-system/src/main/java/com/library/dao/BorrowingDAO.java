package com.library.dao;

import com.library.model.Borrowing;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BorrowingDAO {
    private final BookDAO bookDAO = new BookDAO();
    private final FineDAO fineDAO = new FineDAO();

    

    public List<Borrowing> findOverdueBooks(Connection conn) throws Exception {
        List<Borrowing> list = new ArrayList<>();
        String sql = "SELECT * FROM borrowings " +
                     "WHERE (status = 'borrowed' AND due_date < CURRENT_DATE) " +
                     "OR (status = 'returned' AND return_date > due_date)";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Borrowing b = new Borrowing();
                b.setBorrowId(rs.getInt("borrow_id"));
                b.setUserId(rs.getInt("user_id"));
                b.setBookId(rs.getInt("book_id"));
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

    public Borrowing findActiveBorrowing(Connection conn, int userId, int bookId) throws Exception {
        String sql = "SELECT borrow_id, status, due_date FROM borrowings " +
                     "WHERE user_id = ? AND book_id = ? AND status = 'borrowed'";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Borrowing b = new Borrowing();
                    b.setBorrowId(rs.getInt("borrow_id"));
                    b.setStatus(rs.getString("status"));
                    b.setDueDate(rs.getDate("due_date").toLocalDate());
                    b.setUserId(userId);
                    b.setBookId(bookId);
                    return b;
                }
            }
        }
        return null;
    }

    
    public boolean returnBook(Connection conn, int userId, int bookId) throws Exception {
        	conn.setAutoCommit(false);
	        try {
	        	
	            Borrowing borrowing = findActiveBorrowing(conn, userId, bookId);
	            if (borrowing == null) {
	                System.out.println("No active borrowing found for this book.");
	                return false;
	            }
	            
	
	            if (borrowing.isOverdue()) {
	                Boolean paid = fineDAO.isPaid(conn, borrowing.getBorrowId());
	                if (paid == null || !paid) {
	                    System.out.println("Book is overdue and has an unpaid fine.");
	                    conn.rollback();
	                    return false;
	                }
	            }
	
	            String updateSql = "UPDATE borrowings SET status = 'returned', return_date = CURRENT_DATE WHERE borrow_id = ?";
	            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
	                updateStmt.setInt(1, borrowing.getBorrowId());
	                updateStmt.executeUpdate();
	            }
	
	            if (!bookDAO.setBookStatus(conn, bookId, true)) {
	                throw new SQLException("Failed to update book availability");
	            }
	
	            conn.commit();
	            return true;
	        } catch (Exception e) {
	            conn.rollback();
	            throw e;
	        }
    }


    public boolean borrowBook(Connection conn, int userId, int bookId) throws Exception {
        String insertBorrowSql =
            "INSERT INTO borrowings (user_id, book_id, borrow_date, status) " +
            "VALUES (?, ?, CURRENT_DATE, 'borrowed')";

            conn.setAutoCommit(false);
            try {
                boolean updated = bookDAO.setBookStatus(conn, bookId, false);
                if (!updated) {
                    conn.rollback();
                    return false;
                }
                
                
                try (PreparedStatement ins = conn.prepareStatement(insertBorrowSql)) {
                    ins.setInt(1, userId);
                    ins.setInt(2, bookId);
                    ins.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
    }
}
