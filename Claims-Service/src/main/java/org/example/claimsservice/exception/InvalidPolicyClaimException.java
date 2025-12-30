package org.example.claimsservice.exception;

public class InvalidPolicyClaimException extends RuntimeException {
    public InvalidPolicyClaimException(String message) {
        super(message);
    }       
}
