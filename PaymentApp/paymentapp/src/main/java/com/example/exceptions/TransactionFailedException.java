package com.example.exceptions;

public class TransactionFailedException extends PaymentAppException {
    public TransactionFailedException(String message) {
        super(message);
    }

    public TransactionFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
