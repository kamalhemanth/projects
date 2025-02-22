package com.example.models;

public class Transaction {
    private int transactionId;
    private int fromUserId;
    private int toUserId;
    private String accountNumber;
    private String ifscCode;
    private String toAccountNumber;
    private String toIfscCode;
    private double amount;
    private String type;
    private String status;
    private String paymentMethod;
    private String paymentDetails;

    public Transaction(int transactionId, int fromUserId, int toUserId, String accountNumber, String ifscCode,
            String toAccountNumber, String toIfscCode, double amount, String type, String status,
            String paymentMethod, String paymentDetails) {
        this.transactionId = transactionId;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.accountNumber = accountNumber;
        this.ifscCode = ifscCode;
        this.toAccountNumber = toAccountNumber;
        this.toIfscCode = toIfscCode;
        this.amount = amount;
        this.type = type;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.paymentDetails = paymentDetails;
    }

    // Getters and Setters
    public int getTransactionId() {
        return transactionId;
    }

    public int getFromUserId() {
        return fromUserId;
    }

    public int getToUserId() {
        return toUserId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public String getToAccountNumber() {
        return toAccountNumber;
    }

    public String getToIfscCode() {
        return toIfscCode;
    }

    public double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentDetails() {
        return paymentDetails;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
