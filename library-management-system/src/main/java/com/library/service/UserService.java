package com.library.service;

import com.library.dao.*;
import com.library.model.*;
import com.library.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Service for end-user workflows: login, search, borrow and return media,
 * paying fines, and generating the US5.3 mixed-media fine summary.
 */
public class UserService {
    private Connection conn;
    private UserDAO userDAO = new UserDAO();
    private MediaDAO mediaDAO = new MediaDAO();
    private BorrowingDAO borrowingDAO = new BorrowingDAO();
    private FineDAO fineDAO = new FineDAO();
    private User loggedUser;

    /**
     * Establishes a database connection for user operations.
     *
     * @throws Exception if connecting to the database fails
     */
    public UserService() throws Exception {
        this.conn = DatabaseConnection.connect();
    }

    /**
     * Logs in a user using username and password hash.
     *
     * @param username     account username
     * @param passwordHash password hash to compare
     * @return true if authenticated
     * @throws Exception if a data access error occurs
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
     * Logs out the current user and closes the shared database connection.
     *
     * @throws SQLException if disconnect fails
     */
    public void logout() throws SQLException {
        loggedUser = null;
        DatabaseConnection.disconnect();
    }

    /**
     * Searches for media (books, CDs, journals).
     *
     * @param keyword text to search (title, author, ISBN)
     * @param type    "book", "cd", "journal", or "media" for all
     * @return list of results matching the query
     * @throws Exception if a data access error occurs
     */
    public List<Media> searchMedia(String keyword, String type) throws Exception {
        return mediaDAO.searchMedia(conn, keyword, type);
    }

    /**
     * Borrows media for the logged-in user if:
     * the user is logged in, the media is available, and the user has no unpaid balance.
     *
     * @param mediaId media identifier to borrow
     * @return true if the borrow operation succeeds
     * @throws Exception if preconditions fail or a data access error occurs
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
     * Returns a borrowed media item for the logged-in user.
     *
     * @param mediaId media identifier to return
     * @return true if the return succeeds
     * @throws Exception if not logged in or on data access error
     */
    public boolean returnMedia(int mediaId) throws Exception {
        if (loggedUser == null)
            throw new IllegalStateException("User not logged in.");

        return borrowingDAO.returnMedia(conn, loggedUser.getUserId(), mediaId);
    }

    /**
     * Pays fines partially or fully and updates the user's balance.
     *
     * @param fineId fine identifier to pay
     * @param amount amount to pay
     * @return true if the payment is applied
     * @throws Exception if not logged in or on data access error
     */
    public boolean payFine(int fineId, double amount) throws Exception {
        if (loggedUser == null)
            throw new IllegalStateException("User not logged in.");

        return fineDAO.payFine(conn, fineId, loggedUser.getUserId(), amount);
    }

    /**
     * Returns the currently logged-in user or null if not authenticated.
     *
     * @return current user or null
     */
    public User getLoggedUser() {
        return loggedUser;
    }

    /**
     * Exposes the underlying connection used by this service.
     * Intended for read-only display helpers.
     *
     * @return connection in use
     */
    public Connection getUserConnection() {
        return conn;
    }

    /**
     * Returns borrowings for the given user id.
     *
     * @param userId user to query
     * @return list of borrowings
     * @throws Exception if not logged in or on data access error
     */
    public List<Borrowing> findBorrowings(int userId) throws Exception {
        if (loggedUser == null)
            throw new IllegalStateException("User not logged in.");
        return borrowingDAO.findBorrowings(conn, userId);
    }

    /**
     * Returns fines for the given user id.
     *
     * @param userId user to query
     * @return list of fines
     * @throws Exception if not logged in or on data access error
     */
    public List<Fine> findFines(int userId) throws Exception {
        if (loggedUser == null)
            throw new IllegalStateException("User not logged in.");
        return fineDAO.findFines(conn, userId);
    }

    /**
     * US5.3 — Returns a mixed-media fine summary for the currently logged-in user.
     * Aggregates unpaid fines per media type (e.g., book, cd, journal) and total.
     *
     * @return a {@link FineSummary} for the logged-in user
     * @throws Exception if not logged in or on database error
     */
    public FineSummary getFineSummary() throws Exception {
        if (loggedUser == null) throw new IllegalStateException("User not logged in.");
        FineReportService report = new FineReportService(conn, fineDAO, borrowingDAO, mediaDAO);
        return report.buildFineSummaryForUser(loggedUser.getUserId());
    }

    /**
     * Sets the current logged-in user.
     * Intended for tests.
     *
     * @param user user to set as logged in
     */
    protected void setLoggedUser(User user) { this.loggedUser = user; }

    /**
     * Overrides the user DAO dependency.
     * Intended for tests.
     *
     * @param dao user DAO
     */
    protected void setUserDAO(UserDAO dao) { this.userDAO = dao; }

    /**
     * Overrides the media DAO dependency.
     * Intended for tests.
     *
     * @param dao media DAO
     */
    protected void setMediaDAO(MediaDAO dao) { this.mediaDAO = dao; }

    /**
     * Overrides the borrowing DAO dependency.
     * Intended for tests.
     *
     * @param dao borrowing DAO
     */
    protected void setBorrowingDAO(BorrowingDAO dao) { this.borrowingDAO = dao; }

    /**
     * Overrides the fine DAO dependency.
     * Intended for tests.
     *
     * @param dao fine DAO
     */
    protected void setFineDAO(FineDAO dao) { this.fineDAO = dao; }

    /**
     * Overrides the SQL connection.
     * Intended for tests.
     *
     * @param c connection to set
     */
    protected void setConnection(Connection c) { this.conn = c; }

}
