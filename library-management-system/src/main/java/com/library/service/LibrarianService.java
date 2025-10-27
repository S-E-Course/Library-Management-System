
package com.library.service;

import com.library.dao.BorrowingDAO;
import com.library.dao.FineDAO;
import com.library.dao.UserDAO;
import com.library.model.Borrowing;
import com.library.model.User;
import com.library.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class LibrarianService {
	Connection conn;
    private final BorrowingDAO borrowingDAO = new BorrowingDAO();
    private final FineDAO fineDAO = new FineDAO();
    private final UserDAO userDAO = new UserDAO();
    private User loggedLibrarian;
    
    
    public LibrarianService() throws Exception {
        this.conn = DatabaseConnection.connect();
    }
    
    public boolean login(String username, String passwordHash) throws Exception {
        User Librarian = userDAO.findByUsername(conn, username);
        if (Librarian != null && Librarian.getPasswordHash().equals(passwordHash)) {
        	loggedLibrarian = Librarian;
            return true;
        }
        return false;
    }

    public void logout() throws SQLException {
    	loggedLibrarian = null;
        DatabaseConnection.disconnect();
    }

    public void detectOverdueBooks() throws Exception {
    	if (loggedLibrarian == null) throw new IllegalStateException("Librarian not logged in");
        List<Borrowing> overdueList = borrowingDAO.findOverdueBooks(conn);
        if (overdueList == null || overdueList.isEmpty()) {
            System.out.println("No overdue borrowings found.");
            return;
        }

        for (Borrowing b : overdueList) {
            boolean success = fineDAO.issueFine(conn, b.getBorrowId(), b.getUserId(), 10);
            User u = userDAO.findById(conn, b.getUserId()); // make sure this method exists (below)
            String who = (u != null && u.getUsername() != null) ? u.getUsername()
                                                                : ("user_id=" + b.getUserId());
            if (success) {
                System.out.println("Fine issued for borrowId=" + b.getBorrowId() + " | borrower=" + who);
            } else {
                System.out.println("Failed to issue fine for borrowId=" + b.getBorrowId() + " | borrower=" + who);
            }
        }
    }

}
