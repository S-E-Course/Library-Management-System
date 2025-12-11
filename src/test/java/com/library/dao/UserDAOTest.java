package com.library.dao;

import com.library.model.User;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for UserDAO using Mockito mocks.
 */
class UserDAOTest {

    /**
     * Tests that a user is returned when the username exists.
     *
     * @throws Exception if the DAO call fails
     */
    @Test
    void findByUsername_returnsUserWhenFound() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);

        when(rs.getInt("user_id")).thenReturn(1);
        when(rs.getString("username")).thenReturn("abdallah");
        when(rs.getString("email")).thenReturn("abdallahalmasri2612@gmail.com");
        when(rs.getString("password_hash")).thenReturn("123");
        when(rs.getString("role")).thenReturn("admin");
        when(rs.getDouble("balance")).thenReturn(10.0);

        UserDAO dao = new UserDAO();
        User u = dao.findByUsername(conn, "abdallah");

        assertNotNull(u);
        assertEquals(1, u.getUserId());
        assertEquals("abdallah", u.getUsername());
        assertEquals("admin", u.getRole());
    }

    /**
     * Tests that null is returned when no user is found.
     *
     * @throws Exception if the DAO call fails
     */
    @Test
    void findByUsername_returnsNullWhenNotFound() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        UserDAO dao = new UserDAO();
        User u = dao.findByUsername(conn, "nosuchuser");

        assertNull(u);
    }

    /**
     * Tests that a new user is inserted successfully.
     *
     * @throws Exception if the DAO call fails
     */
    @Test
    void addUser_insertsSuccessfully() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        User u = new User();
        u.setUsername("abdallah");
        u.setEmail("abdallahalmasri2612@gmail.com");
        u.setPasswordHash("123");
        u.setRole("admin");
        u.setBalance(0);

        UserDAO dao = new UserDAO();
        boolean ok = dao.addUser(conn, u);

        assertTrue(ok);
    }

    /**
     * Tests that addUser returns false when a duplicate exists.
     *
     * @throws Exception if the DAO call fails
     */
    @Test
    void addUser_returnsFalseWhenDuplicate() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenThrow(new SQLException("duplicate", "23505"));

        User u = new User();
        u.setUsername("abdallah");
        u.setEmail("abdallahalmasri2612@gmail.com");
        u.setPasswordHash("123");
        u.setRole("admin");
        u.setBalance(0);

        UserDAO dao = new UserDAO();
        boolean ok = dao.addUser(conn, u);

        assertFalse(ok);
    }

    /**
     * Tests that getAllUsers returns a list with users.
     *
     * @throws Exception if the DAO call fails
     */
    @Test
    void getAllUsers_returnsList() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true, false);

        when(rs.getInt("user_id")).thenReturn(1);
        when(rs.getString("username")).thenReturn("abdallah");
        when(rs.getString("email")).thenReturn("abdallahalmasri2612@gmail.com");
        when(rs.getString("password_hash")).thenReturn("123");
        when(rs.getString("role")).thenReturn("admin");
        when(rs.getDouble("balance")).thenReturn(10.0);

        UserDAO dao = new UserDAO();
        List<User> users = dao.getAllUsers(conn);

        assertEquals(1, users.size());
        assertEquals("abdallah", users.get(0).getUsername());
    }

    /**
     * Tests that the user balance is updated.
     *
     * @throws Exception if the DAO call fails
     */
    @Test
    void updateUserBalance_updatesBalance() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);

        UserDAO dao = new UserDAO();
        dao.updateUserBalance(conn, 1, 20.0);

        verify(ps).setDouble(1, 20.0);
        verify(ps).setInt(2, 1);
        verify(ps).executeUpdate();
    }

    /**
     * Tests that getUserBalance returns a value.
     *
     * @throws Exception if the DAO call fails
     */
    @Test
    void getUserBalance_returnsBalance() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getDouble("balance")).thenReturn(50.0);

        UserDAO dao = new UserDAO();
        double bal = dao.getUserBalance(conn, 1);

        assertEquals(50.0, bal);
    }

    /**
     * Tests that getUserBalance throws an exception when no row is found.
     *
     * @throws Exception if the DAO call fails
     */
    @Test
    void getUserBalance_throwsWhenNotFound() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        UserDAO dao = new UserDAO();

        assertThrows(Exception.class, () -> dao.getUserBalance(conn, 1));
    }

    /**
     * Tests that findById returns a user when found.
     *
     * @throws Exception if the DAO call fails
     */
    @Test
    void findById_returnsUser() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);

        when(rs.getInt("user_id")).thenReturn(1);
        when(rs.getString("username")).thenReturn("abdallah");
        when(rs.getString("email")).thenReturn("abdallahalmasri2612@gmail.com");
        when(rs.getString("password_hash")).thenReturn("123");
        when(rs.getString("role")).thenReturn("admin");
        when(rs.getDouble("balance")).thenReturn(0.0);

        UserDAO dao = new UserDAO();
        User u = dao.findById(conn, 1);

        assertNotNull(u);
        assertEquals("abdallah", u.getUsername());
    }

    /**
     * Tests that findById returns null when missing.
     *
     * @throws Exception if the DAO call fails
     */
    @Test
    void findById_returnsNullWhenMissing() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        UserDAO dao = new UserDAO();
        User u = dao.findById(conn, 99);

        assertNull(u);
    }

    /**
     * Tests that deleteUser returns true when a row is deleted.
     *
     * @throws Exception if the DAO call fails
     */
    @Test
    void deleteUser_deletesSuccessfully() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        UserDAO dao = new UserDAO();
        boolean ok = dao.deleteUser(conn, 1);

        assertTrue(ok);
    }

    /**
     * Tests that deleteUser returns false when no row was deleted.
     *
     * @throws Exception if the DAO call fails
     */
    @Test
    void deleteUser_returnsFalseWhenNoRowDeleted() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(0);

        UserDAO dao = new UserDAO();
        boolean ok = dao.deleteUser(conn, 1);

        assertFalse(ok);
    }

    /**
     * Tests that addUser rethrows an SQLException when the SQLState is not the duplicate-key code.
     *
     * @throws Exception if the DAO call fails
     */
    @Test
    void addUser_rethrowsWhenSqlStateIsNotDuplicate() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenThrow(new SQLException("other error", "99999"));

        User u = new User();
        u.setUsername("other");
        u.setEmail("other@example.com");
        u.setPasswordHash("pw");
        u.setRole("user");
        u.setBalance(0);

        UserDAO dao = new UserDAO();

        assertThrows(SQLException.class, () -> dao.addUser(conn, u));
    }

    /**
     * Extra edge case: getAllUsers returns an empty list when there are no rows.
     *
     * @throws Exception if the DAO call fails
     */
    @Test
    void getAllUsers_returnsEmptyListWhenNoUsers() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        UserDAO dao = new UserDAO();
        List<User> users = dao.getAllUsers(conn);

        assertNotNull(users);
        assertTrue(users.isEmpty());
    }
}
