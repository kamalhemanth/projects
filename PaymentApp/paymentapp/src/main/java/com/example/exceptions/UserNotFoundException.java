package com.example.exceptions;

public class UserNotFoundException extends PaymentAppException {
    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}