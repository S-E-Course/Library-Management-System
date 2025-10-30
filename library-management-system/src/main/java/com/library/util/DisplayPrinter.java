package com.library.util;

import java.sql.Connection;
import java.util.List;

import com.library.dao.MediaDAO;
import com.library.model.Borrowing;
import com.library.model.Fine;
import com.library.model.Media;

public class DisplayPrinter {
	
    private final static MediaDAO mediaDAO = new MediaDAO();
    
    /**
     * Prints a formatted list of media items.
     * @param mediaList list
     */
    public static void printMediaList(List<Media> mediaList) {
        if (mediaList == null || mediaList.isEmpty()) {
            System.out.println("(No matching media)");
            return;
        }
        for (Media m : mediaList) {
            System.out.printf("#%d  %s | %s | %s | %s | %s%n",
                    m.getId(), m.getTitle(), m.getAuthor(), m.getIsbn(), m.getType(),
                    m.isAvailable() ? "Available" : "Borrowed");
        }
    }

    /**
     * Prints the list of borrowed media with their due dates and status.
     * @param conn       active database connection
     * @param borrowings list of Borrowing records
     */
    public static void printBorrowedMedia(Connection conn, List<Borrowing> borrowings) throws Exception {
        if (borrowings == null || borrowings.isEmpty()) {
            System.out.println("(You have no borrowed books)");
            return;
        }
        for (Borrowing b : borrowings) {
        	if(!b.getStatus().equalsIgnoreCase("returned")) {
        		Media m = mediaDAO.findById(conn, b.getMediaId());
        		System.out.printf("#%d  %s | Due: %s | Status: %s%n",
                        m.getId(), m.getTitle(), b.getDueDate(), b.isOverdue() ? "Overdue (Can't return)" : "On Time");
        	}
        }
    }

    /**
     * Prints a summary of fines.
     * @param fines list
     */
    public static void printFines(List<Fine> fines) {
        if (fines == null || fines.isEmpty()) {
            System.out.println("(No fines found)");
            return;
        }

        for (Fine f : fines) {
            System.out.printf(
                "Fine #%d | Amount: %.2f NIS | Paid: %s%n",
                f.getId(),
                f.getAmount(),
                f.isPaid() ? "Yes" : "No"
            );
        }

    }

}
