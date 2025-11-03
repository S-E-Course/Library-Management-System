package com.library.service;

import io.github.cdimascio.dotenv.Dotenv;

public class DotenvEmailServer implements EmailServer {

    private final EmailService emailService;

    public DotenvEmailServer() {
        Dotenv dotenv = tryLoad();

        String username = dotenv.get("EMAIL_USERNAME");
        String password = dotenv.get("EMAIL_PASSWORD");
        if (username == null || password == null) {
            throw new IllegalStateException("Missing EMAIL_USERNAME or EMAIL_PASSWORD in .env");
        }
        this.emailService = new EmailService(username, password);
    }

    private Dotenv tryLoad() {
        // 1) Default: working directory (project root recommended)
        try {
            return Dotenv.load();
        } catch (Exception ignored) {}

        // 2) target/classes (when you placed .env there)
        try {
            return Dotenv.configure()
                    .directory("target/classes")
                    .load();
        } catch (Exception ignored) {}

        // 3) src/main/resources (if you put it there during dev)
        try {
            return Dotenv.configure()
                    .directory("src/main/resources")
                    .load();
        } catch (Exception ignored) {}

        // 4) Last resort: do not ignore missing to surface the error
        return Dotenv.configure().load(); // will throw the "Could not find /.env" error
    }

    @Override
    public void send(String to, String subject, String body) {
        emailService.sendEmail(to, subject, body);
    }
}
