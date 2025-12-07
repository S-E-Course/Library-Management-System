package com.library.service;

import com.library.dao.BorrowingDAO;
import com.library.dao.FineDAO;
import com.library.dao.MediaDAO;
import com.library.model.Borrowing;
import com.library.model.Fine;
import com.library.model.Media;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for FineReportService.
 */
class FineReportServiceTest {

    /**
     * Ensures an empty or null fine list produces an empty summary.
     *
     * @throws Exception if DAO access fails
     */
    @Test
    void emptyFinesReturnEmptySummary() throws Exception {
        Connection conn = mock(Connection.class);
        FineDAO fineDAO = mock(FineDAO.class);
        BorrowingDAO borrowingDAO = mock(BorrowingDAO.class);
        MediaDAO mediaDAO = mock(MediaDAO.class);

        when(fineDAO.findFines(conn, 1)).thenReturn(null);

        FineReportService service =
                new FineReportService(conn, fineDAO, borrowingDAO, mediaDAO);

        FineSummary summary = service.buildFineSummaryForUser(1);

        assertEquals(0.0, summary.getTotal());
        assertTrue(summary.getPerType().isEmpty());
    }

    /**
     * Ensures paid fines or zero-amount fines are ignored.
     *
     * @throws Exception if DAO access fails
     */
    @Test
    void ignoresPaidAndZeroAmountFines() throws Exception {
        Connection conn = mock(Connection.class);
        FineDAO fineDAO = mock(FineDAO.class);
        BorrowingDAO borrowingDAO = mock(BorrowingDAO.class);
        MediaDAO mediaDAO = mock(MediaDAO.class);

        Fine f1 = new Fine();
        f1.setId(1);
        f1.setUserId(10);
        f1.setAmount(50.0);
        f1.setPaid(true);

        Fine f2 = new Fine();
        f2.setId(2);
        f2.setUserId(10);
        f2.setAmount(0.0);
        f2.setPaid(false);

        Fine f3 = new Fine();
        f3.setId(3);
        f3.setUserId(10);
        f3.setAmount(30.0);
        f3.setPaid(false);

        List<Fine> fines = new ArrayList<>();
        fines.add(f1);
        fines.add(f2);
        fines.add(f3);

        when(fineDAO.findFines(conn, 10)).thenReturn(fines);

        Borrowing b3 = new Borrowing();
        b3.setBorrowId(100);
        b3.setMediaId(5);
        when(borrowingDAO.getFineBorrowing(conn, 3)).thenReturn(b3);

        Media m = new Media() {
            public String getType() { return "book"; }
            public int getBorrowDurationDays() { return 28; }
        };
        m.setId(5);
        when(mediaDAO.findById(conn, 5)).thenReturn(m);

        FineReportService service =
                new FineReportService(conn, fineDAO, borrowingDAO, mediaDAO);

        FineSummary summary = service.buildFineSummaryForUser(10);

        assertEquals(30.0, summary.getTotal());
        assertEquals(30.0, summary.getPerType().get("book"));
    }

    /**
     * Ensures fines are grouped by media type, including unknown types.
     *
     * @throws Exception if DAO access fails
     */
    @Test
    void groupsFinesByTypeAndHandlesUnknown() throws Exception {
        Connection conn = mock(Connection.class);
        FineDAO fineDAO = mock(FineDAO.class);
        BorrowingDAO borrowingDAO = mock(BorrowingDAO.class);
        MediaDAO mediaDAO = mock(MediaDAO.class);

        Fine f1 = new Fine();
        f1.setId(1);
        f1.setUserId(20);
        f1.setAmount(10.0);
        f1.setPaid(false);

        Fine f2 = new Fine();
        f2.setId(2);
        f2.setUserId(20);
        f2.setAmount(20.0);
        f2.setPaid(false);

        Fine f3 = new Fine();
        f3.setId(3);
        f3.setUserId(20);
        f3.setAmount(5.0);
        f3.setPaid(false);

        List<Fine> fines = new ArrayList<>();
        fines.add(f1);
        fines.add(f2);
        fines.add(f3);

        when(fineDAO.findFines(conn, 20)).thenReturn(fines);

        Borrowing b1 = new Borrowing();
        b1.setBorrowId(101);
        b1.setMediaId(11);
        when(borrowingDAO.getFineBorrowing(conn, 1)).thenReturn(b1);

        Borrowing b2 = new Borrowing();
        b2.setBorrowId(102);
        b2.setMediaId(12);
        when(borrowingDAO.getFineBorrowing(conn, 2)).thenReturn(b2);

        Borrowing b3 = new Borrowing();
        b3.setBorrowId(103);
        b3.setMediaId(13);
        when(borrowingDAO.getFineBorrowing(conn, 3)).thenReturn(b3);

        Media book = new Media() {
            public String getType() { return "book"; }
            public int getBorrowDurationDays() { return 28; }
        };
        book.setId(11);

        Media cd = new Media() {
            public String getType() { return "cd"; }
            public int getBorrowDurationDays() { return 7; }
        };
        cd.setId(12);

        when(mediaDAO.findById(conn, 11)).thenReturn(book);
        when(mediaDAO.findById(conn, 12)).thenReturn(cd);
        when(mediaDAO.findById(conn, 13)).thenReturn(null);

        FineReportService service =
                new FineReportService(conn, fineDAO, borrowingDAO, mediaDAO);

        FineSummary summary = service.buildFineSummaryForUser(20);

        assertEquals(35.0, summary.getTotal());
        Map<String, Double> perType = summary.getPerType();

        assertEquals(10.0, perType.get("book"));
        assertEquals(20.0, perType.get("cd"));
        assertEquals(5.0, perType.get("unknown"));
    }
}
