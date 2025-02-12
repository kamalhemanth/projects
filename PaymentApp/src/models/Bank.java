package models;

public class Bank {
    private int bankId;
    private int userId;
    private String accountNumber;
    private String ifscCode;
    private String bankName;

    public Bank(int bankId, int userId, String accountNumber, String ifscCode, String bankName) {
        this.bankId = bankId;
        this.userId = userId;
        this.accountNumber = accountNumber;
        this.ifscCode = ifscCode;
        this.bankName = bankName;
    }

    // Getters and Setters
    public int getBankId() {
        return bankId;
    }

    public int getUserId() {
        return userId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public String getBankName() {
        return bankName;
    }
}