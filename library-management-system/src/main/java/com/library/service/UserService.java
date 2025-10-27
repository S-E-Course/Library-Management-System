package com.library.service;

import com.library.dao.*;
import com.library.model.*;
import com.library.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserService {
	Connection conn;
    private final UserDAO userDAO = new UserDAO();
    private final BookDAO bookDAO = new BookDAO();
    private final BorrowingDAO borrowingDAO = new BorrowingDAO();
    private final FineDAO fineDAO = new FineDAO();
    private User loggedUser;
    
    
    public UserService() throws Exception {
        this.conn = DatabaseConnection.connect();
    }

    public boolean login(String username, String passwordHash) throws Exception {
        User user = userDAO.findByUsername(conn, username);
        if (user != null && user.getPasswordHash().equals(passwordHash)) {
            loggedUser = user;
            return true;
        }
        return false;
    }

    public void logout() throws SQLException {
        loggedUser = null;
        DatabaseConnection.disconnect();
    }

    public List<Book> searchBooks(String keyword) throws Exception {
        return bookDAO.searchBooks(conn, keyword);
    }

    public boolean borrowBook(int bookId) throws Exception {
        if (loggedUser == null) throw new IllegalStateException("User not logged in.");
        Book b = bookDAO.findById(conn, bookId);
        if (b == null) {
            System.out.println("Book does not exist.");
            return false;
        }
        if (!b.isAvailable()) {
            System.out.println("Book is already borrowed.");
            return false;
        }
        double balance = userDAO.getUserBalance(conn, loggedUser.getUserId());
        if (balance > 0) {
            System.out.println("User has unpaid balance and cannot borrow.");
            return false;
        }
        return borrowingDAO.borrowBook(conn, loggedUser.getUserId(), bookId);
    }
    
    public boolean returnBook(int bookId) throws Exception {
        if (loggedUser == null) throw new IllegalStateException("User not logged in");
        
        return borrowingDAO.returnBook(conn, loggedUser.getUserId(), bookId);
    }

    public boolean payFine(int fineId, double amount) throws Exception {
        if (loggedUser == null) throw new IllegalStateException("User not logged in");
        return fineDAO.payFine(conn, fineId, loggedUser.getUserId(), amount);
    }

    public User getLoggedUser() {
        return loggedUser;
    }
}
