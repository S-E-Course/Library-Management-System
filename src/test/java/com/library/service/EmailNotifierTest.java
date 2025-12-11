package com.library.service;

import com.library.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for EmailNotifier
 */
class EmailNotifierTest {

    /**
     * Checks that an email is sent when the user and email are present.
     */
    @Test
    void notifySendsEmailWhenUserAndEmailExist() {
        EmailServer server = mock(EmailServer.class);
        EmailNotifier notifier = new EmailNotifier(server);

        User u = new User();
        u.setEmail("user@example.com");

        notifier.notify(u, "Hello");

        verify(server).send("user@example.com", "Overdue Reminder", "Hello");
    }

    /**
     * Checks that nothing happens when the user is null.
     */
    @Test
    void notifyDoesNothingWhenUserIsNull() {
        EmailServer server = mock(EmailServer.class);
        EmailNotifier notifier = new EmailNotifier(server);

        notifier.notify(null, "Hello");

        verifyNoInteractions(server);
    }

    /**
     * Checks that nothing happens when the email is null.
     */
    @Test
    void notifyDoesNothingWhenEmailIsNull() {
        EmailServer server = mock(EmailServer.class);
        EmailNotifier notifier = new EmailNotifier(server);

        User u = new User();
        u.setEmail(null);

        notifier.notify(u, "Hello");

        verifyNoInteractions(server);
    }
}
