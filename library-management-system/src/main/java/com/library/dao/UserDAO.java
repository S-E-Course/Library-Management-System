package com.library.dao;

import com.library.model.User;
import com.library.util.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class UserDAO {

    public User findByUsername(String username) throws Exception {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.connect();      //try do Automatic resource management - closes connections even if exceptions occur
        														  //so i don't need Manual close connections
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();     //Execute SELECT queries
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

    public boolean addUser(User user) throws Exception {
        String sql = "INSERT INTO users (username, email, password_hash, role, balance) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getRole());
            stmt.setDouble(5, user.getBalance());
            return stmt.executeUpdate() > 0;     //Execute INSERT/UPDATE/DELETE
            									 //Gets the number of rows affected (0 if no rows inserted, 1 if successful)
        }
    }
    
    public List<User> getAllUsers() throws Exception {
        String sql = "SELECT * FROM users ORDER BY user_id";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
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
    
	private void updateUserBalance(int userId, double amountToAdd) throws Exception {
        String sql = "UPDATE users SET balance = balance + ? WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, amountToAdd);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }
    
    public double getUserBalance(int userId) throws Exception {
        String sql = "SELECT balance FROM users WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("balance");
            }
        }
        throw new Exception("user not found");
    }


}
