package com.library.util;

import java.sql.Connection;
import java.util.List;

import com.library.dao.MediaDAO;
import com.library.model.Borrowing;
import com.library.model.Fine;
import com.library.model.Media;
import com.library.service.FineSummary;

/**
 * Console printing helpers for media lists, borrowings, fines,
 * and fine summaries. Used by the CLI layer.
 */
public class DisplayPrinter {

    /** DAO used to look up media details when printing borrowings. */
    private static final MediaDAO mediaDAO = new MediaDAO();

    /**
     * Prints a list of media items. Shows a placeholder when empty.
     *
     * @param mediaList list of media items
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
     * Prints borrowed media with due date and status. Skips returned items.
     *
     * @param conn _ active database connection
     * @param borrowings list of borrowing records
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
     * Prints a list of fines.
     *
     * @param fines list of fines
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
     * Prints a mixed-media fine summary with per-type totals and a final total.
     *
     * @param s fine summary
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
