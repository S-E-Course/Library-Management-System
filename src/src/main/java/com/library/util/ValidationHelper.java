package com.library.util;

/**
 * Basic validation helpers for checking user roles and email formats.
 */
public class ValidationHelper {

    /** Allowed user roles. */
    private static final String[] VALID_ROLES = { "admin", "librarian", "user" };

    /**
     * Returns true if the given role matches one of the allowed roles.
     *
     * @param role role name to check
     * @return true if valid, false otherwise
     */
    public static boolean isValidRole(String role) {
        if (role == null) return false;
        for (String valid : VALID_ROLES) {
            if (valid.equals(role)) return true;
        }
        return false;
    }

    /**
     * Returns true if the email matches a basic email pattern.
     *
     * @param email email address to check
     * @return true if the format is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }
}
