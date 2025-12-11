package com.library.service;

import com.library.dao.UserDAO;
import com.library.model.User;
import com.library.util.DatabaseConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for AuthService
 */
class AuthServiceTest {

    private AuthService authService;
    private UserDAO userDAO;
    private Connection conn;

    /**
     * Prepares an AuthService with a mocked connection and DAO.
     *
     * @throws Exception if reflection fails
     */
    @BeforeEach
    void setUp() throws Exception {
        conn = mock(Connection.class);
        DatabaseConnection.setMockConnection(conn);

        authService = new AuthService();

        userDAO = mock(UserDAO.class);
        Field f = AuthService.class.getDeclaredField("userDAO");
        f.setAccessible(true);
        f.set(authService, userDAO);
    }

    /**
     * When no user is found, authenticate should return null.
     *
     * @throws Exception if the call fails
     */
    @Test
    void authenticateReturnsNullWhenUserNotFound() throws Exception {
        when(userDAO.findByUsername(conn, "u")).thenReturn(null);

        User result = authService.authenticate("u", "123");

        assertNull(result);
    }

    /**
     * When the stored password hash is null, authenticate should return null.
     *
     * @throws Exception if the call fails
     */
    @Test
    void authenticateReturnsNullWhenPasswordHashIsNull() throws Exception {
        User u = new User();
        u.setUsername("u");
        u.setPasswordHash(null);

        when(userDAO.findByUsername(conn, "u")).thenReturn(u);

        User result = authService.authenticate("u", "123");

        assertNull(result);
    }

    /**
     * When the password hash matches, authenticate should return the user.
     *
     * @throws Exception if the call fails
     */
    @Test
    void authenticateReturnsUserWhenPasswordMatches() throws Exception {
        User u = new User();
        u.setUsername("u");
        u.setPasswordHash("123");

        when(userDAO.findByUsername(conn, "u")).thenReturn(u);

        User result = authService.authenticate("u", "123");

        assertNotNull(result);
        assertEquals("u", result.getUsername());
    }

    /**
     * When the password hash does not match, authenticate should return null.
     *
     * @throws Exception if the call fails
     */
    @Test
    void authenticateReturnsNullWhenPasswordDoesNotMatch() throws Exception {
        User u = new User();
        u.setUsername("u");
        u.setPasswordHash("123");

        when(userDAO.findByUsername(conn, "u")).thenReturn(u);

        User result = authService.authenticate("u", "wrong");

        assertNull(result);
    }
}
