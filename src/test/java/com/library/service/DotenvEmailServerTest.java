package com.library.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for DotenvEmailServer.
 */
class DotenvEmailServerTest {

    private final Path envPath = Paths.get(".env");

    /**
     * Deletes the temporary .env file after each test.
     *
     * @throws IOException if deletion fails
     */
    @AfterEach
    void cleanUp() throws IOException {
        if (Files.exists(envPath)) {
            Files.delete(envPath);
        }
    }

    /**
     * Helper method to write a .env file.
     *
     * @param content text to write
     * @throws IOException if writing fails
     */
    private void writeEnv(String content) throws IOException {
        Files.write(envPath, content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * When username and password are present, constructor should succeed.
     *
     * @throws Exception if the call fails
     */
    @Test
    void constructorSucceedsWhenCredentialsPresent() throws Exception {
        writeEnv("EMAIL_USERNAME=test@example.com\nEMAIL_PASSWORD=secret\n");

        DotenvEmailServer server = new DotenvEmailServer();

        assertNotNull(server);
    }

    /**
     * When credentials are missing, constructor should throw an exception.
     *
     * @throws Exception if file handling fails
     */
    @Test
    void constructorThrowsWhenCredentialsMissing() throws Exception {
        writeEnv("SOMETHING_ELSE=value\n");

        boolean thrown = false;

        try {
            new DotenvEmailServer();
        } catch (IllegalStateException e) {
            thrown = true;
        }

        assertTrue(thrown);
    }

    /**
     * send() should call EmailService.sendEmail with the same values.
     *
     * @throws Exception if reflection or file handling fails
     */
    @Test
    void sendDelegatesToEmailService() throws Exception {
        writeEnv("EMAIL_USERNAME=test@example.com\nEMAIL_PASSWORD=secret\n");

        DotenvEmailServer server = new DotenvEmailServer();

        EmailService mockService = mock(EmailService.class);
        Field f = DotenvEmailServer.class.getDeclaredField("emailService");
        f.setAccessible(true);
        f.set(server, mockService);

        server.send("to@example.com", "Subject", "Body");

        verify(mockService).sendEmail("to@example.com", "Subject", "Body");
    }
}
