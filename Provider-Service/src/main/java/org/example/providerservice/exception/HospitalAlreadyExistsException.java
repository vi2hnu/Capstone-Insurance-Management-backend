package org.example.providerservice.exception;

public class HospitalAlreadyExistsException extends RuntimeException {
    public HospitalAlreadyExistsException(String message) {
        super(message);
    }
}
