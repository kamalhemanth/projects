package database;

import java.sql.*;

// Database Connection details which will have database URL,username and password
public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/PaymentApplication?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USERNAME = "root"; // change this based on your MYSQL setup
    private static final String PASSWORD = "root";

    private Connection connection; // Database connection Object

    // constructor to initialize the database connection
    public DatabaseManager() {
        try {
            // Explicitly load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD); // Establish the connecion to database
            System.out.println("Connected to the database.");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Add it to your classpath.");
            e.printStackTrace(); // Handle database connection errors
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() { // Method to get database connection
        return connection;
    }

    public void closeConnection() { // Method to close database connection
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}