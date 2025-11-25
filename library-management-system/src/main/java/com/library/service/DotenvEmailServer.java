package com.library.service;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Email server that reads SMTP credentials from a .env file.
 * Looks for EMAIL_USERNAME and EMAIL_PASSWORD and uses them to
 * configure an EmailService instance.
 */
public class DotenvEmailServer implements EmailServer {

    private final EmailService emailService;

    /**
     * Loads credentials from the .env file and creates the email service.
     *
     * @throws IllegalStateException if required fields are missing
     */
    public DotenvEmailServer() {
        Dotenv dotenv = tryLoad();

        String username = dotenv.get("EMAIL_USERNAME");
        String password = dotenv.get("EMAIL_PASSWORD");
        if (username == null || password == null) {
            throw new IllegalStateException("Missing EMAIL_USERNAME or EMAIL_PASSWORD in .env");
        }
        this.emailService = new EmailService(username, password);
    }

    /**
     * Tries to load a .env file from a knwon psth.
     *
     * @return loaded Dotenv instance
     */
    private Dotenv tryLoad() {
        try {
            return Dotenv.load();
        } catch (Exception ignored) {}
        try {
            return Dotenv.configure().directory("target/classes").load();
        } catch (Exception ignored) {}
        return Dotenv.configure().load();
    }

    /**
     * Sends an email using the configured EmailService.
     *
     * @param to recipient email
     * @param subject email subject
     * @param body email body text
     */
    @Override
    public void send(String to, String subject, String body) {
        emailService.sendEmail(to, subject, body);
    }
}
