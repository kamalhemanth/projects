package com.example.services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.example.database.DatabaseManager;
import com.example.exceptions.InsufficientBalanceException;
import com.example.exceptions.TransactionFailedException;
import com.example.exceptions.UserNotFoundException;

public class TransactionService {
    private DatabaseManager dbManager;
    private WalletService walletService;
    private static final Logger logger = LogManager.getLogger(TransactionService.class);

    public TransactionService(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void createWalletToWalletTransaction(int fromUserId, String recipientPhoneNumber, double amount)
            throws UserNotFoundException, InsufficientBalanceException, TransactionFailedException {

        if (!isUserRegistered(fromUserId)) {
            logger.warn("Source user with ID {} is not registered.", fromUserId);
            throw new UserNotFoundException("Source user with ID " + fromUserId + " is not registered.");
        }

        int toUserId = getUserIdByPhoneNumber(recipientPhoneNumber);
        if (toUserId == -1) {
            logger.warn("Recipient user with phone number {} is not registered.", recipientPhoneNumber);
            throw new UserNotFoundException(
                    "Recipient user with phone number " + recipientPhoneNumber + " is not registered.");
        }

        walletService.deductFromWallet(fromUserId, amount);

        walletService.addToWallet(toUserId, amount);

        logTransaction(fromUserId, toUserId, amount, "WALLET_TO_WALLET", "COMPLETED");

        logger.info("Wallet-to-Wallet transaction completed successfully from user ID {} to user ID {}.", fromUserId,
                toUserId);
    }

    public void createWalletToBankTransaction(int fromUserId, String accountNumber, String ifscCode, double amount)
            throws UserNotFoundException, InsufficientBalanceException, TransactionFailedException {

        if (!isUserRegistered(fromUserId)) {
            logger.warn("Source user with ID {} is not registered.", fromUserId);
            throw new UserNotFoundException("Source user with ID " + fromUserId + " is not registered.");
        }

        walletService.deductFromWallet(fromUserId, amount);

        logTransaction(fromUserId, -1, amount, "WALLET_TO_BANK", "COMPLETED", accountNumber, ifscCode);

        logger.info("Wallet-to-Bank transaction completed successfully for user ID {}.", fromUserId);
    }

    private boolean isUserRegistered(int userId) {
        String query = "SELECT userId FROM Users WHERE userId = ?";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();
            return rs.next(); // Returns true if the user exists
        } catch (SQLException e) {
            logger.error("Error: Failed to check if user with ID " + userId + " is registered.", e);
            return false;
        }
    }

    private int getUserIdByPhoneNumber(String phoneNumber) {
        String query = "SELECT userId FROM Users WHERE phoneNumber = ?";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query)) {
            statement.setString(1, phoneNumber);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt("userId");
            }
        } catch (SQLException e) {
            logger.error("Error: Failed to retrieve user ID for phone number " + phoneNumber + ".", e);
        }
        return -1;
    }

    private double getWalletBalance(int userId) {
        String query = "SELECT balance FROM Users WHERE userId = ?";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        } catch (SQLException e) {
            logger.error("Error: Failed to retrieve wallet balance for user ID " + userId + ".", e);
        }
        return 0.0;
    }

    private void deductFromWallet(int userId, double amount) {
        String query = "UPDATE Users SET balance = balance - ? WHERE userId = ?";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query)) {
            statement.setDouble(1, amount);
            statement.setInt(2, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error: Failed to deduct amount from wallet for user ID " + userId + ".", e);
        }
    }

    private void addToWallet(int userId, double amount) {
        String query = "UPDATE Users SET balance = balance + ? WHERE userId = ?";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query)) {
            statement.setDouble(1, amount);
            statement.setInt(2, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error: Failed to add amount to wallet for user ID " + userId + ".", e);
        }
    }

    private void logTransaction(int fromUserId, int toUserId, double amount, String type, String status) {
        String query = "INSERT INTO Transactions (fromUserId, toUserId, amount, type, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query)) {
            statement.setInt(1, fromUserId);
            statement.setInt(2, toUserId);
            statement.setDouble(3, amount);
            statement.setString(4, type);
            statement.setString(5, status);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error: Failed to log wallet-to-wallet transaction for user ID " + fromUserId + ".", e);
        }
    }

    private void logTransaction(int fromUserId, int toUserId, double amount, String type, String status,
            String accountNumber, String ifscCode) {
        String query = "INSERT INTO Transactions (fromUserId, toUserId, amount, type, status, accountNumber, ifscCode) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query)) {
            statement.setInt(1, fromUserId);
            statement.setInt(2, toUserId);
            statement.setDouble(3, amount);
            statement.setString(4, type);
            statement.setString(5, status);
            statement.setString(6, accountNumber);
            statement.setString(7, ifscCode);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error: Failed to log wallet-to-bank transaction for user ID " + fromUserId + ".", e);
        }
    }

    public void viewTransactionsHistory(int userId) {
        String query = "SELECT * FROM Transactions WHERE fromUserId = ? OR toUserId = ?";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, userId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                logger.info("Transaction ID: " + rs.getInt("transactionId") +
                        ", Amount: " + rs.getDouble("amount") +
                        ", Type: " + rs.getString("type") +
                        ", Status: " + rs.getString("status"));
            }
        } catch (SQLException e) {
            logger.error("Error: Failed to retrieve transaction history for user ID " + userId + ".", e);
        }
    }

    public void createCardUpiNetbankingToUserTransaction(int fromUserId, int toUserId, double amount,
            String paymentMethod, String paymentDetails) {
        String transactionType;
        switch (paymentMethod) {
            case "CARD":
                transactionType = "CARD_TO_USER";
                break;
            case "UPI":
                transactionType = "UPI_TO_USER";
                break;
            case "NETBANKING":
                transactionType = "NETBANKING_TO_USER";
                break;
            default:
                logger.error("Error: Invalid payment method.");
                return;
        }

        String query = "INSERT INTO Transactions (fromUserId, toUserId, amount, type, status, paymentMethod, paymentDetails) VALUES (?, ?, ?, ?, 'PENDING', ?, ?)";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query)) {
            statement.setInt(1, fromUserId);
            statement.setInt(2, toUserId);
            statement.setDouble(3, amount);
            statement.setString(4, transactionType);
            statement.setString(5, paymentMethod);
            statement.setString(6, paymentDetails);
            statement.executeUpdate();
            logger.info("Transaction created via " + paymentMethod + " for user ID " + fromUserId + ".");
        } catch (SQLException e) {
            logger.error(
                    "Error: Failed to create transaction via " + paymentMethod + " for user ID " + fromUserId + ".", e);
        }
    }

    public void createBankToBankTransaction(int fromUserId, String fromAccountNumber, String fromIfscCode,
            String toAccountNumber, String toIfscCode, double amount) {
        String query = "INSERT INTO Transactions (fromUserId, accountNumber, ifscCode, toAccountNumber, toIfscCode, amount, type, status) VALUES (?, ?, ?, ?, ?, ?, 'BANK_TO_BANK', 'PENDING')";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query)) {
            statement.setInt(1, fromUserId);
            statement.setString(2, fromAccountNumber);
            statement.setString(3, fromIfscCode);
            statement.setString(4, toAccountNumber);
            statement.setString(5, toIfscCode);
            statement.setDouble(6, amount);
            statement.executeUpdate();
            logger.info("Bank-to-Bank transaction created for user ID " + fromUserId + ".");
        } catch (SQLException e) {
            logger.error("Error: Failed to create bank-to-bank transaction for user ID " + fromUserId + ".", e);
        }
    }

    public void refundTransaction(int transactionId, String refundTo, String reason) {

        String query = "SELECT * FROM Transactions WHERE transactionId = ?";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query)) {
            statement.setInt(1, transactionId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                int fromUserId = rs.getInt("fromUserId");
                int toUserId = rs.getInt("toUserId");
                double amount = rs.getDouble("amount");
                String type = rs.getString("type");
                String status = rs.getString("status");

                if (status.equals("REFUNDED")) {
                    logger.error("Error: Transaction with ID " + transactionId + " has already been refunded.");
                    return;
                }

                if (!status.equals("COMPLETED")) {
                    logger.error(
                            "Error: Only completed transactions can be refunded. Transaction ID: " + transactionId);
                    return;
                }

                switch (type) {
                    case "WALLET_TO_WALLET":
                        if (refundTo.equals("WALLET")) {

                            addToWallet(fromUserId, amount);
                            logger.info("Amount refunded to wallet for transaction ID " + transactionId + ".");
                        } else {
                            logger.error(
                                    "Error: Wallet-to-Wallet transactions can only be refunded to the wallet. Transaction ID: "
                                            + transactionId);
                        }
                        break;

                    case "WALLET_TO_BANK":
                        if (refundTo.equals("ORIGINAL_SOURCE")) {

                            boolean refundSuccess = processBankRefund(transactionId, amount);
                            if (refundSuccess) {
                                logger.info("Amount refunded to the original bank account for transaction ID "
                                        + transactionId + ".");
                            } else {
                                logger.error("Error: Refund failed for transaction ID " + transactionId + ".");
                            }
                        } else if (refundTo.equals("WALLET")) {

                            addToWallet(fromUserId, amount);
                            logger.info("Amount refunded to wallet for transaction ID " + transactionId + ".");
                        }
                        break;

                    case "CARD_TO_USER":
                    case "UPI_TO_USER":
                    case "NETBANKING_TO_USER":
                        if (refundTo.equals("ORIGINAL_SOURCE")) {

                            boolean refundSuccess = processRefundViaGateway(transactionId, amount);
                            if (refundSuccess) {
                                logger.info("Amount refunded to the original source for transaction ID " + transactionId
                                        + ".");
                            } else {
                                logger.error("Error: Refund failed for transaction ID " + transactionId + ".");
                            }
                        } else if (refundTo.equals("WALLET")) {

                            addToWallet(fromUserId, amount);
                            logger.info("Amount refunded to wallet for transaction ID " + transactionId + ".");
                        }
                        break;

                    case "BANK_TO_BANK":
                        if (refundTo.equals("ORIGINAL_SOURCE")) {

                            boolean refundSuccess = processBankRefund(transactionId, amount);
                            if (refundSuccess) {
                                logger.info("Amount refunded to the original bank account for transaction ID "
                                        + transactionId + ".");
                            } else {
                                logger.error("Error: Refund failed for transaction ID " + transactionId + ".");
                            }
                        } else if (refundTo.equals("WALLET")) {

                            addToWallet(fromUserId, amount);
                            logger.info("Amount refunded to wallet for transaction ID " + transactionId + ".");
                        }
                        break;
                    case "ADD_MONEY":
                        if (refundTo.equals("ORIGINAL_SOURCE")) {

                            boolean refundSuccess = processRefundViaGateway(transactionId, amount);
                            if (refundSuccess) {
                                logger.info("Amount refunded to the original source for transaction ID " + transactionId
                                        + ".");
                            } else {
                                logger.error("Error: Refund failed for transaction ID " + transactionId + ".");
                            }
                        } else if (refundTo.equals("WALLET")) {

                            deductFromWallet(fromUserId, amount);
                            logger.info("Amount refunded to wallet for transaction ID " + transactionId + ".");
                        }
                        break;

                    default:
                        logger.error("Error: Invalid transaction type for transaction ID " + transactionId + ".");
                }

                updateTransactionStatus(transactionId, "REFUNDED");

                logRefundReason(transactionId, reason);
            } else {
                logger.error("Error: Transaction with ID " + transactionId + " not found.");
            }
        } catch (SQLException e) {
            logger.error("Error: Failed to process refund for transaction ID " + transactionId + ".", e);
        }
    }

    private boolean processRefundViaGateway(int transactionId, double amount) {

        logger.info("Processing refund for transaction ID: " + transactionId + ", Amount: " + amount);
        return true;
    }

    private boolean processBankRefund(int transactionId, double amount) {

        logger.info("Processing bank refund for transaction ID: " + transactionId + ", Amount: " + amount);
        return true;
    }

    private void updateTransactionStatus(int transactionId, String status) {
        String query = "UPDATE Transactions SET status = ? WHERE transactionId = ?";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query)) {
            statement.setString(1, status);
            statement.setInt(2, transactionId);
            statement.executeUpdate();
            logger.info("Transaction status updated to " + status + " for transaction ID " + transactionId + ".");
        } catch (SQLException e) {
            logger.error("Error: Failed to update transaction status for transaction ID " + transactionId + ".", e);
        }
    }

    private void logRefundReason(int transactionId, String reason) {
        String query = "UPDATE Transactions SET refundReason = ? WHERE transactionId = ?";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query)) {
            statement.setString(1, reason);
            statement.setInt(2, transactionId);
            statement.executeUpdate();
            logger.info("Refund reason logged for transaction ID " + transactionId + ".");
        } catch (SQLException e) {
            logger.error("Error: Failed to log refund reason for transaction ID " + transactionId + ".", e);
        }
    }
}