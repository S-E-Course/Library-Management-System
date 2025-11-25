package com.library.service;

import com.library.dao.BorrowingDAO;
import com.library.dao.UserDAO;
import com.library.model.Borrowing;
import com.library.model.User;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.*;

/**
 * Reminder logic for overdue borrowings (US3.1).
 * Counts overdue items per user and sends a message:
 * "You have n overdue book(s)." to each user.
 */
public class ReminderService {

    private final Connection conn;
    private final BorrowingDAO borrowingDAO;
    private final UserDAO userDAO;
    private final EmailNotifier notifier;

    /**
     * Creates a new reminder service.
     *
     * @param conn         SQL connection used for queries
     * @param borrowingDAO DAO for overdue borrowings
     * @param userDAO      DAO for users
     * @param notifier     helper used to send emails
     */
    public ReminderService(Connection conn,
                           BorrowingDAO borrowingDAO,
                           UserDAO userDAO,
                           EmailNotifier notifier) {
        this.conn = conn;
        this.borrowingDAO = borrowingDAO;
        this.userDAO = userDAO;
        this.notifier = notifier;
    }

    /**
     * Sends reminder emails to users who have overdue items.
     * The email body uses the text: "You have n overdue book(s).".
     *
     * @return map from user id to number of overdue items
     * @throws Exception if reading data or sending messages fails
     */
    public Map<Integer, Integer> sendOverdueReminders() throws Exception {
        List<Borrowing> all = borrowingDAO.findOverdueMedia(conn);
        if (all == null || all.isEmpty()) return Collections.emptyMap();

        LocalDate today = LocalDate.now();
        Map<Integer, Integer> counts = new HashMap<>();

        for (Borrowing b : all) {
            boolean overdue = "overdue".equalsIgnoreCase(b.getStatus())
                    || (b.getDueDate() != null && b.getDueDate().isBefore(today));
            if (!overdue) continue;
            counts.merge(b.getUserId(), 1, Integer::sum);
        }

        for (Map.Entry<Integer, Integer> e : counts.entrySet()) {
            User u = userDAO.findById(conn, e.getKey());
            String body = String.format("You have %d overdue book(s).", e.getValue());
            notifier.notify(u, body);
        }
        return counts;
    }
}
