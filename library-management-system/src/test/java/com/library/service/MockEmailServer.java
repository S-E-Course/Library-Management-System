package com.library.service;

import java.util.ArrayList;
import java.util.List;

/** Test double that records emails instead of sending them. */
public class MockEmailServer implements EmailServer {

    public static class SentEmail {
        public final String to, subject, body;
        public SentEmail(String to, String subject, String body) {
            this.to = to; this.subject = subject; this.body = body;
        }
    }

    private final List<SentEmail> outbox = new ArrayList<>();

    @Override
    public void send(String to, String subject, String body) {
        outbox.add(new SentEmail(to, subject, body));
    }

    public List<SentEmail> getOutbox() {
        return outbox;
    }
}
