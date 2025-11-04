package com.library.service;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Loads SMTP credentials from a .env file and provides a configured {@link EmailServer}.
 * It looks for EMAIL_USERNAME and EMAIL_PASSWORD. The .env file can be placed
 * on the application classpath (for example target/classes/.env).
 *
 * Use {@link #send(String, String, String)} to send messages via the underlying mailer.
 */
public class DotenvEmailServer implements EmailServer {

    private final EmailService emailService;

    /**
     * Creates an instance wired with credentials from .env.
     * Uses a fallback search order to locate the .env file.
     *
     * @throws IllegalStateException if the required variables are missing
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
     * Attempts to load the .env file from several common locations.
     *
     * @return a loaded {@link Dotenv} instance
     * @throws RuntimeException if the file cannot be loaded
     */
    private Dotenv tryLoad() {
        try {
            return Dotenv.load();
        } catch (Exception ignored) {}
        try {
            return Dotenv.configure().directory("target/classes").load();
        } catch (Exception ignored) {}
        try {
            return Dotenv.configure().directory("src/main/resources").load();
        } catch (Exception ignored) {}
        return Dotenv.configure().load(); // intentionally throws if still missing
    }

    /**
     * Sends an email using the underlying {@link EmailService}.
     *
     * @param to      recipient address
     * @param subject subject line
     * @param body    plain text body
     */
    @Override
    public void send(String to, String subject, String body) {
        emailService.sendEmail(to, subject, body);
    }
}
