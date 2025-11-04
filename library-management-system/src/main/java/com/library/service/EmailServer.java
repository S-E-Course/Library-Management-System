package com.library.service;

/**
 * Minimal email-sending abstraction used by production and test code.
 * Implementations may deliver messages via SMTP or capture them in-memory.
 */
public interface EmailServer {

    /**
     * Sends a plain text message.
     *
     * @param to      recipient email address
     * @param subject subject line
     * @param body    message body text
     */
    void send(String to, String subject, String body);
}
