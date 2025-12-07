package com.library.service;

import java.sql.Connection;

import com.library.dao.UserDAO;
import com.library.model.User;
import com.library.util.DatabaseConnection;

/**
 * Service that checks user login credentials.
 * Looks up a user by username and compares the stored password hash.
 */
public class AuthService {
    private final UserDAO userDAO = new UserDAO();

    /**
     * Verifies a username and password hash.
     *
     * @param username username to check
     * @param passwordHash password hash to match
     * @return the user if credentials match, or null if not valid
     * @throws Exception if a database error occurs
     */
    public User authenticate(String username, String passwordHash) throws Exception {
        try (Connection conn = DatabaseConnection.connect()) {
            User u = userDAO.findByUsername(conn, username);
            if (u == null) return null;
            if (u.getPasswordHash() == null) return null;
            return u.getPasswordHash().equals(passwordHash) ? u : null;
        }
    }
}
