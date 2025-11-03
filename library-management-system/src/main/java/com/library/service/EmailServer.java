package com.library.service;

public interface EmailServer {
    void send(String to, String subject, String body);
}
