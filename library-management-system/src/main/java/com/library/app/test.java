package com.library.app;
import java.util.List;

import com.library.model.Book;
import com.library.model.User;
import com.library.service.AdminService;
import com.library.service.UserService;

public class test {

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
		
		
		
		
		
		
		
		
		
		
		
		
		String title = "how to know";
		String author = "MO Sam";
		String isbn = "1403529763";
		//a.addBook(title, author, isbn);
		
	    List<Book> list;
	    list = a.searchBooks(isbn);
	    System.out.println(list.get(0).getAuthor() + " " +  list.get(0).getTitle());
	    
	    
	    
	    
	    
	    
	    
	    
		username = "user";
		String email = "user@gmail.com";
		String passwordHash = "123";
		String role = "user";
	    //a.addUser(username, email, passwordHash, role);
	    
	    
		
		
		
		
	    

	    username = "user";
		password = "12";
		if(u.login(username, password)) {
			System.out.println("User Logged In");
			
			
			
			
			
			User user;
			user = u.getLoggedUser();
			System.out.println(user.getUsername() + " " + user.getEmail());
			
			
			
			
			
			
			
			
			list = u.searchBooks(author);
		    System.out.println(list.get(0).getAuthor() + " " +  list.get(0).getTitle());
		}
		
		
		
		
		
		
		
		

	    
	    
	    
	    
	    
	    
		
		a.logout();
		
		if(!a.isLoggedIn()) {
			System.out.println("Admin Logged Out");
		}
	}

}
