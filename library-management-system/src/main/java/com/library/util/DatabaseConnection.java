
package com.library.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

/**
 * Reads db.properties from classpath (src/main/resources or resources on build).
 * Provides a shared Connection. Safe to call multiple times.
 */
public class DatabaseConnection {
    private static Connection connection;
    /**
     * Connects to the database using configuration from db.properties.
     * 
     * If a connection already exists and is open, it will reuse it.
     * 
     * @return a valid SQL Connection
     * @throws SQLException if a database access error occurs
     * @throws IOException if the db.properties file cannot be found or read
     */
    public static Connection connect() throws SQLException, IOException {
        if (connection == null || connection.isClosed()) {
            Properties props = new Properties();
            try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
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
     * Closes the active database connection if it is currently open.
     * 
     * @throws SQLException if closing the connection fails
     */
    public static void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
