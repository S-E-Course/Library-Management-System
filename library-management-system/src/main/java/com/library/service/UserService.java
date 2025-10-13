package com.library.service;

import com.library.dao.*;
import com.library.model.*;
import java.util.List;

public class UserService {
    private final UserDAO userDAO = new UserDAO();
    private final BookDAO bookDAO = new BookDAO();
    private final BorrowingDAO borrowingDAO = new BorrowingDAO();
    private final FineDAO fineDAO = new FineDAO();
    private User loggedUser;

    public boolean login(String username, String passwordHash) throws Exception {
        User user = userDAO.findByUsername(username);
        if (user != null && user.getPasswordHash().equals(passwordHash)) {
            loggedUser = user;
            return true;
        }
        return false;
    }

    public void logout() {
        loggedUser = null;
    }

    public List<Book> searchBooks(String keyword) throws Exception {
        return bookDAO.searchBooks(keyword);
    }

    public boolean borrowBook(int bookId) throws Exception {
        if (loggedUser == null) throw new IllegalStateException("User not logged in");
        return borrowingDAO.borrowBook(loggedUser.getUserId(), bookId);
    }
    
    public boolean returnBook(int bookId) throws Exception {
        if (loggedUser == null) throw new IllegalStateException("User not logged in");
        return borrowingDAO.returnBook(loggedUser.getUserId(), bookId);
    }

    public boolean payFine(int fineId, double amount) throws Exception {
        if (loggedUser == null) throw new IllegalStateException("User not logged in");
        return fineDAO.payFine(fineId, loggedUser.getUserId(), amount);
    }

    public User getLoggedUser() {
        return loggedUser;
    }
}
