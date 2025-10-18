
package com.library.service;

import com.library.dao.BorrowingDAO;
import com.library.dao.FineDAO;
import com.library.dao.UserDAO;
import com.library.model.Borrowing;
import com.library.model.User;
import java.util.List;

public class LibrarianService {
    private final BorrowingDAO borrowingDAO = new BorrowingDAO();
    private final FineDAO fineDAO = new FineDAO();
    private final UserDAO userDAO = new UserDAO();

    public void detectOverdueBooks() throws Exception {
        List<Borrowing> overdueList = borrowingDAO.findOverdueBooks();
        if (overdueList == null || overdueList.isEmpty()) {
            System.out.println("No overdue borrowings found.");
            return;
        }

        for (Borrowing b : overdueList) {
            boolean success = fineDAO.issueFine(b.getBorrowId(), b.getUserId(), 10);
            User u = userDAO.findById(b.getUserId()); // make sure this method exists (below)
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
