package com.library.dao;

import com.library.model.User;

import java.sql.*;
import java.util.*;

/**
 * DAO for user operations such as finding, adding, listing, updating balance, and deleting users.
 */
public class UserDAO {

    /**
     * Finds a user by username.
     *
     * @param conn active database connection
     * @param username username to search
     * @return matching user or null if not found
     * @throws Exception if a database error occurs
     */
    public User findByUsername(Connection conn, String username) throws Exception {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setUsername(rs.getString("username"));
                u.setEmail(rs.getString("email"));
                u.setPasswordHash(rs.getString("password_hash"));
                u.setRole(rs.getString("role"));
                u.setBalance(rs.getDouble("balance"));
                return u;
            }
        }
        return null;
    }

    /**
     * Adds a new user.
     *
     * @param conn active database connection
     * @param user user object to insert
     * @return true if inserted, false if duplicate
     * @throws Exception if a database error occurs
     */
    public boolean addUser(Connection conn, User user) throws Exception {
        String sql = "INSERT INTO users (username, email, password_hash, role, balance) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getRole());
            stmt.setDouble(5, user.getBalance());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                System.out.println("Username or email already exists.");
                return false;
            }
            throw e;
        }
    }

    /**
     * Returns all users ordered by id.
     *
     * @param conn active database connection
     * @return list of users
     * @throws Exception if a database error occurs
     */
    public List<User> getAllUsers(Connection conn) throws Exception {
        String sql = "SELECT * FROM users ORDER BY user_id";
        List<User> users = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setRole(rs.getString("role"));
                user.setBalance(rs.getDouble("balance"));
                users.add(user);
            }
        }
        return users;
    }

    /**
     * Updates a user's balance by adding the given amount.
     *
     * @param conn active database connection
     * @param userId user id
     * @param amount amount to add to balance
     * @throws Exception if a database error occurs
     */
    public void updateUserBalance(Connection conn, int userId, double amount) throws Exception {
        String sql = "UPDATE users SET balance = balance + ? WHERE user_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, amount);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    /**
     * Gets the balance of a user.
     *
     * @param conn active database connection
     * @param userId user id
     * @return current balance
     * @throws Exception if user is not found or a database error occurs
     */
    public double getUserBalance(Connection conn, int userId) throws Exception {
        String sql = "SELECT balance FROM users WHERE user_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("balance");
            }
        }
        throw new Exception("user not found");
    }

    /**
     * Finds a user by id.
     *
     * @param conn active database connection
     * @param userId user id
     * @return user or null if not found
     * @throws Exception if a database error occurs
     */
    public User findById(Connection conn, int userId) throws Exception {
        String sql = "SELECT user_id, username, email, password_hash, role, balance " +
                     "FROM users WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setUserId(rs.getInt("user_id"));
                    u.setUsername(rs.getString("username"));
                    u.setEmail(rs.getString("email"));
                    u.setPasswordHash(rs.getString("password_hash"));
                    u.setRole(rs.getString("role"));
                    u.setBalance(rs.getDouble("balance"));
                    return u;
                }
            }
        }
        return null;
    }

    /**
     * Deletes a user by id.
     *
     * @param conn active database connection
     * @param userId user id
     * @return true if a row was deleted
     * @throws Exception if a database error occurs
     */
    public boolean deleteUser(Connection conn, int userId) throws Exception {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        }
    }
}
