package services;

import database.DatabaseManager;
import java.sql.*;

public class TransactionService {
    private DatabaseManager dbManager;

    public TransactionService(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    // Wallet-to-Wallet Transaction
    public void createWalletToWalletTransaction(int fromUserId, String recipientPhoneNumber, double amount) {
        // Check if the source user is registered
        if (!isUserRegistered(fromUserId)) {
            System.out.println("Error: Source user is not registered.");
            return;
        }

        // Get the recipient's user ID from their phone number
        int toUserId = getUserIdByPhoneNumber(recipientPhoneNumber);
        if (toUserId == -1) {
            System.out.println("Error: Recipient user is not registered.");
            return;
        }

        // Check if the source user has enough balance
        double fromUserBalance = getWalletBalance(fromUserId);
        if (fromUserBalance < amount) {
            System.out.println("Error: Insufficient balance in the source wallet.");
            return;
        }

        // Deduct amount from the source user's wallet
        deductFromWallet(fromUserId, amount);

        // Add amount to the recipient's wallet
        addToWallet(toUserId, amount);

        // Log the transaction
        logTransaction(fromUserId, toUserId, amount, "WALLET_TO_WALLET", "COMPLETED");

        System.out.println("Wallet-to-Wallet transaction completed successfully.");
    }

    // Wallet-to-Bank Transaction
    public void createWalletToBankTransaction(int fromUserId, String accountNumber, String ifscCode, double amount) {
        // Check if the source user is registered
        if (!isUserRegistered(fromUserId)) {
            System.out.println("Error: Source user is not registered.");
            return;
        }

        // Check if the source user has enough balance
        double fromUserBalance = getWalletBalance(fromUserId);
        if (fromUserBalance < amount) {
            System.out.println("Error: Insufficient balance in the source wallet.");
            return;
        }

        // Deduct amount from the source user's wallet
        deductFromWallet(fromUserId, amount);

        // Log the transaction
        logTransaction(fromUserId, -1, amount, "WALLET_TO_BANK", "COMPLETED", accountNumber, ifscCode);

        System.out.println("Wallet-to-Bank transaction completed successfully.");
    }

    // Check if a user is registered
    private boolean isUserRegistered(int userId) {
        String query = "SELECT userId FROM Users WHERE userId = ?";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();
            return rs.next(); // Returns true if the user exists
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get user ID by phone number
    private int getUserIdByPhoneNumber(String phoneNumber) {
        String query = "SELECT userId FROM Users WHERE phoneNumber = ?";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query)) {
            statement.setString(1, phoneNumber);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt("userId");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if the user is not found
    }

    // Get wallet balance for a user
    private double getWalletBalance(int userId) {
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

    // Deduct amount from a user's wallet
    private void deductFromWallet(int userId, double amount) {
        String query = "UPDATE Users SET balance = balance - ? WHERE userId = ?";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query)) {
            statement.setDouble(1, amount);
            statement.setInt(2, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add amount to a user's wallet
    private void addToWallet(int userId, double amount) {
        String query = "UPDATE Users SET balance = balance + ? WHERE userId = ?";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query)) {
            statement.setDouble(1, amount);
            statement.setInt(2, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Log the transaction (for wallet-to-wallet)
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
            e.printStackTrace();
        }
    }

    // Log the transaction (for wallet-to-bank)
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
            e.printStackTrace();
        }
    }

    // View transaction history for a user
    public void viewTransactionsHistory(int userId) {
        String query = "SELECT * FROM Transactions WHERE fromUserId = ? OR toUserId = ?";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, userId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                System.out.println("Transaction ID: " + rs.getInt("transactionId") +
                        ", Amount: " + rs.getDouble("amount") +
                        ", Type: " + rs.getString("type") +
                        ", Status: " + rs.getString("status"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create a card/UPI/netbanking to another user transaction
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
                System.out.println("Error: Invalid payment method.");
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
            System.out.println("Transaction created via " + paymentMethod);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create a bank-to-bank transaction
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
            System.out.println("Bank-to-Bank transaction created.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // In TransactionService.java or PaymentService.java

    // Refund a transaction
    public void refundTransaction(int transactionId, String refundTo, String reason) {
        // Step 1: Fetch transaction details
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

                // Step 2: Check if the transaction is already refunded
                if (status.equals("REFUNDED")) {
                    System.out.println("Error: This transaction has already been refunded.");
                    return;
                }

                // Step 3: Check if the transaction is eligible for refund
                if (!status.equals("COMPLETED")) {
                    System.out.println("Error: Only completed transactions can be refunded.");
                    return;
                }

                // Step 4: Handle refund based on the transaction type
                switch (type) {
                    case "WALLET_TO_WALLET":
                        if (refundTo.equals("WALLET")) {
                            // Refund to the source user's wallet
                            addToWallet(fromUserId, amount);
                            System.out.println("Amount refunded to wallet.");
                        } else {
                            System.out.println(
                                    "Error: Wallet-to-Wallet transactions can only be refunded to the wallet.");
                        }
                        break;

                    case "CARD_TO_USER":
                    case "UPI_TO_USER":
                    case "NETBANKING_TO_USER":
                        if (refundTo.equals("ORIGINAL_SOURCE")) {
                            // Refund to the original source (card/UPI/net banking)
                            boolean refundSuccess = processRefundViaGateway(transactionId, amount);
                            if (refundSuccess) {
                                System.out.println("Amount refunded to the original source.");
                            } else {
                                System.out.println("Error: Refund failed.");
                            }
                        } else if (refundTo.equals("WALLET")) {
                            // Refund to the source user's wallet
                            addToWallet(fromUserId, amount);
                            System.out.println("Amount refunded to wallet.");
                        }
                        break;

                    case "WALLET_TO_BANK":
                    case "BANK_TO_BANK":
                        if (refundTo.equals("ORIGINAL_SOURCE")) {
                            // Refund to the original bank account
                            boolean refundSuccess = processBankRefund(transactionId, amount);
                            if (refundSuccess) {
                                System.out.println("Amount refunded to the original bank account.");
                            } else {
                                System.out.println("Error: Refund failed.");
                            }
                        } else if (refundTo.equals("WALLET")) {
                            // Refund to the source user's wallet
                            addToWallet(fromUserId, amount);
                            System.out.println("Amount refunded to wallet.");
                        }
                        break;

                    default:
                        System.out.println("Error: Invalid transaction type.");
                }

                // Step 5: Update transaction status to REFUNDED
                updateTransactionStatus(transactionId, "REFUNDED");

                // Step 6: Log the refund reason
                logRefundReason(transactionId, reason);
            } else {
                System.out.println("Error: Transaction not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Simulate refund via payment gateway
    private boolean processRefundViaGateway(int transactionId, double amount) {
        // Replace this with actual payment gateway API call
        System.out.println("Processing refund for transaction ID: " + transactionId + ", Amount: " + amount);
        return true; // Simulate a successful refund
    }

    // Simulate bank refund
    private boolean processBankRefund(int transactionId, double amount) {
        // Replace this with actual bank API call
        System.out.println("Processing bank refund for transaction ID: " + transactionId + ", Amount: " + amount);
        return true; // Simulate a successful refund
    }

    // Update transaction status
    private void updateTransactionStatus(int transactionId, String status) {
        String query = "UPDATE Transactions SET status = ? WHERE transactionId = ?";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query)) {
            statement.setString(1, status);
            statement.setInt(2, transactionId);
            statement.executeUpdate();
            System.out.println("Transaction status updated to: " + status);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Log the refund reason
    private void logRefundReason(int transactionId, String reason) {
        String query = "UPDATE Transactions SET refundReason = ? WHERE transactionId = ?";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query)) {
            statement.setString(1, reason);
            statement.setInt(2, transactionId);
            statement.executeUpdate();
            System.out.println("Refund reason logged.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}