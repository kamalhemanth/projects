package services;

import database.DatabaseManager;
import models.User;

import java.sql.*;

public class UserService {
    private DatabaseManager dbManager;
    private int loggedInUserId = -1; // Track the logged-in user

    public UserService(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void registerUser(String phoneNumber, String password) {
        String query = "INSERT INTO Users (phoneNumber, password, name, email) VALUES (?, ?, '', '')";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query,
                Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, phoneNumber);
            statement.setString(2, password);
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                System.out.println("User registered with ID: " + rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean loginUser(String phoneNumber, String password) {
        String query = "SELECT userId FROM Users WHERE phoneNumber = ? AND password = ?";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query)) {
            statement.setString(1, phoneNumber);
            statement.setString(2, password);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                loggedInUserId = rs.getInt("userId");
                System.out.println("Login successful. User ID: " + loggedInUserId);
                return true;
            } else {
                System.out.println("Invalid phone number or password.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
                System.out.println("User updated");
            } else {
                System.out.println("User not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getLoggedInUserId() {
        return loggedInUserId;
    }

    public void logout() {
        loggedInUserId = -1;
        System.out.println("Logged out successfully.");
    }
}