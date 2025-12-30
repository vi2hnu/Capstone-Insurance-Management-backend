package org.example.policyservice.exception;

public class PolicyNotEnrolledByAgentException extends RuntimeException {
    public PolicyNotEnrolledByAgentException(String message) {
        super(message);
    }
}
