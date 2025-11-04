package com.library.model;

/**
 * User account entity.
 * Includes authentication fields, role, and an account balance used for fines.
 *
 * @author
 * @version 1.0
 */
public class User {
    private int userId;
    private String username;
    private String email;
    private String passwordHash;
    private String role;    // admin, librarian, user
    private double balance;

    /** @return user identifier */
    public int getUserId() { return userId; }

    /** @param userId user identifier */
    public void setUserId(int userId) { this.userId = userId; }

    /** @return username */
    public String getUsername() { return username; }

    /** @param username username */
    public void setUsername(String username) { this.username = username; }

    /** @return email address */
    public String getEmail() { return email; }

    /** @param email email address */
    public void setEmail(String email) { this.email = email; }

    /** @return password hash or plain value as stored */
    public String getPasswordHash() { return passwordHash; }

    /** @param passwordHash password hash or plain value as stored */
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    /** @return role string */
    public String getRole() { return role; }

    /** @param role role string */
    public void setRole(String role) { this.role = role; }

    /** @return current account balance */
    public double getBalance() { return balance; }

    /** @param balance current account balance */
    public void setBalance(double balance) { this.balance = balance; }
}
