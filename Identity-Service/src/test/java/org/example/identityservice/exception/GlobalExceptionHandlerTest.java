package org.example.identityservice.exception;


import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    void handleValidationException_ShouldReturnMapWithFieldAndGlobalErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError = new FieldError("userDto", "email", "Email is invalid");
        ObjectError globalError = new ObjectError("userDto", "Passwords do not match");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(fieldError, globalError));

        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleValidationException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        Map<String, String> body = response.getBody();
        assertTrue(body.containsKey("email"));
        assertEquals("Email is invalid", body.get("email"));

        assertTrue(body.containsKey("globalError"));
        assertEquals("Passwords do not match", body.get("globalError"));
    }

    @Test
    void handleUserNotFound_ShouldReturnNotFoundStatus() {
        String msg = "User with ID 123 not found";
        UsersNotFoundException ex = new UsersNotFoundException(msg);

        ResponseEntity<String> response = globalExceptionHandler.handleUserNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(msg, response.getBody());
    }

    @Test
    void userAlreadyExists_ShouldReturnConflictStatus() {
        String msg = "User email already registered";
        UserAlreadyExistsException ex = new UserAlreadyExistsException(msg);

        ResponseEntity<String> response = globalExceptionHandler.userAlreadyExists(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(msg, response.getBody());
    }
}
