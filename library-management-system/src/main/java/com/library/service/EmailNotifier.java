package com.library.service;

import com.library.model.User;

// Very small notifier that knows how to contact a User via email. 
public class EmailNotifier {
    private final EmailServer server;

    public EmailNotifier(EmailServer server) {
        this.server = server;
    }

    public void notify(User user, String message) {
        if (user == null || user.getEmail() == null) return;
        server.send(user.getEmail(), "Overdue Reminder", message);
    }
}
