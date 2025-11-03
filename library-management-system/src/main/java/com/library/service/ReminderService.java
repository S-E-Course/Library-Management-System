package com.library.service;

import com.library.dao.BorrowingDAO;
import com.library.dao.UserDAO;
import com.library.model.Borrowing;
import com.library.model.User;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.*;

/*
  Finds all overdue borrowings and emails each user once with:
   "You have n overdue book(s)."
 */
public class ReminderService {

    private final Connection conn;
    private final BorrowingDAO borrowingDAO;
    private final UserDAO userDAO;
    private final EmailNotifier notifier;

    public ReminderService(Connection conn,
                           BorrowingDAO borrowingDAO,
                           UserDAO userDAO,
                           EmailNotifier notifier) {
        this.conn = conn;
        this.borrowingDAO = borrowingDAO;
        this.userDAO = userDAO;
        this.notifier = notifier;
    }

    // Returns map<userId, overdueCount> for verification.
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
