package com.library.service;

import com.library.dao.BorrowingDAO;
import com.library.dao.FineDAO;
import com.library.dao.MediaDAO;
import com.library.model.Borrowing;
import com.library.model.Fine;
import com.library.model.Media;

import java.sql.Connection;
import java.util.List;

/**
 * Builds a {@link FineSummary} for a user by reading unpaid fines from the database
 * and aggregating them by media type (book, cd, journal, etc.).
 *
 * <p>Feature: US5.3 (Mixed Media Handling).</p>
 *
 * <p>Note: This class does not calculate fines; it only reads existing unpaid fines
 * and summarizes them by type. Fine calculation is done elsewhere (e.g., via
 * {@code FineCalculator} and the fine strategies).</p>
 *
 * @author
 * @version 1.0
 */
public class FineReportService {

    /** Active database connection. */
    private final Connection conn;

    /** DAO for fines. */
    private final FineDAO fineDAO;

    /** DAO for borrowings. */
    private final BorrowingDAO borrowingDAO;

    /** DAO for media. */
    private final MediaDAO mediaDAO;

    /**
     * Creates a new report service.
     *
     * @param conn          active SQL connection
     * @param fineDAO       DAO for reading fines
     * @param borrowingDAO  DAO for mapping fine → borrowing
     * @param mediaDAO      DAO for reading media details (type)
     */
    public FineReportService(Connection conn, FineDAO fineDAO, BorrowingDAO borrowingDAO, MediaDAO mediaDAO) {
        this.conn = conn;
        this.fineDAO = fineDAO;
        this.borrowingDAO = borrowingDAO;
        this.mediaDAO = mediaDAO;
    }

    /**
     * Builds a summary of unpaid fines for the specified user.
     *
     * @param userId user id
     * @return populated {@link FineSummary}; empty summary if no unpaid fines found
     * @throws Exception on database access errors
     */
    public FineSummary buildFineSummaryForUser(int userId) throws Exception {
        List<Fine> fines = fineDAO.findFines(conn, userId);
        FineSummary summary = new FineSummary();
        if (fines == null || fines.isEmpty()) return summary;

        for (Fine f : fines) {
            if (f == null || f.isPaid() || f.getAmount() <= 0) continue;

            Borrowing b = borrowingDAO.getFineBorrowing(conn, f.getId());
            if (b == null) continue;

            Media m = mediaDAO.findById(conn, b.getMediaId());
            String type = (m == null || m.getType() == null) ? "unknown" : m.getType();

            summary.add(type, f.getAmount());
        }
        return summary;
    }
}
