package com.library.service;

import com.library.dao.*;
import com.library.model.*;
import com.library.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Service for administrator tasks.
 * Handles media management, user management, and overdue reminder actions.
 */
public class AdminService {

    private final Connection conn;
    private final UserDAO userDAO = new UserDAO();
    private final MediaDAO mediaDAO = new MediaDAO();
    private User loggedAdmin;
    BorrowingDAO borrowingDAO = new BorrowingDAO();

    /**
     * Creates a new AdminService and opens a database connection.
     *
     * @throws Exception if the connection cannot be opened
     */
    public AdminService() throws Exception {
        this.conn = DatabaseConnection.connect();
    }

    /**
     * Logs in an administrator.
     *
     * @param username admin username
     * @param passwordHash stored password value
     * @return true if login succeeds
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
     * Logs out the administrator and closes the connection.
     *
     * @throws SQLException if disconnect fails
     */
    public void logout() throws SQLException {
        loggedAdmin = null;
        DatabaseConnection.disconnect();
    }

    /**
     * Checks if an admin is logged in.
     *
     * @return true if an admin is logged in
     */
    public boolean isLoggedIn() {
        return loggedAdmin != null;
    }

    /**
     * Adds a media item.
     *
     * @param media media object
     * @return true if added
     * @throws Exception if not logged in or a database error occurs
     */
    public boolean addMedia(Media media) throws Exception {
        if (loggedAdmin == null)
            throw new IllegalStateException("Admin not logged in");
        return mediaDAO.addMedia(conn, media);
    }

    /**
     * Searches media by keyword and type.
     *
     * @param keyword search text
     * @param type media type or media for all
     * @return list of matching media
     * @throws Exception if a database error occurs
     */
    public List<Media> searchMedia(String keyword, String type) throws Exception {
        return mediaDAO.searchMedia(conn, keyword, type);
    }

    /**
     * Removes a media item if it is available.
     *
     * @param mediaId media identifier
     * @return true if removed
     * @throws Exception if not logged in or a database error occurs
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
     * Lists all media filtered by type.
     *
     * @param type media type or media for all
     * @return list of media
     * @throws Exception if a database error occurs
     */
    public List<Media> listAllMedia(String type) throws Exception {
        return mediaDAO.listAllMedia(conn, type);
    }

    /**
     * Adds a user.
     *
     * @param username username
     * @param email email
     * @param passwordHash stored password
     * @param role user role
     * @return true if added
     * @throws Exception if not logged in or a database error occurs
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
     * Deletes a user if no balance or active media exist.
     *
     * @param userId user identifier
     * @return true if deleted
     * @throws Exception if not logged in or a database error occurs
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
     * Lists all users.
     *
     * @return list of users
     * @throws Exception if not logged in or a database error occurs
     */
    public List<User> listUsers() throws Exception {
        if (loggedAdmin == null)
            throw new IllegalStateException("Admin not logged in");
        return userDAO.getAllUsers(conn);
    }

    /**
     * Sends overdue reminders using .env settings.
     *
     * @return number of users notified
     * @throws Exception if sending fails
     */
    public int sendOverdueRemindersFromEnv() throws Exception {
        EmailServer emailServer = new DotenvEmailServer();
        EmailNotifier notifier = new EmailNotifier(emailServer);
        ReminderService reminder = new ReminderService(conn, borrowingDAO, userDAO, notifier);
        return reminder.sendOverdueReminders().size();
    }

    /**
     * Sends overdue reminders using a given email server.
     *
     * @param emailServer email server to use
     * @return number of users notified
     * @throws Exception if sending fails
     */
    public int sendOverdueReminders(EmailServer emailServer) throws Exception {
        EmailNotifier notifier = new EmailNotifier(emailServer);
        ReminderService reminder = new ReminderService(conn, borrowingDAO, userDAO, notifier);
        return reminder.sendOverdueReminders().size();
    }
}
