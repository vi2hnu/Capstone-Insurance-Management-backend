package org.example.claimsservice.exception;

public class NoBankDetailsFoundException extends RuntimeException {
    public NoBankDetailsFoundException(String message) {
        super(message);
    }
}
