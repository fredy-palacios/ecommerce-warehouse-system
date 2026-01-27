package com.fredypalacios.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        Properties props = new Properties();
        try (InputStream input =
                 DatabaseConnection.class
                     .getClassLoader()
                     .getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new RuntimeException("database.properties not found in resources");
            }
            props.load(input);
            URL = props.getProperty("db.url");
            USER = props.getProperty("db.user");
            PASSWORD = props.getProperty("db.password");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }

    public static Connection getConnection() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection connection =
                    DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Successfully connected to Oracle Database");
            return connection;
        } catch (ClassNotFoundException e) {
            System.err.println("Oracle JDBC Driver not found");
            throw new RuntimeException(e);
        } catch (SQLException e) {
            System.err.println("Error connecting to the database");
            System.err.println("Check if Oracle is running: docker ps");
            throw new RuntimeException(e);
        }
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connection closed");
            } catch (SQLException e) {
                System.err.println("Error closing connection");
            }
        }
    }
}