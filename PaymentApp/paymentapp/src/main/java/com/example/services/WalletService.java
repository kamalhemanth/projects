package com.example.services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.example.database.DatabaseManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.example.exceptions.InsufficientBalanceException;
import com.example.exceptions.TransactionFailedException;

public class WalletService {
    private DatabaseManager dbManager;
    private static final Logger logger = LogManager.getLogger(WalletService.class);

    public WalletService(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void deductFromWallet(int userId, double amount)
            throws InsufficientBalanceException, TransactionFailedException {
        String query = "UPDATE Users SET balance = balance - ? WHERE userId = ? AND balance >= ?";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query)) {
            statement.setDouble(1, amount);
            statement.setInt(2, userId);
            statement.setDouble(3, amount);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                logger.debug("Amount {} deducted from wallet for user ID {}.", amount, userId);
            } else {
                logger.warn("Insufficient balance in wallet for user ID {}.", userId);
                throw new InsufficientBalanceException("Insufficient balance in wallet for user ID: " + userId);
            }
        } catch (SQLException e) {
            logger.error("Failed to deduct amount from wallet for user ID {}. Reason: {}", userId, e.getMessage(), e);
            throw new TransactionFailedException("Failed to deduct amount from wallet.", e);
        }
    }

    public void addToWallet(int userId, double amount) throws TransactionFailedException {
        String query = "UPDATE Users SET balance = balance + ? WHERE userId = ?";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query)) {
            statement.setDouble(1, amount);
            statement.setInt(2, userId);
            statement.executeUpdate();
            logger.debug("Amount {} added to wallet for user ID {}.", amount, userId);
        } catch (SQLException e) {
            logger.error("Failed to add amount to wallet for user ID {}. Reason: {}", userId, e.getMessage(), e);
            throw new TransactionFailedException("Failed to add amount to wallet.", e);
        }
    }

    public void addMoneyToWallet(int userId, double amount, String sourceType, String sourceDetails) {
        String query = "UPDATE Users SET balance = balance + ? WHERE userId = ?";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query)) {
            statement.setDouble(1, amount);
            statement.setInt(2, userId);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                logger.info("Amount added to wallet successfully.");
                // Log the transaction
                logTransaction(userId, amount, "ADD_MONEY", sourceType, sourceDetails);
            } else {
                logger.error("Failed to add money to wallet for user ID {}.", userId);
            }
        } catch (SQLException e) {
            logger.error("Error: Failed to add money to wallet for user ID {}. Reason: {}", userId, e.getMessage(), e);
        }
    }

    public double getWalletBalance(int userId) throws TransactionFailedException {
        String query = "SELECT balance FROM Users WHERE userId = ?";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query)) {
            statement.setInt(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    double balance = rs.getDouble("balance");
                    logger.debug("Wallet balance retrieved successfully for user ID {}. Balance: {}", userId, balance);
                    return balance;
                } else {
                    logger.warn("No wallet balance found for user ID {}.", userId);
                    throw new TransactionFailedException("No wallet balance found for user ID: " + userId);
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to retrieve wallet balance for user ID {}. Reason: {}", userId, e.getMessage(), e);
            throw new TransactionFailedException("Failed to retrieve wallet balance.", e);
        }
    }

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
            logger.error(
                    "Error: Failed to log transaction for user ID {}. Amount: {}, Type: {}, Source: {}, Details: {}. Reason: {}",
                    userId, amount, type, sourceType, sourceDetails, e.getMessage(), e);
        }
    }

}
