package services;

import database.DatabaseManager;
import models.Transaction;

import java.sql.*;

public class TransactionService {
    private DatabaseManager dbManager;

    public TransactionService(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void createTransactionPaytm(int fromUserId, String recipientPhoneNumber, double amount) {
        String query = "INSERT INTO Transactions (fromUserId, toUserId, amount, type, status) " +
                "VALUES (?, (SELECT userId FROM Users WHERE phoneNumber = ?), ?, 'PAYTM', 'PENDING')";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query,
                Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, fromUserId);
            statement.setString(2, recipientPhoneNumber);
            statement.setDouble(3, amount);
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                System.out.println("Transaction created with ID: " + rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void createTransactionBank(int fromUserId, String accountNumber, String ifscCode, double amount) {
        String query = "INSERT INTO Transactions (fromUserId, accountNumber, ifscCode, amount, type, status) VALUES (?, ?, ?, ?, 'BANK', 'PENDING')";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query,
                Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, fromUserId);
            statement.setString(2, accountNumber);
            statement.setString(3, ifscCode);
            statement.setDouble(4, amount);
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                System.out.println("Transaction created with ID: " + rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
}