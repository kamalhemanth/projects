package services;

import database.DatabaseManager;
import java.sql.*;

public class WalletService {
    private DatabaseManager dbManager;

    public WalletService(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    // Add money to the wallet from a bank account, card, or UPI
    public void addMoneyToWallet(int userId, double amount, String sourceType, String sourceDetails) {
        String query = "UPDATE Users SET balance = balance + ? WHERE userId = ?";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query)) {
            statement.setDouble(1, amount);
            statement.setInt(2, userId);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Amount added to wallet successfully.");
                // Log the transaction
                logTransaction(userId, amount, "ADD_MONEY", sourceType, sourceDetails);
            } else {
                System.out.println("Failed to add money to wallet.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get wallet balance
    public double getWalletBalance(int userId) {
        String query = "SELECT balance FROM Users WHERE userId = ?";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // Log the transaction when money is added to the wallet
    private void logTransaction(int userId, double amount, String type, String sourceType, String sourceDetails) {
        String query = "INSERT INTO Transactions (fromUserId, amount, type, status, paymentMethod, paymentDetails) VALUES (?, ?, ?, 'COMPLETED', ?, ?)";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setDouble(2, amount);
            statement.setString(3, type);
            statement.setString(4, sourceType);
            statement.setString(5, sourceDetails);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}