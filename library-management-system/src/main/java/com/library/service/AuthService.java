package com.library.service;

import java.sql.Connection;

import com.library.dao.UserDAO;
import com.library.model.User;
import com.library.util.DatabaseConnection;

/**
 * Service for authenticating users by username and password hash.
 * Returns the matching user when credentials are valid; otherwise returns null.
 */
public class AuthService {
    private final UserDAO userDAO = new UserDAO();

    /**
     * Authenticates a user by username and password hash.
     * Password matching is a direct equals check to the stored value.
     *
     * @param username     username
     * @param passwordHash password hash to compare with the stored value
     * @return user when valid, otherwise null
     * @throws Exception if a data access error occurs
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
