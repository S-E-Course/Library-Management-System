package com.library.util;


public class ValidationHelper {
	private static final String[] VALID_ROLES = { "admin", "librarian", "user" };

	
	
	public static boolean isValidRole(String role) {
	    if (role == null) return false;
	    for (String valid : VALID_ROLES) {
	        if (valid.equals(role)) return true;
	    }
	    return false;
	}

    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }
}
