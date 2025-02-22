package com.example.exceptions;

public class PaymentAppException extends Exception {
    public PaymentAppException(String message) {
        super(message);
    }

    public PaymentAppException(String message, Throwable cause) {
        super(message, cause);
    }

}