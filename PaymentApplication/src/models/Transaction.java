package models;

public class Transaction {
    private int transactionId;
    private int fromUserId;
    private int toUserId;
    private String accountNumber;
    private String ifscCode;
    private double amount;
    private String type;
    private String status;

    public Transaction(int transactionId, int fromUserId, int toUserId, String accountNumber, String ifscCode,
            double amount, String type, String status) {
        this.transactionId = transactionId;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.accountNumber = accountNumber;
        this.ifscCode = ifscCode;
        this.amount = amount;
        this.type = type;
        this.status = status;
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

    public double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}