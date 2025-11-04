package com.library.util;

import java.sql.Connection;
import java.util.List;

import com.library.dao.MediaDAO;
import com.library.model.Borrowing;
import com.library.model.Fine;
import com.library.model.Media;
import com.library.service.FineSummary;

/**
 * Utility methods for printing lists and summaries to the console.
 * 
 * This class contains only static methods and is used by the CLI layer
 * to render media lists, borrowed items, fines, and mixed-media fine summaries.
 */
public class DisplayPrinter {

    /** Data-access helper used to resolve media details when printing borrowings. */
    private static final MediaDAO mediaDAO = new MediaDAO();

    /**
     * Prints a formatted list of media items to standard output.
     *
     * @param mediaList list of media items to print; prints a placeholder if null or empty
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
     * Prints the list of borrowed media for a user, including due date and status.
     * Items with status "returned" are skipped.
     *
     * @param conn       active database connection used to resolve media details
     * @param borrowings list of borrowing records to print
     * @throws Exception if media lookup fails
     */
    public static void printBorrowedMedia(Connection conn, List<Borrowing> borrowings) throws Exception {
        if (borrowings == null || borrowings.isEmpty()) {
            System.out.println("(You have no borrowed books)");
            return;
        }
        for (Borrowing b : borrowings) {
            if (!"returned".equalsIgnoreCase(b.getStatus())) {
                Media m = mediaDAO.findById(conn, b.getMediaId());
                System.out.printf("#%d  %s | Due: %s | Status: %s%n",
                        m.getId(), m.getTitle(), b.getDueDate(),
                        b.isOverdue() ? "Overdue (Can't return)" : "On Time");
            }
        }
    }

    /**
     * Prints a list of fines to standard output.
     *
     * @param fines list of fines to print; prints a placeholder if null or empty
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

    /**
     * Prints a user's mixed-media fine summary.
     * Shows per-type totals followed by a grand total line.
     *
     * @param s fine summary to print
     */
    public static void printFineSummary(FineSummary s) {
        System.out.println();
        System.out.println("---- Overdue Fine Summary (by media type) ----");
        if (s.getPerType().isEmpty()) {
            System.out.println("No unpaid fines.");
        } else {
            s.getPerType().forEach((type, amt) ->
                System.out.printf("  %-10s : %.2f%n", type, amt)
            );
            System.out.println("----------------------------------------------");
            System.out.printf("  TOTAL      : %.2f%n", s.getTotal());
        }
        System.out.println();
    }
}
