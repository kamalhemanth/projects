package com.example.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatabaseManager {
    private static final Logger logger = LogManager.getFormatterLogger(DatabaseManager.class);
    private static final String URL = "jdbc:mysql://localhost:3306/PaymentApplication?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USERNAME = "root"; // change this based on your MYSQL setup
    private static final String PASSWORD = "root";

    private Connection connection;

    public DatabaseManager() {
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD); // Establish the connecion to database
            logger.info("Connected to the database.");
        } catch (ClassNotFoundException e) {
            logger.error("MySQL JDBC Driver not found.Ensure the driver is added to the classpath ", e);
        } catch (SQLException e) {
            logger.error("Database connection failed. Verify the URL, username, password, and database status.", e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
                logger.info("Database connection closed.");
            }
        } catch (SQLException e) {
            logger.error("Error occurred while closing the database connection", e);
        }
    }
}
