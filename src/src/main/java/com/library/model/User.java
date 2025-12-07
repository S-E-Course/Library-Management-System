package com.library.model;

/**
 * User account entity.
 * Holds login details, role information, and account balance.
 */
public class User {
    private int userId;
    private String username;
    private String email;
    private String passwordHash;
    private String role;
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

    /** @return stored password hash */
    public String getPasswordHash() { return passwordHash; }

    /** @param passwordHash stored password hash */
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    /** @return role value */
    public String getRole() { return role; }

    /** @param role role value */
    public void setRole(String role) { this.role = role; }

    /** @return account balance */
    public double getBalance() { return balance; }

    /** @param balance account balance */
    public void setBalance(double balance) { this.balance = balance; }
}
