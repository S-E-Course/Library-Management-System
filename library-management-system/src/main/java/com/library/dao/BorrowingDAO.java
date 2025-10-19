package com.library.dao;

import com.library.model.Borrowing;
import com.library.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BorrowingDAO {
    private final BookDAO bookDAO = new BookDAO();

    

    public List<Borrowing> findOverdueBooks() throws Exception {
        List<Borrowing> list = new ArrayList<>();
        String sql = "SELECT * FROM borrowings " +
                     "WHERE (status = 'borrowed' AND due_date < CURRENT_DATE) " +
                     "OR (status = 'returned' AND return_date > due_date)";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
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

    
    public boolean returnBook(int userId, int bookId) throws Exception {
        String selectSql = "SELECT borrow_id, status, due_date FROM borrowings " +
                           "WHERE user_id = ? AND book_id = ? AND status = 'borrowed'";

        String updateSql = "UPDATE borrowings SET status = 'returned', return_date = CURRENT_DATE " +
                           "WHERE borrow_id = ?";

        try (Connection conn = DatabaseConnection.connect()) {
            conn.setAutoCommit(false);

            int borrowId;
            Date dueDate;

            try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, bookId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        borrowId = rs.getInt("borrow_id");
                        dueDate = rs.getDate("due_date");
                    } else {
                        System.out.println("No active borrowing found for this book.");
                        return false;
                    }
                }
            }

            if (dueDate.toLocalDate().isBefore(java.time.LocalDate.now())) {
                System.out.println("Book is overdue and cannot be returned normally.");
                return false;
            }

            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setInt(1, borrowId);
                updateStmt.executeUpdate();
            }

            if (!bookDAO.setBookStatus(conn, bookId, true)) {
                throw new SQLException("Failed to update book availability");
            }

            conn.commit();
            System.out.println("Book returned successfully.");
            return true;
        }
    }
    public boolean borrowBook(int userId, int bookId) throws Exception {
        String insertBorrowSql =
            "INSERT INTO borrowings (user_id, book_id, borrow_date, status) " +
            "VALUES (?, ?, CURRENT_DATE, 'borrowed')";

        try (Connection conn = DatabaseConnection.connect()) {
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
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}
