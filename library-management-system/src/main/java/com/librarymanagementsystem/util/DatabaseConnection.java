package com.librarymanagementsystem.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class DatabaseConnection {
    private static Connection connection;

    public static Connection connect() throws SQLException, IOException {
        if (connection == null || connection.isClosed()) {
        	//Creates an empty container to hold DB settings.
            Properties props = new Properties();
            //Loads the db.properties file from src/main/resources
            try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
                if (input == null) {
                    throw new IOException("db.properties file not found");
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

    public static void disconnect() throws SQLException {
    	if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Disconnected from DB.");
        }
    }
}

