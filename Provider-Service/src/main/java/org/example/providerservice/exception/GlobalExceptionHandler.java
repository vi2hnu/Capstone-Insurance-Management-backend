package org.example.providerservice.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errorMap = new HashMap<>();

        List<ObjectError> errorList = e.getBindingResult().getAllErrors();

        errorList.forEach(error -> {
            if (error instanceof FieldError fieldError) {
                String fieldName = fieldError.getField();
                String message = fieldError.getDefaultMessage();
                errorMap.put(fieldName, message);
            }
            else{
                String message = error.getDefaultMessage();
                errorMap.put("globalError", message);
            }
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMap);
    }

    @ExceptionHandler(HospitalNotFoundException.class)
    public ResponseEntity<String> handleHospitalNotFound(HospitalNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotAuthorizedException.class)
    public ResponseEntity<String> handleUserNotAuthorized(UserNotAuthorizedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(PlanAlreadyRegisteredException.class)
    public ResponseEntity<String> handlePlanAlreadyRegistered(PlanAlreadyRegisteredException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(HospitalBankNotFoundException.class)
    public ResponseEntity<String> hospitalBankNotFound(HospitalBankNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}

