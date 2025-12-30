package org.example.claimsservice.exception;

public class ClaimAlreadySubmittedException extends RuntimeException {
    public ClaimAlreadySubmittedException(String message) {
        super(message);
    }
}
