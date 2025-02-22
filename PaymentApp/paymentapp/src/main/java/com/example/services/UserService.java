package com.example.services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.example.database.DatabaseManager;
import com.example.exceptions.InvalidInputException;
import com.example.exceptions.UserNotFoundException;

public class UserService {
    private static final Logger logger = LogManager.getLogger(UserService.class);
    private DatabaseManager dbManager;
    private int loggedInUserId = -1;

    public UserService(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void registerUser(String phoneNumber, String password) {
        String query = "INSERT INTO Users (phoneNumber, password, name, email, balance) VALUES (?, ?, '', '', 0.0)";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query,
                Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, phoneNumber);
            statement.setString(2, password);
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                logger.info("User registered with ID: " + rs.getInt(1));
            }
        } catch (SQLException e) {
            logger.error("Failed to register user with phone number: " + phoneNumber, e);
        }
    }

    public boolean loginUser(String phoneNumber, String password) throws InvalidInputException, UserNotFoundException {
        String query = "SELECT userId FROM Users WHERE phoneNumber = ? AND password = ?";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query)) {
            statement.setString(1, phoneNumber);
            statement.setString(2, password);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                loggedInUserId = rs.getInt("userId");
                logger.info("Login successful. User ID: " + loggedInUserId);
                return true;
            } else {
                logger.warn("Invalid phone number or password for login attempt with phone number: {}", phoneNumber);
                throw new UserNotFoundException("User not found with phone number: " + phoneNumber);
            }
        } catch (SQLException e) {
            logger.error("Failed to login user with phone number: {}. Reason: {}", phoneNumber, e.getMessage(), e);
            throw new InvalidInputException("Invalid input or database error occurred.", e);
        }
    }

    public void updateUser(int userId, String name, String email, String phoneNumber) {
        String query = "UPDATE Users SET name = ?, email = ?, phoneNumber = ? WHERE userId = ?";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, phoneNumber);
            statement.setInt(4, userId);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                logger.info("User updated");
            } else {
                logger.error("User not found with ID: " + userId);
            }
        } catch (SQLException e) {
            logger.error("Failed to update user profile for user ID: " + userId, e);
        }
    }

    public int getLoggedInUserId() {
        return loggedInUserId;
    }

    public void logout() {
        loggedInUserId = -1;
        logger.info("Logged out successfully.");
    }
}
