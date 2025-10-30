package com.library.service;

import com.library.dao.*;
import com.library.model.*;
import com.library.strategy.*;
import com.library.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Service for librarian actions such as:
 *  - login/logout
 *  - detecting overdue media
 *  - issuing fines using Strategy Pattern
 */
public class LibrarianService {
    private final Connection conn;
    private final BorrowingDAO borrowingDAO = new BorrowingDAO();
    private final FineDAO fineDAO = new FineDAO();
    private final UserDAO userDAO = new UserDAO();
    private final MediaDAO mediaDAO = new MediaDAO();
    private User loggedLibrarian;

    /** Establish DB connection */
    public LibrarianService() throws Exception {
        this.conn = DatabaseConnection.connect();
    }

    /** Authenticate librarian */
    public boolean login(String username, String passwordHash) throws Exception {
        User librarian = userDAO.findByUsername(conn, username);
        if (librarian != null && librarian.getPasswordHash().equals(passwordHash)) {
            loggedLibrarian = librarian;
            return true;
        }
        return false;
    }

    /** Logout and close DB connection */
    public void logout() throws SQLException {
        loggedLibrarian = null;
        DatabaseConnection.disconnect();
    }

    /**
     * Detect overdue media and automatically issue or update fines.
     */
    public void detectOverdueMedia() throws Exception {
        if (loggedLibrarian == null)
            throw new IllegalStateException("Librarian not logged in");

        List<Borrowing> overdueList = borrowingDAO.findOverdueMedia(conn);
        if (overdueList == null || overdueList.isEmpty()) {
            System.out.println("No overdue borrowings found.");
            return;
        }

        LocalDate today = LocalDate.now();

        for (Borrowing b : overdueList) {

            if ("overdue".equalsIgnoreCase(b.getStatus())) {
                Fine existingFine = fineDAO.getBorrowingFine(conn, b.getBorrowId());
                if (existingFine != null && existingFine.getFineDate() != null) {

                    long daysSinceLastFine = ChronoUnit.DAYS.between(existingFine.getFineDate(), today);
                    if (daysSinceLastFine < 1) continue;


                    fineDAO.updateFineBalance(conn, existingFine.getId(), (int)daysSinceLastFine);
                    fineDAO.updateFineDate(conn, existingFine.getId());
                    userDAO.updateUserBalance(conn, b.getUserId(), daysSinceLastFine);
                    
                    System.out.printf("Fine's amount updated for borrowId=%d (amount + %d NIS)%n", b.getBorrowId(), daysSinceLastFine);
                    continue;
                }
            }

            Media media = mediaDAO.findById(conn, b.getMediaId());
            if (media == null) continue;

            long overdueDays = ChronoUnit.DAYS.between(b.getDueDate(), today);
            if (overdueDays <= 0) continue;

            FineCalculator fineCalculator;
            switch (media.getType().toLowerCase()) {
                case "cd":
                    fineCalculator = new FineCalculator(new CDFineStrategy());
                    break;
                case "journal":
                    fineCalculator = new FineCalculator(new JournalFineStrategy());
                    break;
                default:
                    fineCalculator = new FineCalculator(new BookFineStrategy());
                    break;
            }
            double fineAmount = fineCalculator.calculateFine((int) overdueDays);
            boolean issued = fineDAO.issueFine(conn, b.getBorrowId(), b.getUserId(), fineAmount);

            if (issued) {
            	borrowingDAO.updateBorrowingStatus(conn, b.getBorrowId(), "overdue");
                userDAO.updateUserBalance(conn, b.getUserId(), fineAmount);
                System.out.printf("Issued new fine %.2f for borrowId=%d (overdue %d days)%n", fineAmount, b.getBorrowId(), overdueDays);
            } else {
                System.out.printf("Failed to issue fine for borrowId=%d%n", b.getBorrowId());
            }
        }
    }

}
