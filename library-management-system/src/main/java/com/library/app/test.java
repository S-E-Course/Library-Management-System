package com.library.app;
import java.util.List;

import com.library.model.Book;
import com.library.model.User;
import com.library.service.AdminService;
import com.library.service.UserService;

public class test {

	public static void main(String[] args) throws Exception {
		
		AdminService a = new AdminService();
		String username = "mosub";
		String password = "12345";
		boolean b = a.login(username, password);
		if(b) {
			System.out.println("sucsses");
		}
		else {
			System.out.println("failed");
		}
		
		
		
		
		
		
		
		if(a.isLoggedIn()) {
			System.out.println("Admin Logged In");
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
	    
	    
		
		
		
		
	    
	    UserService u = new UserService();
	    username = "user";
		password = "123";
		if(u.login(username, passwordHash)) {
			System.out.println("User Logged In");
		}
		
		
		
		
		
		
		
		
		//u.logout();
		User user;
		user = u.getLoggedUser();
		System.out.println(user.getUsername() + " " + user.getEmail());
		
		
		
		
		
		
		
		
		list = u.searchBooks(author);
	    System.out.println(list.get(0).getAuthor() + " " +  list.get(0).getTitle());
	    
	    
	    
	    
	    
	    
		
		a.logout();
		
		if(!a.isLoggedIn()) {
			System.out.println("Admin Logged Out");
		}
	}

}
