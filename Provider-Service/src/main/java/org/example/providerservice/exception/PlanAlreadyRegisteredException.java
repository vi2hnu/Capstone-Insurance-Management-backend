package org.example.providerservice.exception;

public class PlanAlreadyRegisteredException extends RuntimeException {
    public PlanAlreadyRegisteredException(String message) {
        super(message);
    }
}
