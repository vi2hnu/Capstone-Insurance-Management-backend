package org.example.policyservice.exception;

public class UserAlreadyEnrolledException extends RuntimeException {
    public UserAlreadyEnrolledException(String message) {
        super(message);
    }
}
