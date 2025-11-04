package com.library.service;

import com.library.dao.*;
import com.library.model.*;
import com.library.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Service for administrator workflows.
 * Provides operations to manage media and users and to trigger overdue reminders (US3.1).
 * This class keeps a database connection open for its operations.
 */
public class AdminService {

    private final Connection conn;
    private final UserDAO userDAO = new UserDAO();
    private final MediaDAO mediaDAO = new MediaDAO();
    private User loggedAdmin;
    BorrowingDAO borrowingDAO = new BorrowingDAO();

    /**
     * Initializes a new AdminService with a database connection.
     *
     * @throws Exception if a database connection cannot be open
     */
    public AdminService() throws Exception {
        this.conn = DatabaseConnection.connect();
    }

    /**
     * Logs in an administrator with a given username and password hash.
     *
     * @param username     the admin’s username
     * @param passwordHash the hashed password
     * @return true if login was successful
     * @throws Exception if a database error occurs
     */
    public boolean login(String username, String passwordHash) throws Exception {
        User admin = userDAO.findByUsername(conn, username);
        if (admin != null && admin.getPasswordHash().equals(passwordHash)) {
            loggedAdmin = admin;
            return true;
        }
        return false;
    }

    /**
     * Logs out the currently logged-in administrator and disconnects from the database.
     *
     * @throws SQLException if an error occurs while closing the connection
     */
    public void logout() throws SQLException {
        loggedAdmin = null;
        DatabaseConnection.disconnect();
    }

    /**
     * Checks if an admin is currently logged in.
     *
     * @return true if an admin is logged in
     */
    public boolean isLoggedIn() {
        return loggedAdmin != null;
    }

    /**
     * Adds a new media item (Book, CD, or Journal) to the library database.
     *
     * @param media the media object to add
     * @return true if the operation was successful
     * @throws Exception if an error occurs or admin is not logged in
     */
    public boolean addMedia(Media media) throws Exception {
        if (loggedAdmin == null)
            throw new IllegalStateException("Admin not logged in");
        return mediaDAO.addMedia(conn, media);
    }

    /**
     * Searches for media records by keyword and type.
     *
     * @param keyword the keyword to search in title, author, or ISBN
     * @param type    media type filter ("book", "cd", "journal", or "media" for all)
     * @return a list of matching media items
     * @throws Exception if a database error occurs
     */
    public List<Media> searchMedia(String keyword, String type) throws Exception {
        return mediaDAO.searchMedia(conn, keyword, type);
    }

    /**
     * Removes a media item if it is available.
     *
     * @param mediaId the ID of the media item to remove
     * @return true if the media was removed, false if it is borrowed
     * @throws Exception if a database error occurs or admin not logged in
     */
    public boolean removeMedia(int mediaId) throws Exception {
        if (loggedAdmin == null)
            throw new IllegalStateException("Admin not logged in");
        if (!mediaDAO.mediaAvailable(conn, mediaId)) {
            System.out.println("Media is currently borrowed.");
            return false;
        }
        return mediaDAO.removeMedia(conn, mediaId);
    }

    /**
     * Lists all media items of a given type.
     *
     * @param type media type filter ("book", "cd", "journal", or "media" for all)
     * @return list of media items
     * @throws Exception if a database error occurs
     */
    public List<Media> listAllMedia(String type) throws Exception {
        return mediaDAO.listAllMedia(conn, type);
    }

    /**
     * Adds a new user to the system.
     *
     * @param username     username of the new user
     * @param email        email address
     * @param passwordHash hashed password
     * @param role         user role ("admin", "librarian", "user")
     * @return true if successfully added
     * @throws Exception if admin not logged in or a database error occurs
     */
    public boolean addUser(String username, String email, String passwordHash, String role) throws Exception {
        if (loggedAdmin == null)
            throw new IllegalStateException("Admin not logged in");

        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setPasswordHash(passwordHash);
        u.setRole(role);
        u.setBalance(0.0);

        return userDAO.addUser(conn, u);
    }

    /**
     * Deletes a user account by ID.
     *
     * @param userId ID of the user to remove
     * @return true if the user was successfully deleted
     * @throws Exception if admin not logged in or a database error occurs
     */
    public boolean removeUser(int userId) throws Exception {
        if (loggedAdmin == null)
            throw new IllegalStateException("Admin not logged in");

        if (userDAO.getUserBalance(conn, userId) > 0) {
            System.out.println("User has unpaid balance.");
            return false;
        }

        List<Media> medialist = mediaDAO.findActiveMedia(conn, userId);
        if (medialist != null && !medialist.isEmpty()) {
            for (Media m : medialist) {
                mediaDAO.setMediaStatus(conn, m.getId(), true);
            }
        }

        return userDAO.deleteUser(conn, userId);
    }

    /**
     * Returns a list of all users in the system.
     *
     * @return list of user objects
     * @throws Exception if admin not logged in or a database error occurs
     */
    public List<User> listUsers() throws Exception {
        if (loggedAdmin == null)
            throw new IllegalStateException("Admin not logged in");
        return userDAO.getAllUsers(conn);
    }

    /**
     * US3.1 — Sends overdue reminders using credentials from .env.
     * This is called by the Admin menu action "Send Overdue Reminders".
     *
     * @return number of distinct users who were notified
     * @throws Exception on DB or email errors
     */
    public int sendOverdueRemindersFromEnv() throws Exception {
        EmailServer emailServer = new DotenvEmailServer();
        EmailNotifier notifier = new EmailNotifier(emailServer);
        ReminderService reminder = new ReminderService(conn, borrowingDAO, userDAO, notifier);
        return reminder.sendOverdueReminders().size();
    }

    /**
     * Sends overdue reminders using a provided email server.
     * This helper is intended for unit tests with a mock email server.
     *
     * @param emailServer server used to send emails
     * @return number of distinct users who were notified
     * @throws Exception on DB or email errors
     */
    public int sendOverdueReminders(EmailServer emailServer) throws Exception {
        EmailNotifier notifier = new EmailNotifier(emailServer);
        ReminderService reminder = new ReminderService(conn, borrowingDAO, userDAO, notifier);
        return reminder.sendOverdueReminders().size();
    }
}
