package com.library.service;

import com.library.model.User;

/**
 * Sends email notifications to users.
 * Wraps an email server and provides a simple method to notify a user.
 */
public class EmailNotifier {
    private final EmailServer server;

    /**
     * Creates a notifier that uses the given email server.
     *
     * @param server email server to send messages through
     */
    public EmailNotifier(EmailServer server) {
        this.server = server;
    }

    /**
     * Sends a reminder message to the user's email.
     * Does nothing if the user or email address is missing.
     *
     * @param user_the user to notify
     * @param message_the message body to send
     */
    public void notify(User user, String message) {
        if (user == null || user.getEmail() == null) return;
        server.send(user.getEmail(), "Overdue Reminder", message);
    }
}
