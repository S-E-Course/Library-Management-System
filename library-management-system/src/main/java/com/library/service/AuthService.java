
package com.library.service;

import java.sql.Connection;

import com.library.dao.UserDAO;
import com.library.model.User;
import com.library.util.DatabaseConnection;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();

    public User authenticate(String username, String passwordHash) throws Exception {
    	try (Connection conn = DatabaseConnection.connect()) {
	        User u = userDAO.findByUsername(conn, username);
	        if (u == null) return null;
	        if (u.getPasswordHash() == null) return null;
	        return u.getPasswordHash().equals(passwordHash) ? u : null;
    	}
    }
}
