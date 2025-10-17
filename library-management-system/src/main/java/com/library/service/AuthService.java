
package com.library.service;

import com.library.dao.UserDAO;
import com.library.model.User;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();

    public User authenticate(String username, String passwordHash) throws Exception {
        User u = userDAO.findByUsername(username);
        if (u == null) return null;
        if (u.getPasswordHash() == null) return null;
        return u.getPasswordHash().equals(passwordHash) ? u : null;
    }
}
