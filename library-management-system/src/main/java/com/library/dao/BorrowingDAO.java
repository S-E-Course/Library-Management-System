package com.library.dao;

import com.library.model.Borrowing;
import com.library.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BorrowingDAO {
    private final UserDAO userDAO = new UserDAO();
    private final BookDAO bookDAO = new BookDAO();

    
    public boolean borrowBook(int userId, int bookId) throws Exception {
    	
        double balance = userDAO.getUserBalance(userId);
        if (balance > 0) {
            System.out.println("User has unpaid balance and cannot borrow.");
            return false;
        }

        
        boolean available = bookDAO.bookAvailable(bookId);
        if (!available) {
            System.out.println("Book is not available.");
            return false;
        }

        
        String sqlBorrow =
        	    "INSERT INTO borrowings (user_id, book_id, borrow_date, status) " +
        	    "VALUES (?, ?, CURRENT_DATE, 'borrowed')";


        try (Connection conn = DatabaseConnection.connect()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sqlBorrow)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, bookId);
                stmt.executeUpdate();
                
                
                if (!bookDAO.setBookStatus(bookId, false)) {
                    throw new SQLException("Failed to update book status");
                }

                conn.commit();
                System.out.println("Book borrowed successfully.");
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    
    
    public List<Borrowing> findOverdueBooks() throws Exception {
        List<Borrowing> list = new ArrayList<>();
        
        String sql =
        	    "SELECT * FROM borrowings " +
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
}
