package com.library.service;

import com.library.model.User;

/**
 * Simple adapter that knows how to notify a {@link User} by email
 * using the provided {@link EmailServer}.
 */
public class EmailNotifier {
    private final EmailServer server;

    /**
     * Creates a notifier that delegates to the given server.
     *
     * @param server email server implementation
     */
    public EmailNotifier(EmailServer server) {
        this.server = server;
    }

    /**
     * Sends a notification message to the user's email address.
     * No email is sent if the user or their email is missing.
     *
     * @param user    recipient user
     * @param message message body
     */
    public void notify(User user, String message) {
        if (user == null || user.getEmail() == null) return;
        server.send(user.getEmail(), "Overdue Reminder", message);
    }
}
