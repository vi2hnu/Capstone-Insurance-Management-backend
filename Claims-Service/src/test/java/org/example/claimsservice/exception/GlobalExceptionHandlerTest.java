package org.example.claimsservice.exception;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.when;

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
        FieldError fieldError = new FieldError("objectName", "policyId", "Policy ID cannot be null");
        ObjectError globalError = new ObjectError("objectName", "Global error occurred");

        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError, globalError));

        ResponseEntity<Map<String, String>> response = 
                globalExceptionHandler.handleValidationException(methodArgumentNotValidException);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        Map<String, String> body = response.getBody();
        assertTrue(body.containsKey("policyId"));
        assertEquals("Policy ID cannot be null", body.get("policyId"));
        
        assertTrue(body.containsKey("globalError"));
        assertEquals("Global error occurred", body.get("globalError"));
    }

    @Test
    void userAlreadyEnrolled_returnsBadRequest_whenUnsupportedFileType() {
        String msg = "Only PDF allowed";
        UnsupportedFileTypeException ex = new UnsupportedFileTypeException(msg);

        ResponseEntity<String> response = globalExceptionHandler.userAlreadyEnrolled(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(msg, response.getBody());
    }

    @Test
    void handlePolicyNotFoundException_returnsNotFound() {
        String msg = "Policy 123 not found";
        PolicyNotFoundException ex = new PolicyNotFoundException(msg);

        ResponseEntity<String> response = globalExceptionHandler.handlePolicyNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(msg, response.getBody());
    }

    @Test
    void handleInvalidPolicyClaimException_returnsBadRequest() {
        String msg = "Insufficient coverage";
        InvalidPolicyClaimException ex = new InvalidPolicyClaimException(msg);

        ResponseEntity<String> response = globalExceptionHandler.handleInvalidPolicyClaimException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(msg, response.getBody());
    }

    @Test
    void claimNotFound_returnsNotFound() {
        String msg = "Claim 404 not found";
        ClaimNotFoundException ex = new ClaimNotFoundException(msg);

        ResponseEntity<String> response = globalExceptionHandler.claimNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(msg, response.getBody());
    }

    @Test
    void invalidClaimStage_returnsBadRequest() {
        String msg = "Cannot verify claim in COMPLETED stage";
        InvalidStageException ex = new InvalidStageException(msg);

        ResponseEntity<String> response = globalExceptionHandler.invalidClaimStage(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(msg, response.getBody());
    }

    @Test
    void unauthorizedProviderReview_returnsUnauthorized() {
        String msg = "Provider not associated with this claim";
        UnauthorizedClaimReviewException ex = new UnauthorizedClaimReviewException(msg);

        ResponseEntity<String> response = globalExceptionHandler.unauthorizedProviderReview(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(msg, response.getBody());
    }
}