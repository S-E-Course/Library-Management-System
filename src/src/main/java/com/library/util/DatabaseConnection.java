package com.library.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

/**
 * Loads database settings from db.properties and provides a shared connection.
 */
public class DatabaseConnection {
    private static Connection connection;

    /**
     * Opens a database connection using values from db.properties.
     * Reuses the connection if it is already open.
     *
     * @return active SQL connection
     * @throws SQLException on database errors
     * @throws IOException if the properties file cannot be read
     */
    public static Connection connect() throws SQLException, IOException {
        if (connection == null || connection.isClosed()) {
            Properties props = new Properties();
            try (InputStream input =
                     DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
                if (input == null) {
                    throw new IOException("db.properties not found on classpath");
                }
                props.load(input);
            }

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");
            connection = DriverManager.getConnection(url, user, password);
        }
        return connection;
    }

    /**
     * Closes the active connection if it is open.
     *
     * @throws SQLException if closing fails
     */
    public static void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
    
    /**
     * Sets a mock connection for testing.
     *
     * @param c _ the mock connection to use
     */
    public static void setMockConnection(Connection c) {
        connection = c;
    }


}
