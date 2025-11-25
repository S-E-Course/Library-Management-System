package com.library.service;

import com.library.dao.BorrowingDAO;
import com.library.dao.UserDAO;
import com.library.model.Borrowing;
import com.library.model.User;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests sending overdue reminders using mocked DAO and email server.
 */
class ReminderServiceTest {

    /**
     * Tests that reminders are sent to the correct users with the correct counts.
     *
     * @throws Exception if DAO calls fail
     */
    @Test
    void sendsExpectedOverdueReminders() throws Exception {
        Connection conn = null;

        BorrowingDAO borrowingDAO = mock(BorrowingDAO.class);
        UserDAO userDAO = mock(UserDAO.class);
        EmailServer emailServer = mock(EmailServer.class);

        Borrowing b1 = new Borrowing();
        b1.setUserId(1);
        b1.setStatus("overdue");
        b1.setDueDate(LocalDate.now().minusDays(3));

        Borrowing b2 = new Borrowing();
        b2.setUserId(1);
        b2.setStatus("borrowed");
        b2.setDueDate(LocalDate.now().minusDays(1));

        Borrowing b3 = new Borrowing();
        b3.setUserId(2);
        b3.setStatus("overdue");
        b3.setDueDate(LocalDate.now().minusDays(5));

        List<Borrowing> list = Arrays.asList(b1, b2, b3);
        when(borrowingDAO.findOverdueMedia(conn)).thenReturn(list);

        User u1 = new User();
        u1.setUserId(1);
        u1.setEmail("u1@example.com");

        User u2 = new User();
        u2.setUserId(2);
        u2.setEmail("u2@example.com");

        when(userDAO.findById(conn, 1)).thenReturn(u1);
        when(userDAO.findById(conn, 2)).thenReturn(u2);

        ReminderService service =
                new ReminderService(conn, borrowingDAO, userDAO, new EmailNotifier(emailServer));

        Map<Integer, Integer> counts = service.sendOverdueReminders();

        assertEquals(2, counts.get(1));
        assertEquals(1, counts.get(2));

        verify(emailServer, times(1))
                .send("u1@example.com", "Overdue Reminder", "You have 2 overdue book(s).");
        verify(emailServer, times(1))
                .send("u2@example.com", "Overdue Reminder", "You have 1 overdue book(s).");
        verifyNoMoreInteractions(emailServer);
    }
}
