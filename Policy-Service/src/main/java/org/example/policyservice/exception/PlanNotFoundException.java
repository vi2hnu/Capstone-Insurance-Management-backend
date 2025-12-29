package org.example.policyservice.exception;

public class PlanNotFoundException extends RuntimeException{
    public PlanNotFoundException(String message){
        super(message);
    }
}
