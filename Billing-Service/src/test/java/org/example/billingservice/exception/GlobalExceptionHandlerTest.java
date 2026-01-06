package org.example.billingservice.exception;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @Test
    void handleValidationException_returnsMapOfErrorsAndBadRequest() {
        FieldError fieldError = new FieldError("objectName", "userId", "User ID cannot be blank");
        ObjectError globalError = new ObjectError("objectName", "Global validation error occurred");

        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError, globalError));

        ResponseEntity<Map<String, String>> response = 
                globalExceptionHandler.handleValidationException(methodArgumentNotValidException);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        Map<String, String> body = response.getBody();
        assertTrue(body.containsKey("userId"));
        assertEquals("User ID cannot be blank", body.get("userId"));
        
        assertTrue(body.containsKey("globalError"));
        assertEquals("Global validation error occurred", body.get("globalError"));
    }

    @Test
    void razorpayClientFailed_returnsBadGatewayAndMessage() {
        String errorMessage = "Razorpay API unavailable";
        RazorPayClientException exception = new RazorPayClientException(errorMessage);

        ResponseEntity<String> response = globalExceptionHandler.razorpayClientFailed(exception);

        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @Test
    void transactionNotFound_returnsNotFoundAndMessage() {
        String errorMessage = "Transaction 123 not found";
        TransactionNotFoundException exception = new TransactionNotFoundException(errorMessage);

        ResponseEntity<String> response = globalExceptionHandler.transactionNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }
}