package com.library.app;
import com.library.dao.UserDAO;
import com.library.model.User;

public class test {

	public static void main(String[] args) throws Exception {
		
		UserDAO ud = new UserDAO();
		User u = new User();
		u = ud.findByUsername("mosub");
		System.out.print(u.getEmail());
		
	}

}
