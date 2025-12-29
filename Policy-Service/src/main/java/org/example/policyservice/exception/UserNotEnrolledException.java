package org.example.policyservice.exception;

public class UserNotEnrolledException extends RuntimeException {
    public UserNotEnrolledException(String message) {
        super(message);
    }
}
