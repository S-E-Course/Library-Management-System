package com.library.service;

/**
 * Basic interface for sending email messages.
 * Different implementations may send real emails or store them for testing.
 */
public interface EmailServer {

    /**
     * Sends a plain text email message.
     *
     * @param to      recipient email address
     * @param subject subject line
     * @param body    message body
     */
    void send(String to, String subject, String body);
}
