package org.example.claimsservice.exception;

public class UnauthorizedClaimReviewException extends RuntimeException {
    public UnauthorizedClaimReviewException(String message) {
        super(message);
    }
}
