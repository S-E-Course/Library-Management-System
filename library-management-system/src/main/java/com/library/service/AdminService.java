package com.library.service;

import com.library.dao.UserDAO;
import com.library.dao.BookDAO;
import com.library.model.Book;
import com.library.model.User;
import com.library.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class AdminService {
	Connection conn;
    private final UserDAO userDAO = new UserDAO();
    private final BookDAO bookDAO = new BookDAO();
    private User loggedAdmin;
    
    
    public AdminService() throws Exception {
        this.conn = DatabaseConnection.connect();
    }

    // US1.1 - Login
    public boolean login(String username, String passwordHash) throws Exception {
        User admin = userDAO.findByUsername(conn, username);
        if (admin != null && admin.getPasswordHash().equals(passwordHash)
                && admin.getRole().equals("admin")) {
            loggedAdmin = admin;
            return true;
        }
        return false;
    }

    // US1.2 - Logout
    public void logout() throws SQLException {
        loggedAdmin = null;
        DatabaseConnection.disconnect();
    }

    public boolean isLoggedIn() {
        return loggedAdmin != null;
    }

    // US1.3 - Add Book
    public boolean addBook(String title, String author, String isbn) throws Exception {
        if (loggedAdmin == null) throw new IllegalStateException("Admin not logged in");
        Book b = new Book();
        b.setTitle(title);
        b.setAuthor(author);
        b.setIsbn(isbn);
        return bookDAO.addBook(conn, b);
    }

    // US1.4 - Search Book
    public List<Book> searchBooks(String keyword) throws Exception {
        return bookDAO.searchBooks(conn, keyword);
    }

    // Add user
    public boolean addUser(String username, String email, String passwordHash, String role) throws Exception {
        if (loggedAdmin == null) throw new IllegalStateException("Admin not logged in");
        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setPasswordHash(passwordHash);
        u.setRole(role);
        u.setBalance(0.0);
        return userDAO.addUser(conn, u);
    }

	public boolean removeBook(int bookId) throws Exception {
		if (loggedAdmin == null) throw new IllegalStateException("Admin not logged in");
		if(!bookDAO.bookAvailable(conn, bookId)){
			System.out.println("Book is currently borrowed.");
			return false;
		}
		return bookDAO.removeBook(conn, bookId);
	}

	public List<Book> listBooks() throws Exception {
        return bookDAO.listAllBooks(conn);
	}

	public boolean removeUser(int userId) throws Exception {
        return userDAO.deleteUser(conn, userId);
    }

	public List<User> listUsers() throws Exception {
        return userDAO.getAllUsers(conn);
    
	}
}
