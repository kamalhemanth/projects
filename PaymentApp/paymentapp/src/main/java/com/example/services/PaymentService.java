package com.example.services;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.example.database.DatabaseManager;

public class PaymentService {
    private DatabaseManager dbManager;
    private static final Logger logger = LogManager.getLogger(PaymentService.class);

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
            logger.info("Payment made for transaction ID: " + transactionId);

            String updateQuery = "UPDATE Transactions SET status = 'COMPLETED' WHERE transactionId = ?";
            try (PreparedStatement updateStatement = dbManager.getConnection().prepareStatement(updateQuery)) {
                updateStatement.setInt(1, transactionId);
                updateStatement.executeUpdate();
            }
        } catch (SQLException e) {
            logger.error("Failed to make payment for transaction ID: " + transactionId, e);
        }
    }

    public void refundTransaction(int transactionId) {
        String query = "UPDATE Transactions SET status = 'REFUNDED' WHERE transactionId = ?";
        try (PreparedStatement statement = dbManager.getConnection().prepareStatement(query)) {
            statement.setInt(1, transactionId);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                logger.info("Transaction refunded");
            } else {
                logger.error("Transaction not found");
            }
        } catch (SQLException e) {
            logger.error("Failed to process refund for transaction ID: " + transactionId, e);
        }
    }
}
