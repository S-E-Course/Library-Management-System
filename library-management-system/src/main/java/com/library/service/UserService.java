package com.library.service;

import com.library.dao.*;
import com.library.model.*;
import com.library.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


public class UserService {
    private final Connection conn;
    private final UserDAO userDAO = new UserDAO();
    private final MediaDAO mediaDAO = new MediaDAO();
    private final BorrowingDAO borrowingDAO = new BorrowingDAO();
    private final FineDAO fineDAO = new FineDAO();
    private User loggedUser;


    public UserService() throws Exception {
        this.conn = DatabaseConnection.connect();
    }

    /** Login using username + password hash */
    public boolean login(String username, String passwordHash) throws Exception {
        User user = userDAO.findByUsername(conn, username);
        if (user != null && user.getPasswordHash().equals(passwordHash)) {
            loggedUser = user;
            return true;
        }
        return false;
    }

    /** Logout and close DB connection */
    public void logout() throws SQLException {
        loggedUser = null;
        DatabaseConnection.disconnect();
    }

    /**
     * Search for media (books, CDs, journals).
     * 
     * @param keyword text to search (title, author, ISBN)
     * @param type "book", "cd", "journal", or "media" for all
     */
    public List<Media> searchMedia(String keyword, String type) throws Exception {
        return mediaDAO.searchMedia(conn, keyword, type);
    }

    /**
     * Borrow media if:
     * User is logged in
     * Media is available
     * User has no unpaid balance
     */
    public boolean borrowMedia(int mediaId) throws Exception {
        if (loggedUser == null)
            throw new IllegalStateException("User not logged in.");

        Media media = mediaDAO.findById(conn, mediaId);
        if (media == null) {
            System.out.println("Media not found.");
            return false;
        }

        if (!media.isAvailable()) {
            System.out.println("Media is already borrowed.");
            return false;
        }

        double balance = userDAO.getUserBalance(conn, loggedUser.getUserId());
        if (balance > 0) {
            System.out.println("User has unpaid balance and cannot borrow.");
            return false;
        }

        return borrowingDAO.borrowMedia(conn, loggedUser.getUserId(), mediaId);
    }
    
    /**
     * Return borrowed media.
     */
    public boolean returnMedia(int mediaId) throws Exception {
        if (loggedUser == null)
            throw new IllegalStateException("User not logged in.");

        return borrowingDAO.returnMedia(conn, loggedUser.getUserId(), mediaId);
    }

    /**
     * Pay fines partially or fully.
     * Updates user's balance.
     */
    public boolean payFine(int fineId, double amount) throws Exception {
        if (loggedUser == null)
            throw new IllegalStateException("User not logged in.");

        return fineDAO.payFine(conn, fineId, loggedUser.getUserId(), amount);
    }

    /** Get the currently logged-in user. */
    public User getLoggedUser() {
        return loggedUser;
    }
    
    public Connection getUserConnection() {
        return conn;
    }
    
    public List<Borrowing> findBorrowings(int userId) throws Exception {
        if (loggedUser == null)
            throw new IllegalStateException("User not logged in.");
        return borrowingDAO.findBorrowings(conn, userId);
    }
    
    public List<Fine> findFines(int userId) throws Exception {
        if (loggedUser == null)
            throw new IllegalStateException("User not logged in.");
        return fineDAO.findFines(conn, userId);
    }
}
