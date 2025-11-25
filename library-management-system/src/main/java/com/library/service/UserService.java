package com.library.service;

import com.library.dao.*;
import com.library.model.*;
import com.library.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Service for end-user actions: login, search, borrow, return,
 * paying fines, and creating the mixed-media fine summary (US5.3).
 */
public class UserService {
    private Connection conn;
    private UserDAO userDAO = new UserDAO();
    private MediaDAO mediaDAO = new MediaDAO();
    private BorrowingDAO borrowingDAO = new BorrowingDAO();
    private FineDAO fineDAO = new FineDAO();
    private User loggedUser;

    /**
     * Opens a database connection for user operations.
     *
     * @throws Exception if the connection fails
     */
    public UserService() throws Exception {
        this.conn = DatabaseConnection.connect();
    }

    /**
     * Logs in a user with username and password hash.
     *
     * @param username login name
     * @param passwordHash password hash to check
     * @return true when valid
     * @throws Exception if reading user data fails
     */
    public boolean login(String username, String passwordHash) throws Exception {
        User user = userDAO.findByUsername(conn, username);
        if (user != null && user.getPasswordHash().equals(passwordHash)) {
            loggedUser = user;
            return true;
        }
        return false;
    }

    /**
     * Logs out and closes the shared connection.
     *
     * @throws SQLException if closing fails
     */
    public void logout() throws SQLException {
        loggedUser = null;
        DatabaseConnection.disconnect();
    }

    /**
     * Searches for media records.
     *
     * @param keyword text to match
     * @param type media type or "media" for all
     * @return matching media list
     * @throws Exception if data access fails
     */
    public List<Media> searchMedia(String keyword, String type) throws Exception {
        return mediaDAO.searchMedia(conn, keyword, type);
    }

    /**
     * Borrows a media item for the logged-in user.
     * Borrowing is allowed only if the user has no unpaid balance
     * and no overdue media.
     *
     * @param mediaId id of the media to borrow
     * @return true if borrowed
     * @throws Exception if the user is not logged in or borrowing fails
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
        if (borrowingDAO.hasOverdueForUser(conn, loggedUser.getUserId())) {
            System.out.println("You have overdue borrowed items. Cannot borrow new media.");
            return false;
        }

        return borrowingDAO.borrowMedia(conn, loggedUser.getUserId(), mediaId);
    }

    /**
     * Returns a borrowed media item for the logged-in user.
     *
     * @param mediaId id of media to return
     * @return true if returned
     * @throws Exception if not logged in or return fails
     */
    public boolean returnMedia(int mediaId) throws Exception {
        if (loggedUser == null)
            throw new IllegalStateException("User not logged in.");

        return borrowingDAO.returnMedia(conn, loggedUser.getUserId(), mediaId);
    }

    /**
     * Pays a fine partly or fully.
     *
     * @param fineId id of the fine
     * @param amount amount to pay
     * @return true if applied
     * @throws Exception if not logged in or payment fails
     */
    public boolean payFine(int fineId, double amount) throws Exception {
        if (loggedUser == null)
            throw new IllegalStateException("User not logged in.");

        return fineDAO.payFine(conn, fineId, loggedUser.getUserId(), amount);
    }

    /**
     * Returns the logged-in user.
     *
     * @return user or null
     */
    public User getLoggedUser() {
        return loggedUser;
    }

    /**
     * Returns the connection used by this service.
     *
     * @return SQL connection
     */
    public Connection getUserConnection() {
        return conn;
    }

    /**
     * Gets borrowings for a specific user.
     *
     * @param userId user id
     * @return list of borrowings
     * @throws Exception if not logged in or reading fails
     */
    public List<Borrowing> findBorrowings(int userId) throws Exception {
        if (loggedUser == null)
            throw new IllegalStateException("User not logged in.");
        return borrowingDAO.findBorrowings(conn, userId);
    }

    /**
     * Gets fines for a specific user.
     *
     * @param userId user id
     * @return list of fines
     * @throws Exception if not logged in or reading fails
     */
    public List<Fine> findFines(int userId) throws Exception {
        if (loggedUser == null)
            throw new IllegalStateException("User not logged in.");
        return fineDAO.findFines(conn, userId);
    }

    /**
     * Builds a mixed-media fine summary for the logged-in user (US5.3).
     *
     * @return fine summary
     * @throws Exception if not logged in or reading fails
     */
    public FineSummary getFineSummary() throws Exception {
        if (loggedUser == null) throw new IllegalStateException("User not logged in.");
        FineReportService report = new FineReportService(conn, fineDAO, borrowingDAO, mediaDAO);
        return report.buildFineSummaryForUser(loggedUser.getUserId());
    }

    /**
     * Sets the logged-in user (testing use only).
     *
     * @param user user to set
     */
    protected void setLoggedUser(User user) { this.loggedUser = user; }

    /**
     * Replaces the user DAO (testing use only).
     *
     * @param dao override value
     */
    protected void setUserDAO(UserDAO dao) { this.userDAO = dao; }

    /**
     * Replaces the media DAO (testing use only).
     *
     * @param dao override value
     */
    protected void setMediaDAO(MediaDAO dao) { this.mediaDAO = dao; }

    /**
     * Replaces the borrowing DAO (testing use only).
     *
     * @param dao override value
     */
    protected void setBorrowingDAO(BorrowingDAO dao) { this.borrowingDAO = dao; }

    /**
     * Replaces the fine DAO (testing use only).
     *
     * @param dao override value
     */
    protected void setFineDAO(FineDAO dao) { this.fineDAO = dao; }

    /**
     * Replaces the SQL connection (testing use only).
     *
     * @param c connection to set
     */
    protected void setConnection(Connection c) { this.conn = c; }

}
