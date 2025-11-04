package com.library.service;

import java.util.ArrayList;
import java.util.List;

/**
 * A mock implementation of {@link EmailServer} used for testing purposes.
 * 
 * Instead of actually sending emails, this class records them in memory.
 * The recorded messages can later be inspected to verify that the system
 * sent the correct notifications.
 */
public class MockEmailServer implements EmailServer {

    /**
     * Represents a single email record that was "sent" by this mock server.
     * Contains the recipient address, subject, and body text.
     */
    public static class SentEmail {
        /** The recipient email address. */
        public final String to;
        /** The subject of the email. */
        public final String subject;
        /** The body content of the email. */
        public final String body;

        /**
         * Constructs a SentEmail object with the specified details.
         *
         * @param to recipient email address
         * @param subject subject line
         * @param body body text of the email
         */
        public SentEmail(String to, String subject, String body) {
            this.to = to;
            this.subject = subject;
            this.body = body;
        }
    }

    /** Stores all emails that were "sent" by this mock server. */
    private final List<SentEmail> outbox = new ArrayList<>();

    /**
     * Simulates sending an email by recording it in the outbox list.
     *
     * @param to recipient email address
     * @param subject subject line
     * @param body body text of the email
     */
    @Override
    public void send(String to, String subject, String body) {
        outbox.add(new SentEmail(to, subject, body));
    }

    /**
     * Returns all recorded sent emails.
     *
     * @return list of sent emails
     */
    public List<SentEmail> getOutbox() {
        return outbox;
    }
}
