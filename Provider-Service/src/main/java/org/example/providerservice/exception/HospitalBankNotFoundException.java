package org.example.providerservice.exception;

public class HospitalBankNotFoundException extends RuntimeException {
    public HospitalBankNotFoundException(String message) {
        super(message);
    }
}
