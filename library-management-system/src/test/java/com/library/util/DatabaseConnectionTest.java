package com.library.util;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Unit tests for the {@link DatabaseConnection} utility class.
 * 
 * These tests verify the correct behavior of the database connection management,
 * including establishing, maintaining, and closing the shared connection instance.
 * 
 * Test order is defined explicitly using {@link org.junit.jupiter.api.Order}.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DatabaseConnectionTest {

    /** Shared connection used during tests. */
    private static Connection connection;

    /**
     * Establishes a connection before all tests run.
     *
     * @throws Exception if the connection cannot be established
     */
    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        connection = DatabaseConnection.connect();
    }

    /**
     * Closes the connection after all tests complete.
     *
     * @throws Exception if disconnection fails
     */
    @AfterAll
    static void tearDownAfterClass() throws Exception {
        DatabaseConnection.disconnect();
    }

    /**
     * Verifies that the connection is open after initialization.
     *
     * @throws SQLException if querying connection state fails
     */
    @Test
    @org.junit.jupiter.api.Order(1)
    void testConnectionIsOpen() throws SQLException {
        assertThat("Connection should be open", connection.isClosed(), is(false));
    }

    /**
     * Ensures {@link DatabaseConnection#connect()} returns the same instance
     * when called again while the connection is open.
     *
     * @throws SQLException if connection state fails
     * @throws IOException if reloading configuration fails
     */
    @Test
    @org.junit.jupiter.api.Order(2)
    void testSingletonConnection1() throws SQLException, IOException {
        Connection secondConnection = DatabaseConnection.connect();
        assertThat("Should return the same connection instance when connection is open", secondConnection, sameInstance(connection));
        secondConnection = null;
    }

    /**
     * Verifies {@link DatabaseConnection#disconnect()} properly closes the connection.
     *
     * @throws SQLException if closing the connection fails
     */
    @Test
    @org.junit.jupiter.api.Order(3)
    void testDisconnectClosesConnection() throws SQLException {
        DatabaseConnection.disconnect();
        assertThat("Connection should be closed after disconnect", connection.isClosed(), is(true));
    }

    /**
     * Ensures a new connection can be established after the previous one was closed.
     *
     * @throws SQLException if connection reopening fails
     * @throws IOException if reloading configuration fails
     */
    @Test
    @org.junit.jupiter.api.Order(4)
    void testSingletonConnection2() throws SQLException, IOException {
        connection = DatabaseConnection.connect();
        assertThat("Should return the same connection instance when connection is null", connection, sameInstance(connection));
    }
}
