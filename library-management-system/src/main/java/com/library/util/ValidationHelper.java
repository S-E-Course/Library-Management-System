package com.library.util;

/**
 * Provides helper methods for validating roles and email formats.
 * 
 * follow the correct format before being stored or processed in the system.
 */
public class ValidationHelper {

    /** List of allowed user roles. */
    private static final String[] VALID_ROLES = { "admin", "librarian", "user" };

    /**
     * Checks if the given role is valid.
     * 
     * @param role the role string to validate
     * @return true if the role is one of the predefined valid roles, false otherwise
     */
    public static boolean isValidRole(String role) {
        if (role == null) return false;
        for (String valid : VALID_ROLES) {
            if (valid.equals(role)) return true;
        }
        return false;
    }

    /**
     * Checks if the given email follows a valid email format.
     * 
     * @param email the email address to validate
     * @return true if the email format is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }
}
