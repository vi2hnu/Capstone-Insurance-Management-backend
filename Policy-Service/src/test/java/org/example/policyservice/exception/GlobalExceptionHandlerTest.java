package org.example.policyservice.exception;

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
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        FieldError fieldError = new FieldError("userDTO", "email", "Invalid email format");
        ObjectError globalError = new ObjectError("userDTO", "Global error message");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError, globalError));

        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleValidationException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid email format", response.getBody().get("email"));
        assertEquals("Global error message", response.getBody().get("globalError"));
    }

    @Test
    void handlePlanAlreadyExits_shouldReturnConflict() {
        PlanAlreadyExistsException ex = new PlanAlreadyExistsException("Plan already exists");
        
        ResponseEntity<String> response = globalExceptionHandler.handlePlanAlreadyExits(ex);
        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Plan already exists", response.getBody());
    }

    @Test
    void handlePlanNotExists_shouldReturnNotFound() {
        PlanNotFoundException ex = new PlanNotFoundException("Plan not found");
        
        ResponseEntity<String> response = globalExceptionHandler.handlePlanNotExists(ex);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Plan not found", response.getBody());
    }

    @Test
    void userAlreadyEnrolled_shouldReturnConflict() {
        UserAlreadyEnrolledException ex = new UserAlreadyEnrolledException("User already enrolled");
        
        ResponseEntity<String> response = globalExceptionHandler.userAlreadyEnrolled(ex);
        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User already enrolled", response.getBody());
    }

    @Test
    void policyNotExist_shouldReturnNotFound() {
        PolicyNotFoundException ex = new PolicyNotFoundException("Policy does not exist");
        
        ResponseEntity<String> response = globalExceptionHandler.policyNotExist(ex);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Policy does not exist", response.getBody());
    }

    @Test
    void userNotEnrolled_shouldReturnBadRequest() {
        UserNotEnrolledException ex = new UserNotEnrolledException("User not enrolled");
        
        ResponseEntity<String> response = globalExceptionHandler.userNotEnrolled(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User not enrolled", response.getBody());
    }

    @Test
    void policyNotEnrolledByAgent_shouldReturnBadRequest() {
        PolicyNotEnrolledByAgentException ex = new PolicyNotEnrolledByAgentException("Policy not enrolled by agent");
        
        ResponseEntity<String> response = globalExceptionHandler.policyNotEnrolledByAgent(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Policy not enrolled by agent", response.getBody());
    }
}