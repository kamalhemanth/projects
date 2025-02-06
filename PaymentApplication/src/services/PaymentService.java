package services;

import database.DatabaseManager;

import java.sql.*;

public class PaymentService {
    private DatabaseManager dbManager;

    public PaymentService(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void makePayment(int transactionId, String paymentMethod, String paymentDetails) {
        String query = "INSERT INTO Payments (transactionId, paymentMethod, paymentDetails) VALUES (?, ?, ?)";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query)) {
            statement.setInt(1, transactionId);
            statement.setString(2, paymentMethod);
            statement.setString(3, paymentDetails);
            statement.executeUpdate();
            System.out.println("Payment made for transaction ID: " + transactionId);

            // Update transaction status to COMPLETED
            String updateQuery = "UPDATE Transactions SET status = 'COMPLETED' WHERE transactionId = ?";
            try (PreparedStatement updateStatement = dbManager.getConnection().prepareStatement(updateQuery)) {
                updateStatement.setInt(1, transactionId);
                updateStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void refundTransaction(int transactionId) {
        String query = "UPDATE Transactions SET status = 'REFUNDED' WHERE transactionId = ?";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query)) {
            statement.setInt(1, transactionId);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Transaction refunded");
            } else {
                System.out.println("Transaction not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}