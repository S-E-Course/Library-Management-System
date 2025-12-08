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
 * Creates a summary of unpaid fines for a user.
 * Each fine is grouped by the media type such as book, cd, or journal.
 */
public class FineReportService {

    private final Connection conn;
    private final FineDAO fineDAO;
    private final BorrowingDAO borrowingDAO;
    private final MediaDAO mediaDAO;

    /**
     * Builds a report service with the required DAOs.
     *
     * @param conn         database connection
     * @param fineDAO      DAO for fines
     * @param borrowingDAO DAO for borrowings
     * @param mediaDAO     DAO for media details
     */
    public FineReportService(Connection conn, FineDAO fineDAO, BorrowingDAO borrowingDAO, MediaDAO mediaDAO) {
        this.conn = conn;
        this.fineDAO = fineDAO;
        this.borrowingDAO = borrowingDAO;
        this.mediaDAO = mediaDAO;
    }

    /**
     * Creates a fine summary for a user.
     * Only unpaid fines with a positive amount are included.
     *
     * @param userId user identifier
     * @return fine summary for the user
     * @throws Exception if database access fails
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
