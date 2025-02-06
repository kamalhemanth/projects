package models;

public class Payment {
    private int paymentId;
    private int transactionId;
    private String paymentMethod;
    private String paymentDetails;

    public Payment(int paymentId, int transactionId, String paymentMethod, String paymentDetails) {
        this.paymentId = paymentId;
        this.transactionId = transactionId;
        this.paymentMethod = paymentMethod;
        this.paymentDetails = paymentDetails;
    }

    // Getters and Setters
    public int getPaymentId() {
        return paymentId;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentDetails() {
        return paymentDetails;
    }
}