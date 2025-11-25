package com.library.util;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests DatabaseConnection using Mockito so no real database is used.
 */
class DatabaseConnectionTest {

    private static Connection mockConn;

    /**
     * Sets a mocked connection before tests.
     */
    @BeforeAll
    static void setup() {
        mockConn = mock(Connection.class);
    }

    /**
     * Tests that connect() returns a connection when none exists.
     */
    @Test
    void testConnectReturnsConnection() throws Exception {
        DatabaseConnection.setMockConnection(mockConn);

        Connection c = DatabaseConnection.connect();

        assertNotNull(c);
        assertEquals(mockConn, c);
    }

    /**
     * Tests that disconnect() calls close() on the connection.
     */
    @Test
    void testDisconnectClosesConnection() throws Exception {
        DatabaseConnection.setMockConnection(mockConn);

        when(mockConn.isClosed()).thenReturn(false);

        DatabaseConnection.disconnect();

        verify(mockConn).close();
    }

    /**
     * Tests that connect() does not replace an already open connection.
     */
    @Test
    void testConnectReusesExistingConnection() throws Exception {
        DatabaseConnection.setMockConnection(mockConn);

        when(mockConn.isClosed()).thenReturn(false);

        Connection c1 = DatabaseConnection.connect();
        Connection c2 = DatabaseConnection.connect();

        assertSame(c1, c2);
    }
}
