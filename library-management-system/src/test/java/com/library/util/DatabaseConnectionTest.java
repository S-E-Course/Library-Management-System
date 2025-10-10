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


//order tests with @Order(1), @Order(2), @Order(3)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DatabaseConnectionTest {

    private static Connection connection;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        connection = DatabaseConnection.connect();
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
        DatabaseConnection.disconnect();
    }
    

    @Test
    @org.junit.jupiter.api.Order(1)
    void testConnectionIsOpen() throws SQLException {
        assertThat("Connection should be open", connection.isClosed(), is(false));
    }
    
    @Test
    @org.junit.jupiter.api.Order(2)
    void testSingletonConnection1() throws SQLException, IOException {
        Connection secondConnection = DatabaseConnection.connect();
        assertThat("Should return the same connection instance when connection isClosed", secondConnection, sameInstance(connection));
        secondConnection = null;
    }
    
    @Test
    @org.junit.jupiter.api.Order(3)
    void testDisconnectClosesConnection() throws SQLException {
        DatabaseConnection.disconnect();
        assertThat("Connection should be closed after disconnect", connection.isClosed(), is(true));
    }
    
    @Test
    @org.junit.jupiter.api.Order(4)
    void testSingletonConnection2() throws SQLException, IOException {
    	connection = DatabaseConnection.connect();
        assertThat("Should return the same connection instance when connection = null", connection, sameInstance(connection));
    }
    

}
