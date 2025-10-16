package com.library.app;
import java.util.List;

import com.library.model.Book;
import com.library.model.User;
import com.library.service.AdminService;
import com.library.service.UserService;

public class Main {

	public static void main(String[] args) throws Exception {
		
		AdminService a = new AdminService();
	    UserService u = new UserService();
		String username = "mosub";
		String password = "12345";
		
		
		boolean b = a.login(username, password);
		
		
		
		if(a.isLoggedIn()) {
			System.out.println("Admin Logged In");
			u = null;
		}
		else if(u.login(username, password)) {
			System.out.println("User Logged In");
			a = null;
		}
		
		
	}
}