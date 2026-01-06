package org.example.providerservice.exception;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void handleValidationException_shouldReturnBadRequestAndErrorMap() {
        // Arrange
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        FieldError fieldError = new FieldError("dto", "hospitalName", "must not be blank");
        ObjectError globalError = new ObjectError("dto", "Global failure");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError, globalError));

        // Act
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleValidationException(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("must not be blank", response.getBody().get("hospitalName"));
        assertEquals("Global failure", response.getBody().get("globalError"));
    }

    @Test
    void handleHospitalNotFound_shouldReturnNotFound() {
        HospitalNotFoundException ex = new HospitalNotFoundException("Hospital not found");
        
        ResponseEntity<String> response = globalExceptionHandler.handleHospitalNotFound(ex);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Hospital not found", response.getBody());
    }

    @Test
    void handleUserNotAuthorized_shouldReturnForbidden() {
        UserNotAuthorizedException ex = new UserNotAuthorizedException("User not authorized");
        
        ResponseEntity<String> response = globalExceptionHandler.handleUserNotAuthorized(ex);
        
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("User not authorized", response.getBody());
    }

    @Test
    void handlePlanAlreadyRegistered_shouldReturnConflict() {
        PlanAlreadyRegisteredException ex = new PlanAlreadyRegisteredException("Plan already registered");
        
        ResponseEntity<String> response = globalExceptionHandler.handlePlanAlreadyRegistered(ex);
        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Plan already registered", response.getBody());
    }

    @Test
    void hospitalBankNotFound_shouldReturnNotFound() {
        HospitalBankNotFoundException ex = new HospitalBankNotFoundException("Bank details not found");
        
        ResponseEntity<String> response = globalExceptionHandler.hospitalBankNotFound(ex);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Bank details not found", response.getBody());
    }

    @Test
    void hospitalAlreadyExists_shouldReturnConflict() {
        HospitalAlreadyExistsException ex = new HospitalAlreadyExistsException("Hospital exists");
        
        ResponseEntity<String> response = globalExceptionHandler.hospitalAlreadyExists(ex);
        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Hospital exists", response.getBody());
    }

    @Test
    void userAlreadyRegistered_shouldReturnConflict() {
        UserAlreadyRegisteredException ex = new UserAlreadyRegisteredException("User already mapped");
        
        ResponseEntity<String> response = globalExceptionHandler.userAlreadyRegistered(ex);
        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User already mapped", response.getBody());
    }

    @Test
    void planNotFoundException_shouldReturnNotFound() {
        PlanNotFoundException ex = new PlanNotFoundException("Plan not found");
        
        ResponseEntity<String> response = globalExceptionHandler.planNotFoundException(ex);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Plan not found", response.getBody());
    }
}