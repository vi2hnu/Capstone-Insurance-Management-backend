package org.example.policyservice.exception;

public class PlanAlreadyExistsException extends RuntimeException{
    public PlanAlreadyExistsException(String message) {
        super(message);
    }
}
